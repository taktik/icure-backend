package org.taktik.icure.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.ApplicationSettingsDAO;
import org.taktik.icure.entities.ApplicationSettings;
import org.taktik.icure.logic.ApplicationSettingsLogic;

@Service
public class ApplicationSettingsLogicImpl extends GenericLogicImpl<ApplicationSettings, ApplicationSettingsDAO> implements ApplicationSettingsLogic {

    private ApplicationSettingsDAO applicationSettingsDAO;

    @Override
    protected ApplicationSettingsDAO getGenericDAO() {
        return this.applicationSettingsDAO;
    }

    @Autowired
    public void setApplicationSettingsDAO(ApplicationSettingsDAO dao) { this.applicationSettingsDAO = dao; }

    @Override
    public ApplicationSettings modifyApplicationSettings(ApplicationSettings applicationSettings) {
        return applicationSettingsDAO.save(applicationSettings);
    }

    @Override
    public ApplicationSettings createApplicationSettings(ApplicationSettings applicationSettings) {
        return applicationSettingsDAO.create(applicationSettings);
    }
}
