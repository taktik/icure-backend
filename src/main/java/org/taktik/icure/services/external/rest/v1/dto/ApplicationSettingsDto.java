package org.taktik.icure.services.external.rest.v1.dto;

import java.util.HashMap;
import java.util.Map;

public class ApplicationSettingsDto extends StoredDto {

    protected Map<String, String> options = new HashMap<>();

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
