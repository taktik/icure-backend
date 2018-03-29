map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc.delegations && doc.invoiceIds && doc.invoiceIds.length) {
    doc.invoiceIds.forEach(function(i) { emit(i, 1); });
  }
};
