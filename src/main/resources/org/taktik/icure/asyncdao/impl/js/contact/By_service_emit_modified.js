map = function (doc) {
  if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted ) {
    doc.services.forEach(function (s) { emit(s._id, { contactId:doc._id, serviceId:s._id, modified:s.modified }); });
  }
};
