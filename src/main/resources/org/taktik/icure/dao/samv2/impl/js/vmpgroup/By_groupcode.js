map = function(doc) {
    if (doc.java_type === 'org.taktik.icure.entities.samv2.VmpGroup' && !doc.deleted && doc.code) {
        emit(doc.code, 1)
    }
};
