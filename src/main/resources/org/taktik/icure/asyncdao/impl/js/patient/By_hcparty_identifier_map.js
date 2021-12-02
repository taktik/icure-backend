function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Patient' && !doc.deleted && doc.identifier) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (d) {
                doc.identifier.forEach(function(k) {
                     emit([d, k.system, k.value], null);
                  });
            });
        }
    }
}
