map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Filter' && !doc.deleted && doc.userId && doc.filterEntity) {
		//emit(doc.date, doc._id);
		emit([doc.filterEntity, doc.userId], doc._id);
	}
};
