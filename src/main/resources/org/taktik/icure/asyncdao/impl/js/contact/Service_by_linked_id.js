map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Contact' && !doc.deleted ) {
        doc.services.forEach(function (s) { if (s.qualifiedLinks && Object.keys(s.qualifiedLinks).length) {
            Object.keys(s.qualifiedLinks).forEach(function(k) { s.qualifiedLinks[k].forEach( function(l) { emit(l, [k, s._id]) } ) } )
        } });
    }
};
