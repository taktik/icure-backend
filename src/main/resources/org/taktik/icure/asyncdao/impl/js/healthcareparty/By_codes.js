map = function (doc) {
  var emit_by_code = function(doc) {
      if (doc.codes && doc.codes.length) {
          doc.codes.forEach(function (code) {
          emit([code.type, code.code], doc._id);
        });
      }
  };

  if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) {
      emit_by_code(doc);
  }
};
