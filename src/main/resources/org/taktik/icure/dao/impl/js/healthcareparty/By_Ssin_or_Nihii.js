map = function(doc) {
	if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) {
		if (doc.ssin) {
			emit(doc.ssin, doc._id)
		}
		if (doc.nihii) {
			emit(doc.nihii, doc._id)
		}
	}
}
