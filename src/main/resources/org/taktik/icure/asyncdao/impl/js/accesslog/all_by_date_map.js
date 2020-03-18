map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.AccessLog' && !doc.deleted && doc.date) {
		emit(doc.date, doc._id);
	}
};