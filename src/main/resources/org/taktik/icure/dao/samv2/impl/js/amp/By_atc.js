map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.ampps && doc.ampps.length) {
        ampps.forEach(function (ampp) {
            (ampp.atcs || []).forEach(function (atc) {
                emit(atc, 1)
            })
        })
    }
};
