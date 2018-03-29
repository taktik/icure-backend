map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    var actors = {};
    if (doc.fromHealthcarePartyId) {
      actors[doc.fromHealthcarePartyId] = 1;
    }
    if (doc.secretForeignKeys) {
      doc.secretForeignKeys.forEach(function (fk) {
        actors[fk] = 1;
      });
    }
    if (doc.recipients) {
      doc.recipients.forEach(function (rId) {
        actors[rId] = 1;
      });
    }
    Object.keys(doc.delegations).forEach(function (k) {
      Object.keys(actors).forEach(function (a) {
        emit([k, doc.fromAddress, a], doc._id);
      });
    });
  }
};
