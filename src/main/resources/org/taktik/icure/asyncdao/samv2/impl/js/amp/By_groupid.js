map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Amp' && !doc.deleted && doc.vmp && doc.vmp.vmpGroup && doc.vmp.vmpGroup.id) {
        emit(doc.vmp.vmpGroup.id, 1)
    }
};
