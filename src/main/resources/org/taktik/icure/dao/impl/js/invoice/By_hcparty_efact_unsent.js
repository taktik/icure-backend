function isInvoiceUnsent(doc) {
  if(!!doc.sentDate) return false;
  doc.invoicingCodes.forEach(function(code){
    if(code.status >= 4) return false;
  });
  return true;
}

map = function (doc) {
	if (doc.java_type == 'org.taktik.icure.entities.Invoice' && doc.sentMediumType === "efact" && doc.invoiceType ==="mutualfund" && isInvoiceUnsent(doc) && doc.delegations && Object.keys(doc.delegations).length) {
		Object.keys(doc.delegations).forEach(function (k) {
            emit([k, doc.invoiceDate], doc._id)
		});
	}
};
