map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.User' && !doc.deleted && !!doc.healthcarePartyId) {
            emit(doc.healthcarePartyId, doc._id)
    }
};
