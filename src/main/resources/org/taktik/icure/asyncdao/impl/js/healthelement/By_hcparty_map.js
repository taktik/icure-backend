map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.HealthElement' && !doc.deleted && doc.secretForeignKeys && doc.secretForeignKeys.length && doc.delegations) {
        Object.keys(doc.delegations).forEach(function (key) {
            var delegationsByKey = doc.delegations[key];
            delegationsByKey.forEach(function (delegation) {
                emit([delegation.delegatedTo ], doc._id);
            });
        });
    }
};
