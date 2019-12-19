package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.AgendaDAO;
import org.taktik.icure.entities.Agenda;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.AgendaLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.util.List;

@Service
public class AgendaLogicImpl extends GenericLogicImpl<Agenda, AgendaDAO> implements AgendaLogic {

    private AgendaDAO agendaDAO;
    private ICureSessionLogic sessionLogic;

    @Override
    public Agenda createAgenda(Agenda agenda) {
        return agendaDAO.create(agenda);
    }

    @Override
    public List<String> deleteAgenda(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public Agenda getAgenda(String agenda) {
        return agendaDAO.get(agenda);
    }

    @Override
    public Agenda modifyAgenda(Agenda agenda) {
        return agendaDAO.save(agenda);
    }

    @Override
    public List<Agenda> getAllAgendaForUser(String userId) {
        return agendaDAO.getAllAgendaForUser(userId);
    }

    @Override
    public List<Agenda> getReadableAgendaForUser(String userId) {
        return agendaDAO.getReadableAgendaForUser(userId);
    }

    @Autowired
    public void setCalendarItemDAO(AgendaDAO agenda) {
        this.agendaDAO = agenda;
    }

    @Autowired
    public void setSessionLogic(ICureSessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Override
    protected AgendaDAO getGenericDAO() {
        return agendaDAO;
    }
}
