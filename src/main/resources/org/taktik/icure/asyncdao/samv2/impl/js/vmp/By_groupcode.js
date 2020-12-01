map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Vmp' && !doc.deleted && doc.vmpGroup && doc.vmpGroup.code) {
        emit(doc.vmpGroup.code, 1)
    }
};
