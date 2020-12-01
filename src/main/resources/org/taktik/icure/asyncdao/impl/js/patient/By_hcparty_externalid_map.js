map = function (doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.externalId) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                var externalId = doc.externalId.replace(new RegExp('\\s', 'g'), '').replace(new RegExp('\\W', 'g'), '');
                emit([k, externalId], doc._id);
            });
        }
    }
};
