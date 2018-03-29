map = function (doc) {
	if (doc.java_type === 'org.taktik.icure.entities.Patient' && doc.mergeToPatientId) {
        emit(doc.deleted, doc.mergeToPatientId);
    }
};
