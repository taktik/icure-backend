map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Agenda' && !doc.deleted) {
        doc.rights.forEach(function(right) {
            emit(right.userId, doc);
        });
    }
};
