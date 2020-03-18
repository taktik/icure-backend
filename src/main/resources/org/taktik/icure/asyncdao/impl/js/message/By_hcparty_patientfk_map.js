map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations && Object.keys(doc.delegations).length) {
    var fkIds = {};
    doc.secretForeignKeys.forEach(function(fk) {
      fkIds[fk] = 1;
    });
    Object.keys(doc.delegations).forEach(function (k) {
      Object.keys(fkIds).forEach(function (fk) {
        emit([k, fk], doc._id);
      });
    });
  }
};
