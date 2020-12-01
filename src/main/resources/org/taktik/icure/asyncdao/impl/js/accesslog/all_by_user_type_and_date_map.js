map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.AccessLog' && !doc.deleted && doc.date && doc.user) {
		emit([doc.user,doc.accessType,doc.date], doc._id);
	}
};