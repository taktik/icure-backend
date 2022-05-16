map = function(doc) {
    var emit_hcp = function (doc) {
        doc.identifier.forEach(function (identifier) {
            if (identifier.identifier && identifier.identifier.length) {
                identifier.identifier.forEach(function (identifier) {
                    emit([identifier.system, identifier.value], doc._id);
                });
            }
        });
    };

    if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) {
        emit_hcp(doc)
    }
}
