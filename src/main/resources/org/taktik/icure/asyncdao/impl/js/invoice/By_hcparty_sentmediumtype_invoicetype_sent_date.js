map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && !!doc.sentMediumType && !!doc.invoiceType && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
            emit([k, doc.sentMediumType, doc.invoiceType, !!doc.sentDate, doc.invoiceDate], doc._id)
		});
	}
};
