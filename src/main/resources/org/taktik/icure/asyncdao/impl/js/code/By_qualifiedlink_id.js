map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.base.Code' && !doc.deleted && doc.qualifiedLinks && Object.keys(doc.qualifiedLinks).length) {
        Object.keys(doc.qualifiedLinks).forEach(function(k) { doc.qualifiedLinks[k].forEach( function(l) { emit([k, l], doc.id) } ) } )
    }
};
