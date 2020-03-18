map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Invoice' && !doc.deleted && !doc.sentDate && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    Object.keys(doc.delegations).forEach(function (k) {
      doc.secretForeignKeys.forEach(function (fk) {
        emit([k, fk], doc._id);
      });
    });
  }
};
