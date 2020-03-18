map = function (doc) {
  var emit_forms = function (k, doc) {
    doc.secretForeignKeys.forEach(function (fk) {
      emit([k, fk], doc._id);
    });
  };

  if (doc.java_type == 'org.taktik.icure.entities.Form' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    Object.keys(doc.delegations).forEach(function (k) {
      emit_forms(k, doc);
    });
  }
};
