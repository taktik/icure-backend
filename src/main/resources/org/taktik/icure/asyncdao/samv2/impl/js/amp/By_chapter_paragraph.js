map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted) {
        var lrps = {};
        (doc.ampps || []).forEach(function(ampp) {
            (ampp.dmpps || []).forEach(function(dmpp) {
                (dmpp.reimbursements || []).forEach(function(r) {
                    var split = (r.legalReferencePath || '').split('-')
                    split[1] && split[2] && (lrps[[split[1], split[2]]] = true);
                })
            })
        })

        Object.keys(lrps).forEach(function (k) {
            emit(k.split(","), 1);
        })
    }
};
