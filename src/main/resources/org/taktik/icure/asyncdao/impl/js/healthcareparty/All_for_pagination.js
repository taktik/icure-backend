map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) {
        emit(doc.lastName, doc._id)
    }
}
