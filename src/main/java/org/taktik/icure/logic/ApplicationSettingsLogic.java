package org.taktik.icure.logic;

import org.taktik.icure.entities.ApplicationSettings;

public interface ApplicationSettingsLogic extends EntityPersister<ApplicationSettings, String> {
    ApplicationSettings modifyApplicationSettings(ApplicationSettings applicationSettings);
    ApplicationSettings createApplicationSettings(ApplicationSettings applicationSettings);
}
