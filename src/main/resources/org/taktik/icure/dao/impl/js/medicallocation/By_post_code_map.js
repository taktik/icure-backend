map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.MedicalLocation' && !doc.deleted && doc.address) {
		emit(doc.address.postalCode, doc._id);
	}
};
