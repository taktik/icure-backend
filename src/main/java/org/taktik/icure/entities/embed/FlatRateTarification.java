package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlatRateTarification implements Serializable {

    protected String code;
    protected FlatRateType flatRateType;
    protected java.util.Map<String, String> label;
    protected Set<Valorisation> valorisations;

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

    public Set<Valorisation> getValorisations() {
        return valorisations;
    }

    public void setValorisations(Set<Valorisation> valorisations) {
        this.valorisations = valorisations;
    }

    public FlatRateType getFlatRateType() { return flatRateType; }

    public void setFlatRateType(FlatRateType flatRateType) { this.flatRateType = flatRateType; }
}
