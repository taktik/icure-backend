package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.PlaceDAO;
import org.taktik.icure.entities.Place;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.PlaceLogic;
import org.taktik.icure.logic.ICureSessionLogic;

import java.util.List;

@Service
public class PlaceLogicImpl extends GenericLogicImpl<Place, PlaceDAO> implements PlaceLogic {

    private PlaceDAO placeDAO;
    private ICureSessionLogic sessionLogic;

    @Override
    public Place createPlace(Place place) {
        return placeDAO.create(place);
    }

    @Override
    public List<String> deletePlace(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public Place getPlace(String place) {
        return placeDAO.get(place);
    }

    @Override
    public Place modifyPlace(Place place) {
        return placeDAO.save(place);
    }

    @Autowired
    public void setCalendarItemDAO(PlaceDAO place) {
        this.placeDAO = place;
    }

    @Autowired
    public void setSessionLogic(ICureSessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Override
    protected PlaceDAO getGenericDAO() {
        return placeDAO;
    }
}
