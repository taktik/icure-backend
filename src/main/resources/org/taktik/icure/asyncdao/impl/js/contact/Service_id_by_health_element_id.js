function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted ) {
        doc.subContacts.forEach(function(sc) {
            if (sc.healthElementId && sc.services && Object.keys(sc.services).length) {
                sc.services.forEach(function(s) {
                    emit(sc.healthElementId, s.serviceId)
                })
            }
        });
    }
}
