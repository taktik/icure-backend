package org.taktik.icure.services.external.rest.v1.dto;

import java.util.HashMap;
import java.util.Map;

public class ApplicationSettingsDto extends StoredDto {

    protected Map<String, String> settings = new HashMap<>();

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
