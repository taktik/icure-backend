package org.taktik.icure.entities;

import org.taktik.icure.entities.base.StoredDocument;

import java.util.HashMap;
import java.util.Map;

public class ApplicationSettings extends StoredDocument {

    protected Map<String, String> settings = new HashMap<>();

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
