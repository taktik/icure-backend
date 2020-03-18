map = function (doc) {
  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted ) {
    doc.services.forEach(function (s) { emit(s._id, doc._id); });
  }
};



