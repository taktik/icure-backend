map = function(doc) {
    var onlyUnique = function(value, index, self) {
        return self.indexOf(value) === index;
    };

    if (doc.java_type === 'org.taktik.icure.entities.HealthcareParty' && !doc.deleted) {
        var cps = [];
        doc.addresses.forEach(function (a) {
            cps.push(a.postalCode);
        });
        cps.filter(onlyUnique).forEach(function (cp) {
            emit([doc.speciality, doc.nihiiSpecCode, cp], doc._id)
        });
    }
};
