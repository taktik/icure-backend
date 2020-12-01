map = function (doc) {
  var emit_contacts = function (k, doc) {
    doc.secretForeignKeys.forEach(function (fk) {
      emit([k, fk], doc._id);
    });
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    Object.keys(doc.delegations).forEach(function (k) {
     emit_contacts(k, doc);
    });
  }
};
