function hasInvoicingCodes(doc) {
  return doc.invoicingCodes.length > 0;
}

map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && doc.sentMediumType === "efact" && doc.invoiceType ==="mutualfund" && hasInvoicingCodes(doc) && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
            emit([k, doc.invoicingCodes[0].pending, doc.invoicingCodes[0].canceled, doc.invoicingCodes[0].accepted, doc.invoicingCodes[0].resent, doc.invoicingCodes[0].archived, doc.invoiceDate], doc._id)
		});
	}
};