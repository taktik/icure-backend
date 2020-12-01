map = function (doc) {
  var emit_contacts = function (k, doc) {
    var formIdsMap = {};
    doc.services.forEach(function (s) {
      if (s.formId) {
        if (!formIdsMap[s.formId]) {
          formIdsMap[s.formId] = 1;
          emit([k, s.formId], doc._id);
        }
      }
    });
    doc.subContacts.forEach(function (sc) {
      if (sc.formId) {
        if (!formIdsMap[sc.formId]) {
          formIdsMap[sc.formId] = 1;
          emit([k, sc.formId], doc._id);
        }
      }
    });
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    Object.keys(doc.delegations).forEach(function (k) {
     emit_contacts(k, doc);
    });
  }
};
