map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.TimeTable' && !doc.deleted) {
        emit(doc.agendaId, doc);
    }
};
