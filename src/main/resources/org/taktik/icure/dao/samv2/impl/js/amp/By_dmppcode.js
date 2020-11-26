map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.ampps && doc.ampps.length) {
        var dmpps = {}
        doc.ampps.forEach(function(ampp) { ampp && ampp.dmpps && ampp.dmpps.forEach(function(dmpp) { dmpp && dmpp.code && (dmpps[dmpp.code] = 1)})})
        Object.keys(dmpps).forEach(k => emit(k,1))
    }
};
