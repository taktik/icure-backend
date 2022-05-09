function (doc) {
    var emit_services_by_helement = function (hcparty, doc) {
        doc.subContacts.forEach(function (sc) {
            if (sc.healthElementId && sc.services && Object.keys(sc.services).length) {
                sc.services.forEach(function (s) {
                    emit([hcparty, sc.healthElementId], s.serviceId)
                })
            }
        });
    };

    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                emit_services_by_helement(k, doc);
            });
        }
    }
}
