map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && doc.delegations && Object.keys(doc.delegations).length && doc.invoicingCodes) {
    var statuses = {};
    doc.invoicingCodes.forEach(function(ic) {
      var status = ic.accepted?"accepted":ic.canceled?"canceled":ic.resent?"resent":ic.pending?"pending":null;
      if (status) {statuses[status] = 1}
    });
    if (Object.keys(statuses).length) {
      Object.keys(statuses).forEach(function (s) {
        Object.keys(doc.delegations).forEach(function (k) {
          emit([s, k, doc.sentDate], doc._id);
        });
      });
    } else {
      Object.keys(doc.delegations).forEach(function (k) {
        emit([null, k, doc.sentDate], doc._id);
      });
    }
  }
};
