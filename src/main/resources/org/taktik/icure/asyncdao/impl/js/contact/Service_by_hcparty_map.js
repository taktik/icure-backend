map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                doc.services.forEach(function (service) {
                    emit(k, service._id);
                })
            });
        }
    }
};
