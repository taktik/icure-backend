map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.ampps) {
        doc.ampps.forEach(function (ampp) {
            (ampp.atcs || []).forEach(function (atc) {
                atc.code && emit(atc.code, 1)
            })
        })
    }
};
