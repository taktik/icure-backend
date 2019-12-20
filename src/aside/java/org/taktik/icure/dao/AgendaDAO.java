package org.taktik.icure.dao;

import org.taktik.icure.entities.Agenda;

import java.util.List;

public interface AgendaDAO extends GenericDAO<Agenda> {
    List<Agenda> getAllAgendaForUser(String userId);

    List<Agenda> getReadableAgendaForUser(String userId);
}
