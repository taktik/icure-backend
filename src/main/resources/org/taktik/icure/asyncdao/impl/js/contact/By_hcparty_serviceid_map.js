function(doc) {
  var emit_contacts = function (k, doc) {
    var idsMap = {};
    doc.services.forEach(function (s) {
      if (s._id) {
        if (!idsMap[s._id]) {
          idsMap[s._id] = 1;
          emit([k, s._id], doc._id);
        }
      }
    });
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    Object.keys(doc.delegations).forEach(function (k) {
     emit_contacts(k, doc);
    });
  }
}
