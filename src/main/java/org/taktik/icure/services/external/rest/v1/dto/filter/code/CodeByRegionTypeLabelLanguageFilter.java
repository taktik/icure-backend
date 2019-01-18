package org.taktik.icure.services.external.rest.v1.dto.filter.code;

import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import java.util.Objects;
import java.util.Optional;

import static org.taktik.icure.db.StringUtils.sanitizeString;

@JsonPolymorphismRoot(Filter.class)
public class CodeByRegionTypeLabelLanguageFilter extends Filter<Code> implements org.taktik.icure.dto.filter.code.CodeByRegionTypeLabelLanguageFilter {

    private String region;
    private String type;
    private String language;
    private String label;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeByRegionTypeLabelLanguageFilter)) return false;
        CodeByRegionTypeLabelLanguageFilter that = (CodeByRegionTypeLabelLanguageFilter) o;
        return Objects.equals(region, that.region) &&
                Objects.equals(type, that.type) &&
                Objects.equals(language, that.language) &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, type, language, label);
    }

    @Override
    public boolean matches(Code item) {
        String ss = sanitizeString(label);
        return (region == null || item.getRegions() == null || item.getRegions().contains(region))
                && (type == null || Objects.equals(type, this.type))
                && (language == null ? item.getLabel().values().stream().anyMatch( l -> Optional.ofNullable(l).map(s -> sanitizeString(s).contains(ss)).orElse(false))
                    : Optional.ofNullable(item.getLabel().get(language)).map(s -> sanitizeString(s).contains(ss)).orElse(false));
    }
}
