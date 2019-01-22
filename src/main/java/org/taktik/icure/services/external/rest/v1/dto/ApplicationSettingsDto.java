package org.taktik.icure.services.external.rest.v1.dto;

import java.util.HashMap;
import java.util.Map;

public class ApplicationSettingsDto extends StoredDto {
    protected Map<String, String> options = new HashMap<>();
}
