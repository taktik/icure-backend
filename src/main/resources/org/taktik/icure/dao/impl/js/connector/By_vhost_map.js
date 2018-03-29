map = function (doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Connector' && !doc.deleted && doc.virtualHosts && doc.virtualHosts.length) {
        for (var i = 0; i < doc.virtualHosts.length; i++) {
            emit(doc.virtualHosts[i], doc._id);
        }
    }
};
