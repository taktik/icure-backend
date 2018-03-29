map = function (doc) {
  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
    if (doc.delegations) {
      var ss = {};
      doc.services.forEach(function (s) {
        ss[s.label]=1;
      });
      Object.keys(ss).forEach(function (s) {
        Object.keys(doc.delegations).forEach(function (k) {
          emit([k,s], 1);
        });
      });
    }
  }
};



