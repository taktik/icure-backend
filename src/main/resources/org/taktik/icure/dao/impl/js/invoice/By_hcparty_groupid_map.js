map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted) {
    if (doc.groupId) {
      Object.keys(doc.delegations).forEach(function (k) {
        emit([k, doc.groupId], doc._id)
      });
    }
  }
};
