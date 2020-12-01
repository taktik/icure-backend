map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Receipt' && !doc.deleted ) {
        doc.references.forEach(function (s) { emit(s, doc._id, doc.created); });
    }
};
