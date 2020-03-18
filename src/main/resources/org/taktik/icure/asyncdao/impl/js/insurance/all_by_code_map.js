map = function(doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Insurance' && !doc.deleted && doc.code) {
		doc.code.split(',').forEach(function(key) {emit(key, doc._id);});
	}
};
