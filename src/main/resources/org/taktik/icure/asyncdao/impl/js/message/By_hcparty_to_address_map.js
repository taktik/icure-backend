map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc.delegations && Object.keys(doc.delegations).length) {
    var addresses = (doc.toAddresses && doc.toAddresses.length) || (doc.invoiceIds && doc.invoiceIds.length) ? {} : {'INBOX': 1};
    if (doc.toAddresses) {
      doc.toAddresses.forEach(function (a) {
        addresses[a] = 1;
      });
    }
    Object.keys(doc.delegations).forEach(function (k) {
      Object.keys(addresses).forEach(function (a) {
        emit([k, a, -doc.received], doc._id);
      });
    });
  }
};
