package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.MedicalLocationDAO;
import org.taktik.icure.dao.PlaceDAO;
import org.taktik.icure.entities.MedicalLocation;
import org.taktik.icure.entities.Place;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.MedicalLocationLogic;

import java.util.List;

@Service
public class MedicalLocationLogicImpl extends GenericLogicImpl<MedicalLocation, MedicalLocationDAO> implements MedicalLocationLogic {

    private MedicalLocationDAO medicalLocationDAO;
    private ICureSessionLogic sessionLogic;

    @Override
    public MedicalLocation createMedicalLocation(MedicalLocation medicalLocation) {
        return medicalLocationDAO.create(medicalLocation);
    }

    @Override
    public List<String> deleteMedicalLocation(List<String> ids) throws DeletionException {
        try {
            deleteEntities(ids);
            return ids;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public MedicalLocation getMedicalLocation(String medicalLocation) {
        return this.medicalLocationDAO.get(medicalLocation);
    }

    @Override
    public MedicalLocation modifyMedicalLocation(MedicalLocation medicalLocation) {
        return this.medicalLocationDAO.save(medicalLocation);
    }

    @Override
    public List<MedicalLocation> findByPostCode(String postCode) {
        return this.medicalLocationDAO.byPostCode(postCode);
    }

    @Override
    protected MedicalLocationDAO getGenericDAO() {
        return this.medicalLocationDAO;
    }

    @Autowired
    public void setSessionLogic(ICureSessionLogic sessionLogic) {
        this.sessionLogic = sessionLogic;
    }

    @Autowired
    public void setMedicalLocationDAO(MedicalLocationDAO dao) { this.medicalLocationDAO = dao; }
}