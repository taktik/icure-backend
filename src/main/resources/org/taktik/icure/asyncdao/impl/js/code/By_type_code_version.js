map = function(doc) {
    if (doc.java_type == 'org.taktik.icure.entities.base.Code' && !doc.deleted) {
        emit([doc.type, doc.code, doc.version], doc._id);
    }
};
