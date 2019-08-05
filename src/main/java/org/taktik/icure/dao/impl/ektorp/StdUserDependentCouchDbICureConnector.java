package org.taktik.icure.dao.impl.ektorp;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.digest.DigestUtils;
import org.ektorp.AttachmentInputStream;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbInfo;
import org.ektorp.DesignDocInfo;
import org.ektorp.DocumentOperationResult;
import org.ektorp.Options;
import org.ektorp.Page;
import org.ektorp.PageRequest;
import org.ektorp.PurgeResult;
import org.ektorp.ReplicationStatus;
import org.ektorp.Revision;
import org.ektorp.StreamingChangesResult;
import org.ektorp.StreamingViewResult;
import org.ektorp.UpdateHandlerRequest;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.ObjectMapperFactory;
import org.ektorp.impl.StdCouchDbInstance;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.properties.CouchDbProperties;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class StdUserDependentCouchDbICureConnector implements CouchDbICureConnector {
    private CouchDbProperties couchDbProperties;
    private LoadingCache<CouchDbConnectorReference, CouchDbICureConnector> connectors = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(240, TimeUnit.MINUTES)
            .build(new CacheLoader<CouchDbConnectorReference, CouchDbICureConnector>() {
                @Override
                public CouchDbICureConnector load(@NotNull CouchDbConnectorReference key) throws Exception {
                    String name = StdUserDependentCouchDbICureConnector.this.databaseName.replaceAll(couchDbPrefix, "icure-" + key.groupId);

                    CouchDbInstance dbInstance;

                    if (StdUserDependentCouchDbICureConnector.this.couchDbProperties.getUrl().equals(key.dbInstanceUrl)) {
                        dbInstance = StdUserDependentCouchDbICureConnector.this.dbInstance;
                    } else {
                        dbInstance = otherInstances.get(key.dbInstanceUrl);
                        if (dbInstance == null) {
                            otherInstances.put(key.dbInstanceUrl, dbInstance = new StdCouchDbInstance(new StdHttpClient.Builder()
                                    .maxConnections(couchDbProperties.getMaxConnections())
                                    .socketTimeout(couchDbProperties.getSocketTimeout())
                                    .username(couchDbProperties.getUsername())
                                    .password(couchDbProperties.getPassword())
                                    .url(key.dbInstanceUrl)
                                    .build()));
                        }
                    }

                    boolean dbExists = dbInstance.checkIfDbExists(name);
                    if (!dbExists) {
                        if (key.allowFallback) {
                            return fallbackConnector;
                        } else {
                            throw new IllegalArgumentException("Group "+key.groupId+" does not exist on "+key.dbInstanceUrl);
                        }
                    }

                    StdCouchDbICureConnector connector = om == null ? new StdCouchDbICureConnector(name, dbInstance) : new StdCouchDbICureConnector(name, dbInstance, om);

                    //Might want to emit some event that can be caught by the DAOs to init the standard documents

                    return connector;
                }
            });

    private CouchDbICureConnector fallbackConnector;

    @Value("${icure.couchdb.prefix}")
    private String couchDbPrefix;

    private final String uuid;

    @Autowired
    @Lazy
    private SessionLogic sessionLogic;

    public void setSessionLogic(@Lazy SessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    private String databaseName;
    private CouchDbInstance dbInstance;
    private boolean allowAnonymousAccess = false;
    private Map<String, CouchDbInstance> otherInstances = new HashMap<>();
    private ObjectMapperFactory om;

    public String getBaseDatabaseName() {
        return databaseName;
    }

    public StdUserDependentCouchDbICureConnector(String databaseName, CouchDbInstance dbInstance) {
        this.databaseName = databaseName;
        this.dbInstance = dbInstance;

        fallbackConnector = new StdCouchDbICureConnector(databaseName, dbInstance);
        uuid = DigestUtils.sha256Hex(databaseName+':'+dbInstance.getUuid());
    }

    public StdUserDependentCouchDbICureConnector(String databaseName, CouchDbInstance dbInstance, CouchDbProperties couchDbProperties, boolean allowAnonymousAccess) {
        this.databaseName = databaseName;
        this.dbInstance = dbInstance;
        this.allowAnonymousAccess = allowAnonymousAccess;
        this.couchDbProperties = couchDbProperties;

        fallbackConnector = new StdCouchDbICureConnector(databaseName, dbInstance);
        uuid = DigestUtils.sha256Hex(databaseName+':'+dbInstance.getUuid());
    }

    public StdUserDependentCouchDbICureConnector(String databaseName, CouchDbInstance dbInstance, ObjectMapperFactory om) {
        this.databaseName = databaseName;
        this.dbInstance = dbInstance;
        this.om = om;

        fallbackConnector = new StdCouchDbICureConnector(databaseName, dbInstance, om);
        uuid = DigestUtils.sha256Hex(databaseName+':'+dbInstance.getUuid());
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void initSystemDesignDocument() {
        this.getCurrentUserRealConnector().initSystemDesignDocument();
    }

    @Override
    public CouchDbICureConnector getCurrentUserRealConnector() {
        if (sessionLogic != null && sessionLogic.getCurrentSessionContext() != null) {
            User user = sessionLogic.getCurrentSessionContext().getUser();
            if (user != null && user.getGroupId() != null) {
                return getCouchDbICureConnector(user.getGroupId(),  sessionLogic.getCurrentSessionContext().getDbInstanceUrl(), true);
            } else {
                return fallbackConnector;
            }
        }
        if (allowAnonymousAccess) {
            return fallbackConnector;
        }
        throw new IllegalArgumentException("No session available");
    }

    @Override
    public CouchDbICureConnector getCouchDbICureConnector(String groupId, String dbInstanceUrl, boolean allowFallback) {
        try {
            if (groupId == null && !allowFallback) { throw new IllegalArgumentException("Missing group id"); }
            return groupId==null ? fallbackConnector : connectors.get(new CouchDbConnectorReference(dbInstanceUrl == null ? this.couchDbProperties.getUrl() : dbInstanceUrl, groupId, allowFallback));
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void addToBulkBuffer(Object o) {
        getCurrentUserRealConnector().addToBulkBuffer(o);
    }

    @Override
    public List<DocumentOperationResult> flushBulkBuffer() {
        return getCurrentUserRealConnector().flushBulkBuffer();
    }

    @Override
    public void clearBulkBuffer() {
        getCurrentUserRealConnector().clearBulkBuffer();
    }

    @Override
    public void create(String id, Object o) {
        getCurrentUserRealConnector().create(id, o);
    }

    @Override
    public void create(Object o) {
        getCurrentUserRealConnector().create(o);
    }

    @Override
    public void update(Object o) {
        getCurrentUserRealConnector().update(o);
    }

    @Override
    public String delete(Object o) {
        return getCurrentUserRealConnector().delete(o);
    }

    @Override
    public String delete(String id, String revision) {
        return getCurrentUserRealConnector().delete(id, revision);
    }

    @Override
    public String copy(String sourceDocId, String targetDocId) {
        return getCurrentUserRealConnector().copy(sourceDocId, targetDocId);
    }

    @Override
    public String copy(String sourceDocId, String targetDocId, String targetRevision) {
        return getCurrentUserRealConnector().copy(sourceDocId, targetDocId, targetRevision);
    }

    @Override
    public PurgeResult purge(Map<String, List<String>> revisionsToPurge) {
        return getCurrentUserRealConnector().purge(revisionsToPurge);
    }

    @Override
    public <T> T get(Class<T> c, String id) {
        return getCurrentUserRealConnector().get(c, id);
    }

    @Override
    public <T> T get(Class<T> c, String id, Options options) {
        return getCurrentUserRealConnector().get(c, id, options);
    }

    @Override
    public <T> T find(Class<T> c, String id) {
        return getCurrentUserRealConnector().find(c, id);
    }

    @Override
    public <T> T find(Class<T> c, String id, Options options) {
        return getCurrentUserRealConnector().find(c, id, options);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public <T> T get(Class<T> c, String id, String rev) {
        return getCurrentUserRealConnector().get(c, id, rev);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public <T> T getWithConflicts(Class<T> c, String id) {
        return getCurrentUserRealConnector().getWithConflicts(c, id);
    }

    @Override
    public boolean contains(String id) {
        return getCurrentUserRealConnector().contains(id);
    }

    @Override
    public InputStream getAsStream(String id) {
        return getCurrentUserRealConnector().getAsStream(id);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public InputStream getAsStream(String id, String rev) {
        return getCurrentUserRealConnector().getAsStream(id, rev);
    }

    @Override
    public InputStream getAsStream(String id, Options options) {
        return getCurrentUserRealConnector().getAsStream(id, options);
    }

    @Override
    public List<Revision> getRevisions(String id) {
        return getCurrentUserRealConnector().getRevisions(id);
    }

    @Override
    public String getCurrentRevision(String id) {
        return getCurrentUserRealConnector().getCurrentRevision(id);
    }

    @Override
    public AttachmentInputStream getAttachment(String id, String attachmentId) {
        return getCurrentUserRealConnector().getAttachment(id, attachmentId);
    }

    @Override
    public AttachmentInputStream getAttachment(String id, String attachmentId, String revision) {
        return getCurrentUserRealConnector().getAttachment(id, attachmentId, revision);
    }

    @Override
    public String createAttachment(String docId, AttachmentInputStream data) {
        return getCurrentUserRealConnector().createAttachment(docId, data);
    }

    @Override
    public String createAttachment(String docId, String revision, AttachmentInputStream data) {
        return getCurrentUserRealConnector().createAttachment(docId, revision, data);
    }

    @Override
    public String deleteAttachment(String docId, String revision, String attachmentId) {
        return getCurrentUserRealConnector().deleteAttachment(docId, revision, attachmentId);
    }

    @Override
    public List<String> getAllDocIds() {
        return getCurrentUserRealConnector().getAllDocIds();
    }

    @Override
    public <T> List<T> queryView(ViewQuery query, Class<T> type) {
        return getCurrentUserRealConnector().queryView(query, type);
    }

    @Override
    public List<ComplexKey> queryViewForComplexKeys(ViewQuery query) {
        return getCurrentUserRealConnector().queryViewForComplexKeys(query);
    }

    @Override
    public <T> Page<T> queryForPage(ViewQuery query, PageRequest pr, Class<T> type) {
        return getCurrentUserRealConnector().queryForPage(query, pr, type);
    }

    @Override
    public List<String> queryViewForIds(final ViewQuery query) {
        return getCurrentUserRealConnector().queryViewForIds(query);
    }

    @Override
    public ViewResult queryView(ViewQuery query) {
        return getCurrentUserRealConnector().queryView(query);
    }

    @Override
    public StreamingViewResult queryForStreamingView(ViewQuery query) {
        return getCurrentUserRealConnector().queryForStreamingView(query);
    }

    @Override
    public InputStream queryForStream(ViewQuery query) {
        return getCurrentUserRealConnector().queryForStream(query);
    }

    @Override
    public void createDatabaseIfNotExists() {
        getCurrentUserRealConnector().createDatabaseIfNotExists();
    }

    @Override
    public String getDatabaseName() {
        return getCurrentUserRealConnector().getDatabaseName();
    }

    @Override
    public String path() {
        return getCurrentUserRealConnector().path();
    }

    @Override
    public HttpClient getConnection() {
        return getCurrentUserRealConnector().getConnection();
    }

    @Override
    public DbInfo getDbInfo() {
        return getCurrentUserRealConnector().getDbInfo();
    }

    @Override
    public DesignDocInfo getDesignDocInfo(String designDocId) {
        return getCurrentUserRealConnector().getDesignDocInfo(designDocId);
    }

    @Override
    public void compact() {
        getCurrentUserRealConnector().compact();
    }

    @Override
    public void compactViews(String designDocumentId) {
        getCurrentUserRealConnector().compactViews(designDocumentId);
    }

    @Override
    public void cleanupViews() {
        getCurrentUserRealConnector().cleanupViews();
    }

    @Override
    public int getRevisionLimit() {
        return getCurrentUserRealConnector().getRevisionLimit();
    }

    @Override
    public void setRevisionLimit(int limit) {
        getCurrentUserRealConnector().setRevisionLimit(limit);
    }

    @Override
    public ReplicationStatus replicateFrom(String source) {
        return getCurrentUserRealConnector().replicateFrom(source);
    }

    @Override
    public ReplicationStatus replicateFrom(String source, Collection<String> docIds) {
        return getCurrentUserRealConnector().replicateFrom(source, docIds);
    }

    @Override
    public ReplicationStatus replicateTo(String target) {
        return getCurrentUserRealConnector().replicateTo(target);
    }

    @Override
    public ReplicationStatus replicateTo(String target, Collection<String> docIds) {
        return getCurrentUserRealConnector().replicateTo(target, docIds);
    }

    @Override
    public List<DocumentOperationResult> executeBulk(InputStream inputStream) {
        return getCurrentUserRealConnector().executeBulk(inputStream);
    }

    @Override
    public List<DocumentOperationResult> executeAllOrNothing(InputStream inputStream) {
        return getCurrentUserRealConnector().executeAllOrNothing(inputStream);
    }

    @Override
    public List<DocumentOperationResult> executeBulk(Collection<?> objects) {
        return getCurrentUserRealConnector().executeBulk(objects);
    }

    @Override
    public List<DocumentOperationResult> executeAllOrNothing(Collection<?> objects) {
        return getCurrentUserRealConnector().executeAllOrNothing(objects);
    }

    @Override
    public List<DocumentChange> changes(ChangesCommand cmd) {
        return getCurrentUserRealConnector().changes(cmd);
    }

    @Override
    public StreamingChangesResult changesAsStream(ChangesCommand cmd) {
        return getCurrentUserRealConnector().changesAsStream(cmd);
    }

    @Override
    public ChangesFeed changesFeed(ChangesCommand cmd) {
        return getCurrentUserRealConnector().changesFeed(cmd);
    }

    @Override
    public String callUpdateHandler(String designDocID, String function, String docId) {
        return getCurrentUserRealConnector().callUpdateHandler(designDocID, function, docId);
    }

    @Override
    public String callUpdateHandler(String designDocID, String function, String docId, Map<String, String> params) {
        return getCurrentUserRealConnector().callUpdateHandler(designDocID, function, docId, params);
    }

    @Override
    public <T> T callUpdateHandler(final UpdateHandlerRequest req, final Class<T> c) {
        return getCurrentUserRealConnector().callUpdateHandler(req, c);
    }

    @Override
    public String callUpdateHandler(final UpdateHandlerRequest req) {
        return getCurrentUserRealConnector().callUpdateHandler(req);
    }

    @Override
    public void ensureFullCommit() {
        getCurrentUserRealConnector().ensureFullCommit();
    }

    @Override
    public void updateMultipart(String id, InputStream stream, String boundary, long length, Options options) {
        getCurrentUserRealConnector().updateMultipart(id, stream, boundary, length, options);
    }

    @Override
    public void update(String id, InputStream document, long length, Options options) {
        getCurrentUserRealConnector().update(id, document, length, options);
    }

    @Override
    public <T> List<CouchKeyValue<T>> queryViewWithKeys(ViewQuery query, Class<T> type) {
        return getCurrentUserRealConnector().queryViewWithKeys(query, type);
    }

    @Override
    public CouchDbICureConnector getFallbackConnector() {
        return fallbackConnector;
    }

    private class CouchDbConnectorReference {
        private final String dbInstanceUrl;
        private final String groupId;
        private final boolean allowFallback;

        public CouchDbConnectorReference(String dbInstanceUrl, String groupId, boolean allowFallback) {
            this.dbInstanceUrl = dbInstanceUrl;
            this.groupId = groupId;
            this.allowFallback = allowFallback;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CouchDbConnectorReference that = (CouchDbConnectorReference) o;
            return allowFallback == that.allowFallback &&
                    Objects.equals(dbInstanceUrl, that.dbInstanceUrl) &&
                    Objects.equals(groupId, that.groupId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dbInstanceUrl, groupId, allowFallback);
        }
    }
}
