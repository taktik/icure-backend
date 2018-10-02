package org.taktik.icure.dao.replicator;

import com.google.common.collect.ImmutableList;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import kotlin.Unit;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.taktik.couchdb.Change;
import org.taktik.couchdb.CouchDbInstance;
import org.taktik.icure.entities.Group;
import org.taktik.icure.entities.base.StoredDocument;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.taktik.icure.dao.replicator.NewGroupObserver.IS_SYNC_DEAD_TIMEOUT;

/**
 * @author Bernard Paulus - 14/03/2017
 */
public abstract class AbstractReplicator<T extends StoredDocument> implements FilteredReplicator {
	private static final Logger log = LoggerFactory.getLogger(AbstractReplicator.class);

	private HazelcastInstance hazelcast;
	private IMap<String, ReplicatorJobStatus> replicatorJobsStatusesByGroupId;

	@Value("${icure.couchdb.username}")
	protected String couchDbUsername;
	@Value("${icure.couchdb.password}")
	protected String couchDbPassword;
	@Value("${icure.couchdb.prefix}")
	protected String couchDbPrefix;
	@Value("${icure.couchdb.url}")
	protected String couchDbUrl;

	protected HttpClient httpClient = null;

	public AbstractReplicator(HazelcastInstance hazelcast) {
		this.hazelcast = hazelcast;
	}

	protected abstract List<String> getAllIds(String groupId);

	protected abstract Class<T> getEntityType();

	protected abstract void prepareReplication(Group group);

	protected abstract void replicate(Group group, List<String> entityIds);

	@PostConstruct
	public void init() {
		replicatorJobsStatusesByGroupId = hazelcast.getMap(getClass().getCanonicalName() + ".replicatorJobsStatusesByGroupId");
	}

	@Override
	public CompletableFuture<Boolean> startReplication(Group replicatedGroup, SslContextFactory sslContextFactory) {
		// should check if a replication hasn't been started or if group has been updated
		GroupDBUrl groupDb = new GroupDBUrl(couchDbUrl);
		ReplicatorJobStatus jobStatus = tryToGetJob(replicatedGroup);
		if (jobStatus == null) {
			log.info("Someone else is busy with " + replicatedGroup.getId());
			return CompletableFuture.completedFuture(true); //It is a success because someone else is taking care of this replication... We do not need to mark it as pending
		}
		synchronized (this) {
			if (httpClient == null) {
				log.info("Init http client from " + replicatedGroup.getId());
				httpClient = new HttpClient(sslContextFactory);
				try {
					httpClient.setMaxConnectionsPerDestination(65535);
					httpClient.start();
				} catch (Exception e) {
					log.error("Cannot start HTTP client");
					try {
						httpClient.stop();
					} catch (Exception ignored) {
						return CompletableFuture.completedFuture(false); //retry
					} finally {
						httpClient = null;
					}
					return CompletableFuture.completedFuture(false); //retry
				}
			}
		}
		CouchDbInstance groupDbInstance = new CouchDbInstance(httpClient, URI.create(groupDb.getInstanceUrl(replicatedGroup)), groupDb.getDbName(replicatedGroup), replicatedGroup.getId(), replicatedGroup.getPassword());
		log.info("Create instance for " + replicatedGroup.getId());
		return groupDbInstance.exists().thenApply((Boolean result) -> {
			log.info("Db exists for " + replicatedGroup.getId());
			if (result) {
				prepareReplication(replicatedGroup);
				result = replicateExistingData(replicatedGroup); //This is sort of blocking
				if (result) {
					observeChanges(replicatedGroup, jobStatus, groupDbInstance);
				}
			}
			return result;
		});
	}

	private void observeChanges(Group replicatedGroup, ReplicatorJobStatus jobStatus, CouchDbInstance groupDbInstance) {
		groupDbInstance.changes(jobStatus.getSeq() != null ? jobStatus.getSeq() : "now", (Change c) -> {
			observe(c, replicatedGroup);
			return Unit.INSTANCE;
		}, () -> {
			replicatorJobsStatusesByGroupId.put(replicatedGroup.getId(), replicatorJobsStatusesByGroupId.get(replicatedGroup.getId()).timestamp(System.currentTimeMillis()));
			return Unit.INSTANCE;
		}).whenComplete((unit, throwable) -> observeChanges(replicatedGroup, jobStatus, groupDbInstance));
	}

	private boolean replicateExistingData(Group group) {
		long startTime = System.currentTimeMillis();
		try {
			List<String> allIds = this.getAllIds(group.getId());
			replicate(group, allIds);
		} catch (Exception e) {
			log.error("Exception during initial replication : {} for group {} ({}) ", e.getLocalizedMessage(), group.getId(), group.getName());
			return false;
		}
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		log.info("Initial replication completed for {} in {} ms", group.getId(), duration);
		return true;
	}

	public void observe(Change change, Group group) {
		if (change.getDoc() == null) {
			return;
		}
		if (getEntityType().getCanonicalName().equals(change.getDoc().get("java_type"))) {
			ImmutableList<String> entityIds = ImmutableList.of((String) change.getDoc().get("_id"));
			try {
				replicate(group, entityIds);
			} catch (Exception e) {
				log.error("replication failed: {}", e.getLocalizedMessage());
				return;
			}
			replicatorJobsStatusesByGroupId.put(group.getId(), new ReplicatorJobStatus(System.currentTimeMillis(), change.getSeq()));
		}
	}

	private ReplicatorJobStatus tryToGetJob(Group group) {
		final ReplicatorJobStatus jobStatus = new ReplicatorJobStatus(System.currentTimeMillis(), null);
		ReplicatorJobStatus jobStatusInCluster = replicatorJobsStatusesByGroupId.putIfAbsent(group.getId(), jobStatus);
		if (jobStatusInCluster == null) {
			return jobStatus;
		} else {
			if (jobStatus.getTimestamp() - jobStatusInCluster.getTimestamp() <= IS_SYNC_DEAD_TIMEOUT /* the current job is not dead */ || !replicatorJobsStatusesByGroupId.replace(group.getId(), jobStatusInCluster, jobStatus)) /* It has already been acquired in the meantime */ {
				return null;
			} else {
				return jobStatus.seq(jobStatusInCluster.getSeq());
			}
		}
	}

}
