map = function(doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Property' && !doc.deleted && doc.type.identifier) {
        emit(doc.type.identifier,doc._id);
    }
};