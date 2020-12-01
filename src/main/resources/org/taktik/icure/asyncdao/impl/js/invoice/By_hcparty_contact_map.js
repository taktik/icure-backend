map = function (doc) {
	var emit_invoices = function (k, doc) {
		var ctsIds = {};
		doc.invoicingCodes.forEach(function(ic) {
			if (ic.contactId) {
				ctsIds[ic.contactId] = 1;
			}
		});
		Object.keys(ctsIds).forEach(function (cid) {
			emit([k, cid], doc._id);
		});
	};

	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
			emit_invoices(k, doc);
		});
	}
};
