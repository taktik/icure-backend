package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class FlatRateTarificationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String code;
    protected java.util.Map<String, String> label;
    protected Set<ValorisationDto> valorisations;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public Set<ValorisationDto> getValorisations() {
        return valorisations;
    }

    public void setValorisations(Set<ValorisationDto> valorisations) {
        this.valorisations = valorisations;
    }
}
