map = function (doc) {
    var emit_by_tag = function (doc) {
          if (doc.tags && doc.tags.length) {
                doc.tags.forEach(function (tag) {
                  emit([tag.type, tag.code], doc._id);
                });
            }
    };

    if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) {
        emit_by_tag(doc);
    }
};
