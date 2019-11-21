map = function(doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) {
		if (doc.delegations) {
			Object.keys(doc.delegations).forEach(function(k){
				var ssin = doc.ssin ? doc.ssin.replace(new RegExp('\\s', 'g'), '').replace(new RegExp('\\W', 'g'), '') : null;
				emit([k, ssin], doc._id);
			});
		}
	}
};
