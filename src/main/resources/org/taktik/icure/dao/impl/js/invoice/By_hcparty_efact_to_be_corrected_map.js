function isInvoiceBad(doc) {
  if(!doc.sentDate || doc.invoicingCodes.length === 0) return false;
  return doc.invoicingCodes.every(function(code){
    return code.status && (code.status & 32) === 32
  });
}

map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && doc.sentMediumType === "efact" && doc.invoiceType ==="mutualfund" && isInvoiceBad(doc) && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
      emit([k, doc.invoiceDate], doc._id)
		});
	}
};
