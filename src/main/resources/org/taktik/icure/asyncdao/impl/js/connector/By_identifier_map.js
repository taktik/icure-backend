map = function(doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Connector' && !doc.deleted && doc.identifier) {
        emit(doc.identifier,doc._id);
    }
};