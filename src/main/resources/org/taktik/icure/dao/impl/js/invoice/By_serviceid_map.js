map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted) {
    doc.invoicingCodes.forEach(function (ic) {
      if (ic.serviceId) {
        emit(ic.serviceId, doc._id);
      }
    });
  }
};
