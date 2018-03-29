map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Message' && !doc.deleted && doc.delegations && doc.parentId) {
    emit(doc.parentId, 1);
  }
};
