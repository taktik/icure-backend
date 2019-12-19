package org.taktik.icure.logic;

import org.taktik.icure.entities.Agenda;
import org.taktik.icure.entities.CalendarItemType;
import org.taktik.icure.exceptions.DeletionException;

import java.util.List;

public interface AgendaLogic extends EntityPersister<Agenda, String>{
    Agenda createAgenda(Agenda agenda);

    List<String> deleteAgenda(List<String> ids) throws DeletionException;

    Agenda getAgenda(String agenda);

    Agenda modifyAgenda(Agenda agenda);

    List<Agenda> getAllAgendaForUser(String userId);

    List<Agenda> getReadableAgendaForUser(String userId);
}
