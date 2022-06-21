map = function (doc) {
	if (doc.java_type === 'org.taktik.icure.entities.objectstorage.ObjectStorageTask' && !doc.deleted) {
		emit([doc.documentId, doc.attachmentId], doc._id)
	}
};
