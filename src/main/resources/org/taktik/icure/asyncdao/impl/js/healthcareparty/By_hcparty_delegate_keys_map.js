map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted && doc.hcPartyKeys) {
        Object.keys(doc.hcPartyKeys).forEach(function(k) {
          //Emit for each delegate a tuple: thisDoc.id and the key encrypted for the delegate
           emit(k,[doc._id,doc.hcPartyKeys[k][1]]);
        });
    }
};
