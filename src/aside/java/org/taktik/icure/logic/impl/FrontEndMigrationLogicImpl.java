package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.FrontEndMigrationDAO;
import org.taktik.icure.entities.FrontEndMigration;
import org.taktik.icure.exceptions.DeletionException;
import org.taktik.icure.logic.FrontEndMigrationLogic;

import java.util.Arrays;
import java.util.List;

@Service
public class FrontEndMigrationLogicImpl extends GenericLogicImpl<FrontEndMigration, FrontEndMigrationDAO> implements FrontEndMigrationLogic {

    private FrontEndMigrationDAO frontEndMigrationDAO;

    @Override
    public FrontEndMigration createFrontEndMigration(FrontEndMigration frontEndMigration) {
        return frontEndMigrationDAO.create(frontEndMigration);
    }

    @Override
    public String deleteFrontEndMigration(String frontEndMigrationId) throws DeletionException {
        try {
            deleteEntities(Arrays.asList(frontEndMigrationId));
            return frontEndMigrationId;
        } catch (Exception e) {
            throw new DeletionException(e.getMessage(), e);
        }
    }

    @Override
    public FrontEndMigration getFrontEndMigration(String frontEndMigrationId) {
        return frontEndMigrationDAO.get(frontEndMigrationId);
    }

    @Override
    public List<FrontEndMigration> getFrontEndMigrationByUserIdName(String userId, String name) {
        return frontEndMigrationDAO.getByUserIdName(userId, name);
    }

    @Override
    public FrontEndMigration modifyFrontEndMigration(FrontEndMigration frontEndMigration) {
        return frontEndMigrationDAO.save(frontEndMigration);
    }

    @Override
    protected FrontEndMigrationDAO getGenericDAO() {
        return this.frontEndMigrationDAO;
    }

    @Autowired
    public void setFrontEndMigrationDAO(FrontEndMigrationDAO frontEndMigrationDAO) {
        this.frontEndMigrationDAO = frontEndMigrationDAO;
    }
}
