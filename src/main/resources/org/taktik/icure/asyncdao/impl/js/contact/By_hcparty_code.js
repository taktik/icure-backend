map = function (doc) {
  var emit_by_code = function(hcparty, doc) {
      var d = doc.openingDate;
      if (doc.codes && doc.codes.length) {
          doc.codes.forEach(function (code) {
          emit([hcparty, code.type, code.code,  d<99999999?d*1000000:d], doc._id);
        });
      }
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
    if (doc.delegations) {
      Object.keys(doc.delegations).forEach(function (k) {
        emit_by_code(k, doc);
      });
    }
  }
};
