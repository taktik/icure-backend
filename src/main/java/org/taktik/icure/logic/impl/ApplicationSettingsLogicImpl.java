package org.taktik.icure.logic.impl;

import org.taktik.icure.dao.ApplicationSettingsDAO;
import org.taktik.icure.entities.ApplicationSettings;
import org.taktik.icure.logic.ApplicationSettingsLogic;

import java.util.List;

public class ApplicationSettingsLogicImpl extends GenericLogicImpl<ApplicationSettings, ApplicationSettingsDAO> implements ApplicationSettingsLogic {
//    @Override
//    public List<ApplicationSettings> getApplicationSettings() {
//        return null;
//    }

    @Override
    protected ApplicationSettingsDAO getGenericDAO() {
        return null;
    }
}
