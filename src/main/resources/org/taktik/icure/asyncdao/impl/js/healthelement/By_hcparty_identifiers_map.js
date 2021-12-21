map = function (doc) {
    if (doc.java_type == 'org.taktik.icure.entities.HealthElement' && !doc.deleted && doc.identifier && doc.delegations) {
        Object.keys(doc.delegations).forEach(function (d) {
            doc.identifier.forEach(function (k) {
                emit([d, k.system, k.value], doc._id);
            });
        });
    }
};
