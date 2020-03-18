map = function(doc) {
    if (doc.java_type == 'org.taktik.icure.entities.Patient' && !doc.deleted) {
        if(doc.patientHealthCareParties) {
            doc.patientHealthCareParties.forEach(function(phcp){
                emit([phcp.healthcarePartyId,doc.dateOfBirth],doc._id);
            });
        }
    }
};
