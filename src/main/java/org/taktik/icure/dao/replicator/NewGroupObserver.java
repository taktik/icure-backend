package org.taktik.icure.dao.replicator;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ILock;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import kotlin.Unit;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.taktik.couchdb.Change;
import org.taktik.couchdb.CouchDbInstance;
import org.taktik.icure.dao.GenericDAO;
import org.taktik.icure.dao.GroupDAO;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.base.StoredDocument;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * starts wired replicators on new group
 *
 * @author Bernard Paulus - 13/03/2017
 */
public class NewGroupObserver {
	private static final Logger log = LoggerFactory.getLogger(NewGroupObserver.class);
	public static final int IS_SYNC_DEAD_TIMEOUT = 10 * 60 * 1000; // ms

	@Value("${icure.couchdb.username}")
	private String couchDbUsername;
	@Value("${icure.couchdb.password}")
	private String couchDbPassword;
	@Value("${icure.couchdb.prefix}")
	private String couchDbPrefix;
	@Value("${icure.couchdb.url}")
	private String couchDbUrl;

	private List<FilteredReplicator> replicators;
	private GroupDAO groupDAO;
	private HazelcastInstance hazelcast;
	private SslContextFactory sslContextFactory;
	private List<GenericDAO> allDaos;
	private final Map<String, PendingReplication> pendingReplications = new HashMap<>();

	private HttpClient httpClient = null;

	public NewGroupObserver(HazelcastInstance hazelcast, SslContextFactory sslContextFactory, GroupDAO groupDAO, List<FilteredReplicator> replicators, List<GenericDAO> allDaos) {
		this.groupDAO = groupDAO;
		this.hazelcast = hazelcast;
		this.replicators = replicators;
		this.sslContextFactory = sslContextFactory;
		this.allDaos = allDaos;
	}

	@PostConstruct
	public void init() {
		ILock lock = hazelcast.getLock(getClass().getCanonicalName() + ".lock");
		lock.lock();
		try {
			final IAtomicReference<Boolean> isExecutorStarted = hazelcast.getAtomicReference(getClass().getCanonicalName() + ".isExecutorStarted");
			if (!Boolean.TRUE.equals(isExecutorStarted.get())) {
				IScheduledExecutorService scheduledExecutor = hazelcast.getScheduledExecutorService(getClass().getCanonicalName() + ".scheduledExecutor");
				scheduledExecutor.scheduleAtFixedRate(new ObserverStarter(), 1, 10, TimeUnit.MINUTES);
				isExecutorStarted.set(Boolean.TRUE);

				log.info("Captured lock and starting group observer");
				this.ensureObserverStarted();
			}
		} finally {
			lock.unlock();
		}
	}

	public void ensureObserverStarted() {
		IAtomicLong lastHeartBeat = getLastHeartBeat();
		long time = System.currentTimeMillis();
		if (lastHeartBeat.get() == 0) {
			lastHeartBeat.set(time);
			// init
			startObserver();
			List<Group> groups = groupDAO.getAll();
			groups.sort(Comparator.comparing(StoredDocument::getId));
			CompletableFuture<Boolean> f = CompletableFuture.completedFuture(true);
			for (Group group : groups) {
				//Compose them so they do not start all at the same time
				f=f.thenCompose(b -> prepareDesignDocumentsAndStartReplications(group)).exceptionally((t)->{log.error("An error occurred during Observers start",t); return false; });
			}
		} else if (lastHeartBeat.get() < time - IS_SYNC_DEAD_TIMEOUT) {
			startObserver();
		}
	}

	private void startObserver() {
		startHttpClient();

		CouchDbInstance dbInstance = new CouchDbInstance(httpClient, URI.create(couchDbUrl), couchDbPrefix + "-config", couchDbUsername, couchDbPassword);
		dbInstance.changes("now",
			(Change change) -> {
				getLastHeartBeat().set(System.currentTimeMillis());

				if (change.getDoc() != null && Group.class.getCanonicalName().equals(change.getDoc().get("java_type"))) {
					Group group = groupDAO.get((String) change.getDoc().get("_id"));
					prepareDesignDocumentsAndStartReplications(group);
				}
				return Unit.INSTANCE;
			},
			() -> {
				getLastHeartBeat().set(System.currentTimeMillis());

				Set<Map.Entry<String, PendingReplication>> entries;
				synchronized (this.pendingReplications) {
					entries = new HashSet<>(this.pendingReplications.entrySet());
				}
				entries.forEach(entry -> {
					PendingReplication p = entry.getValue();
					if (p != null) {
						p.getReplicator().startReplication(p.getGroup(), this.sslContextFactory).thenAccept(b -> {
							if (b) {
								synchronized (this.pendingReplications) {
									this.pendingReplications.remove(entry.getKey());
								}
							}
						});
					}
				});
				return Unit.INSTANCE;
			}).whenComplete((unit, throwable) -> {this.startObserver();});
	}

	private void startHttpClient() {
		synchronized (this) {
			if (httpClient == null) {
				httpClient = new HttpClient(this.sslContextFactory);

				try {
					httpClient.setMaxConnectionsPerDestination(65535);
					httpClient.start();
				} catch (Exception e) {
					log.error("Cannot start HTTP client");
					try {
						httpClient.stop();
					} catch (Exception ignored) {
						throw new RuntimeException();
					} finally {
						httpClient = null;
					}
					throw new RuntimeException();
				}
			}
		}
	}

	private IAtomicLong getLastHeartBeat() {
		return hazelcast.getAtomicLong(getClass().getCanonicalName() + ".lastHeartBeat");
	}

	private CompletableFuture<Boolean> prepareDesignDocumentsAndStartReplications(Group group) {
		log.info("Starting replications for "+group.getId());

		allDaos.forEach(d->d.initStandardDesignDocument(group.getId()));
		log.info("Standard docs initialised for "+group.getId());

		CompletableFuture<FilteredReplicator> f = CompletableFuture.completedFuture(null);
		for (FilteredReplicator r : replicators) {
			f = f.thenCompose(previousReplicator -> {
				queueReplicatorIfNeeded(group, previousReplicator);
				return r.startReplication(group, this.sslContextFactory).thenApply(succeeded -> succeeded ? null : r);
			}).exceptionally(t->{log.error("Cannot start replicator ",t); return r;});
		}
		return f.thenApply(lastReplicator -> { queueReplicatorIfNeeded(group, lastReplicator); return true; });
	}

	private void queueReplicatorIfNeeded(Group group, FilteredReplicator previousReplicator) {
		try {
			if (previousReplicator != null) {
				synchronized (this.pendingReplications) {
					log.info("Queuing pending replication for " + group.getId());
					this.pendingReplications.put(group.getId(), new PendingReplication(group, previousReplicator));
				}
			}
		} catch (Exception e) {
			log.error("Error while queuing replication",e);
		}
	}

	private class PendingReplication {
		FilteredReplicator replicator;
		Group group;

		public PendingReplication(Group group, FilteredReplicator replicator) {

			this.group = group;
			this.replicator = replicator;
		}

		public FilteredReplicator getReplicator() {
			return replicator;
		}

		public void setReplicator(FilteredReplicator replicator) {
			this.replicator = replicator;
		}

		public Group getGroup() {
			return group;
		}

		public void setGroup(Group group) {
			this.group = group;
		}
	}

}
