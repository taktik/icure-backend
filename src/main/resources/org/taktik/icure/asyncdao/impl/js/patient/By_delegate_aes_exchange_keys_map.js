map = function(doc) {
	if (doc.java_type === 'org.taktik.icure.entities.Patient' && !doc.deleted && (doc.hcPartyKeys || doc.aesExchangeKeys)) {
		if (doc.aesExchangeKeys) {
			let aesPubKeys = Object.keys(doc.aesExchangeKeys);
			Object.values(doc.aesExchangeKeys).forEach(function (ks) {
				Object.keys(ks).forEach(function (k) {
					let delegateKeys = ks[k];
					Object.entries(delegateKeys).forEach(function ([delPub, delK]) {
						if (!aesPubKeys[delPub.slice(-12)]) {
							emit(k, [doc._id, delPub, delK]);
						}
					})
				});
			});
		}

		if (!doc.aesExchangeKeys || !Object.keys(doc.aesExchangeKeys).includes(doc.publicKey)) {
			Object.keys(doc.hcPartyKeys).forEach(function (k) {
				emit(k, [doc._id, doc.publicKey, doc.hcPartyKeys[k][1]]);
			});
		}
	}
}
