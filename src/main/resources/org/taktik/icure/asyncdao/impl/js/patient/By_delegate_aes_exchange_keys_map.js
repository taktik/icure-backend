function(doc) {
	if (doc.java_type === 'org.taktik.icure.entities.Patient' && !doc.deleted && (doc.hcPartyKeys || doc.aesExchangeKeys)) {
		if (doc.aesExchangeKeys) {
			Object.values(doc.aesExchangeKeys).forEach(function (ks) {
				Object.keys(ks).forEach(function (k) {
					//Emit for each delegate a tuple: thisDoc.id and the keys encrypted for the delegate
					emit(k, [doc._id].concat(ks[k].slice(1)));
				});
			});
		}

		if (!doc.aesExchangeKeys || !Object.keys(doc.aesExchangeKeys).includes(doc.publicKey)) {
			Object.keys(doc.hcPartyKeys).forEach(function (k) {
				//Emit for each delegate a tuple: thisDoc.id and the keys encrypted for the delegate
				emit(k, [doc._id].concat(doc.hcPartyKeys[k].slice(1)));
			});
		}
	}
}
