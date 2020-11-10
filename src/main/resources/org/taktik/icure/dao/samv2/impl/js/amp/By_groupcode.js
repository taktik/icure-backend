map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.vmp && doc.vmp.vmpGroup && doc.vmp.vmpGroup.code) {
        emit(doc.vmp.vmpGroup.code, 1)
    }
};
