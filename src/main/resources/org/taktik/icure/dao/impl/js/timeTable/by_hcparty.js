map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.TimeTable' && !doc.deleted) {
        if (doc.delegations) {
            Object.keys(doc.delegations).forEach(function (k) {
                emit(k, doc);
            });
        }
    }
};



