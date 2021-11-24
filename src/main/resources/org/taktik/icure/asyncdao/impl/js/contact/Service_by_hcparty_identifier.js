function(doc) {
    var emit_services_by_identifier = function (hcparty, doc) {
        doc.services.forEach(function (service) {
            if (service.identifier && service.identifier.length) {
                service.identifier.forEach(function (identifier) {
                    emit([hcparty, identifier.system, identifier.value], service._id);
                });
            }
        });
    };

    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                emit_services_by_identifier(k, doc);
            });
        }
    }
}
