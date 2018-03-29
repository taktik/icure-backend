map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
      emit([k, doc.invoiceReference], doc._id);
		});
	}
};
