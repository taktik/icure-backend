map = function(doc) {
	if (doc.java_type === 'org.taktik.icure.entities.User' && !doc.deleted && !!doc.patientId) {
		emit(doc.patientId, doc._id)
	}
};
