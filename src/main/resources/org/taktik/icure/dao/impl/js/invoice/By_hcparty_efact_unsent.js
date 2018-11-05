function isInvoiceUnsent(doc) {
  if(doc.invoicingCodes.length === 0) return false;
  return doc.invoicingCodes.some(function(code){
    return !(code.pending || code.canceled || code.accepted || code.resent || code.archived);
  });
}

map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && doc.sentMediumType === "efact" && doc.invoiceType ==="mutualfund" && isInvoiceUnsent(doc) && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
      emit([k, doc.invoiceDate], doc._id)
		});
	}
};