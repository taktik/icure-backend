map = function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.CalendarItem' && !doc.deleted) {
        emit([doc.agendaId,doc.startTime], doc);
    }
};
