map = function (doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) {
        if (doc.patientHealthCareParties) {
            doc.patientHealthCareParties.forEach(function (phcp) {
                var ssin = doc.ssin ? doc.ssin.replace(new RegExp('\\s', 'g'), '').replace(new RegExp('\\W', 'g'), '') : null;
                emit([phcp.healthcarePartyId, ssin], doc._id);
            });
        }
    }
};
