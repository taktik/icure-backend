map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.Vmp' && !doc.deleted && doc.vmpGroup && doc.vmpGroup.id) {
        emit(doc.vmpGroup.id, 1)
    }
};
