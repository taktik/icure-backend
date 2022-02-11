function(doc) { if (doc.java_type == 'org.taktik.icure.entities.Contact' && !doc.deleted && doc.externalId) emit( doc.externalId, doc._id )}
