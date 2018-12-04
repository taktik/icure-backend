map = function (doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) {
        if (doc.patientHealthCareParties) {
            doc.patientHealthCareParties.forEach(function (phcp) {
                var active = doc.active;
                emit([phcp.healthcarePartyId, active], doc._id);
            });
        }
    }
};
