map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.ampps) {
        var atcs = {}
        doc.ampps.forEach(function (ampp) {
            (ampp.atcs || []).forEach(function (atc) {
                atc.code && (atcs[atc.code] = true)
            })
        })
        Object.keys(atcs).forEach(function (atcCode) {
            emit(atcCode, 1)
        })
    }
};
