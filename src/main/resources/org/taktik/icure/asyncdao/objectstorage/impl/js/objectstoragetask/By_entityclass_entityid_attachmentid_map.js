map = function (doc) {
	if (doc.java_type === 'org.taktik.icure.entities.objectstorage.ObjectStorageTask' && !doc.deleted) {
		emit([doc.entityClassName, doc.entityId, doc.attachmentId], doc._id)
	}
};
