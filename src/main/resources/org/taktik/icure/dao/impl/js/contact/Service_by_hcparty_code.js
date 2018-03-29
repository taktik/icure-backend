map = function (doc) {
  var emit_services_by_code = function(hcparty, doc) {
    doc.services.forEach(function (service) {
      var d = service.valueDate ? service.valueDate : service.openingDate;
      if (service.codes && service.codes.length) {
        service.codes.forEach(function (code) {
          emit([hcparty, code.type, code.code,  d<99999999?d*1000000:d], service._id);
        });
      }
    });
  };

  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
    if (doc.delegations) {
      Object.keys(doc.delegations).forEach(function (k) {
        emit_services_by_code(k, doc);
      });
    }
  }
};
