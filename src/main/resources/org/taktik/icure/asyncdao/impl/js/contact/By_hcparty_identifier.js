function(doc) {
    var emit_contacts = function (hcParty, doc) {
        if (doc.identifier && doc.identifier.length) {
            doc.identifier.forEach(function (identifier) {
                emit([hcParty, identifier.system, identifier.value], doc._id);
            });
        }
    };

    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted && doc.delegations && Object.keys(doc.delegations).length) {
        Object.keys(doc.delegations).forEach(function (k) {
            emit_contacts(k, doc);
        });
    }
}
