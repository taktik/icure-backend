map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Device' && !doc.deleted) {
        if(doc.responsible) {
            emit(doc.responsible, doc._id)
        }
    }
};
