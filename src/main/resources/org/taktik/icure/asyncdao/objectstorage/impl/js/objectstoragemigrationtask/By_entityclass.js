map = function (doc) {
	if (doc.java_type === 'org.taktik.icure.entities.objectstorage.ObjectStorageMigrationTask' && !doc.deleted) {
		emit(doc.entityClassName, doc._id)
	}
};
