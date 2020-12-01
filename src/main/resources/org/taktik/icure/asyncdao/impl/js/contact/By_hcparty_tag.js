map = function (doc) {
    var emit_by_tag = function (hcparty, doc) {
          var d = doc.openingDate;
          if (doc.tags && doc.tags.length) {
                doc.tags.forEach(function (tag) {
                  emit([hcparty, tag.type, tag.code, d<99999999?d*1000000:d], doc._id);
                });
            }
    };

    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                emit_by_tag(k, doc);
            });
        }
    }
};
