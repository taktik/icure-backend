map = function(doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) {
		if (doc.delegations) {
			Object.keys(doc.delegations).forEach(function(k){
				var ssin;
				if(doc.ssin) {
				  ssin = doc.ssin.replace(new RegExp('\\s', 'g'), '').replace(new RegExp('\\W', 'g'), '');
				}
				ssin = ssin || null;

				emit([k, ssin], doc._id);
			});
		}
	}
};
