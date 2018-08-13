map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.Agenda' && !doc.deleted) {
        emit(doc.userId, doc);
    }
};
