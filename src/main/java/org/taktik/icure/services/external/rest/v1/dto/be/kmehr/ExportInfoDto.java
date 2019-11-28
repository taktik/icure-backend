package org.taktik.icure.services.external.rest.v1.dto.be.kmehr;

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExportInfoDto implements Serializable {
    List<String> secretForeignKeys;
    List<String> excludedIds;
    HealthcarePartyDto recipient;
    String softwareName;
    String softwareVersion;

    public List<String> getSecretForeignKeys() {
        return secretForeignKeys;
    }

    public void setSecretForeignKeys(List<String> secretForeignKeys) {
        this.secretForeignKeys = secretForeignKeys;
    }

    public List<String> getExcludedIds() {
        if(excludedIds != null)
            return excludedIds;
        else
            return new ArrayList<>();
    }

    public void setExcludedIds(List<String> excludedIds) {
        this.excludedIds = excludedIds;
    }

    public HealthcarePartyDto getRecipient() {
        return recipient;
    }

    public void setRecipient(HealthcarePartyDto recipient) {
        this.recipient = recipient;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }
}
