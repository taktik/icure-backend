map = function (doc) {
  if (doc.java_type == 'org.taktik.icure.entities.Error' && !doc.deleted) {
    emit([doc.userId, doc.domain], doc._id);
  }
};