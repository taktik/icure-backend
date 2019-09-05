map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.vmp && doc.vmp.code) {
        emit(doc.vmp.code, 1)
    }
};
