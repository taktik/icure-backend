/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.ImmutableMap;
import org.taktik.icure.entities.embed.Periodicity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Code extends StoredDocument implements CodeIdentification {
	private static final long serialVersionUID = 1L;
	public static final Map<String,String> versionsMap = ImmutableMap.of(
			"INAMI-RIZIV", "1.0"
	);

	// id = type|code|version  => this must be unique

    protected String author;

	protected Set<String> regions; //ex: be,fr
    protected List<Periodicity> periodicity;
	protected String type; //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
    protected String code; //ex: I06.2 (or from tags -> healthcareelement). Local codes are encoded as LOCAL:SLLOCALFROMMYSOFT
    protected String version; //ex: 10. Must be lexicographically searchable

    protected Integer level; //ex: 0 = System, not to be modified by user, 1 = optional, created or modified by user

    protected java.util.Map<String, String> label; //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}

	@Deprecated
    protected List<String> links; //Links towards related codes (corresponds to an approximate link in qualifiedLinks)

	protected Map<LinkQualification, List<String>> qualifiedLinks; //Links towards related codes
    protected Set<CodeFlag> flags; //flags (like female only) for the code
    protected java.util.Map<String, Set<String>> searchTerms; //Extra search terms/ language

	protected String data;

    protected Map<AppendixType, String> appendices;

    protected boolean disabled;

	public static Code dataCode(String typeAndCodeAndVersion, String data) {
		Code c = new Code(typeAndCodeAndVersion);
		c.data = data;

		return c;
	}

    public Code() {
    }

    public Code(String typeAndCodeAndVersion) {
        this(typeAndCodeAndVersion.split("\\|")[0],typeAndCodeAndVersion.split("\\|")[1],typeAndCodeAndVersion.split("\\|")[2]);
    }

	public Code(String type, String code, String version) {
		this(new HashSet<>(Arrays.asList("be","fr")),type, code, version);
	}

	public Code(Set<String> regions, String type, String code, String version) {
		this(regions, type, code, version, new HashMap<>());
	}

	public Code(String region, String type, String code, String version) {
		this(Collections.singleton(region), type, code, version);
	}

    public Code(Set<String> regions, String type, String code, String version, java.util.Map<String, String> label) {
        this.regions = regions;
        this.type = type;
        this.code = code;
        this.version = version;
        this.label = label;

        this.id = type+'|'+code+'|'+version;
    }

    public String toString() {
        return this.type + ":" + this.code;
    }

    @Deprecated
    @JsonIgnore
    public String getDescrFR() {
        return label != null ? label.get("fr") : null;
    }

    @Deprecated
    public void setDescrFR(String descrFR) {
        if (label == null) { label = new HashMap<>(); }
        label.put("fr", descrFR);
    }

    @Deprecated
    @JsonIgnore
    public String getDescrNL() {
        return label != null ? label.get("nl") : null;
    }

    @Deprecated
    public void setDescrNL(String descrNL) {
        if (label == null) { label = new HashMap<>(); }
        label.put("nl", descrNL);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.id = type+'|'+code+'|'+version;
        this.code = code;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.id = type+'|'+code+'|'+version;
        this.type = type;
    }

	public Set<String> getRegions() {
		return regions;
	}

	public void setRegions(Set<String> regions) {
		this.regions = regions;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
        this.id = type+'|'+code+'|'+version;
		this.version = version;
	}

    public Map<String, Set<String>> getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(Map<String, Set<String>> searchTerms) {
        this.searchTerms = searchTerms;
    }

    public Set<CodeFlag> getFlags() {
        return flags;
    }

    public void setFlags(Set<CodeFlag> flags) {
        this.flags = flags;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

	public Map<LinkQualification, List<String>> getQualifiedLinks() {
		return qualifiedLinks;
	}

	public void setQualifiedLinks(Map<LinkQualification, List<String>> qualifiedLinks) {
		this.qualifiedLinks = qualifiedLinks;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public Map<AppendixType, String> getAppendices() { return appendices; }

    public void setAppendices(Map<AppendixType, String> appendices) { this.appendices = appendices; }

    public boolean isDisabled() { return disabled; }

    public void setDisabled(boolean disabled) { this.disabled = disabled; }

    public List<Periodicity> getPeriodicity() { return periodicity; }

    public void setPeriodicity(List<Periodicity> periodicity) { this.periodicity = periodicity; }

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Code)) return false;
		if (!super.equals(o)) return false;
		Code code1 = (Code) o;
		return Objects.equals(type, code1.type) &&
				Objects.equals(code, code1.code) &&
				Objects.equals(version, code1.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), type, code, version);
	}
}
