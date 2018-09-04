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

package org.taktik.icure.services.external.rest.v1.dto;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.taktik.icure.entities.base.LinkQualification;
import org.taktik.icure.services.external.rest.v1.dto.embed.CodeFlag;


public class CodeDto extends StoredDto {
	// id = type|code|version  => this must be unique

	protected Set<String> regions; //ex: be,fr
	protected String type; //ex: ICD (type + version + code combination must be unique) (or from tags -> CD-ITEM)
	protected String version; //ex: 10
	protected String code; //ex: I06.2 (or from tags -> healthcareelement)
	protected Integer level; //ex: 0 = System, not to be modified by user, 1 = optional, created or modified by user
	protected java.util.Map<String, String> label; //ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}
	protected java.util.Map<String, Set<String>> searchTerms; //Extra search terms

	@Deprecated
	protected List<String> links; //Links towards related codes (corresponds to an approximate link in qualifiedLinks)

	protected Map<LinkQualification, List<String>> qualifiedLinks; //Links towards related codes
	protected List<CodeFlag> flags; //flags (like female only) for the code

	protected String data;

	public CodeDto() {
	}

	public CodeDto(String typeAndCode) {
		this.type = typeAndCode.split(":")[0];
		this.code = typeAndCode.split(":")[1];
	}

	public CodeDto(String type, String code) {
		this(type,code,0);
	}

	public CodeDto(String type, String code, Integer level) {
		this.type = type;
		this.code = code;
		this.level = level;
	}

	public CodeDto(String type, String code, String version) {
		this.type = type;
		this.version = version;
		this.code = code;
	}

    public CodeDto(Set<String> regions, String type, String code, String version) {
        this(regions, type, code, version, new HashMap<>());
    }

    public CodeDto(String region, String type, String code, String version) {
        this(Collections.singleton(region), type, code, version);
    }

    public CodeDto(Set<String> regions, String type, String code, String version, java.util.Map<String, String> label) {
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
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
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Set<String> getRegions() {
		return regions;
	}

	public void setRegions(Set<String> regions) {
		this.regions = regions;
	}

	public Map<LinkQualification, List<String>> getQualifiedLinks() {
		return qualifiedLinks;
	}

	public void setQualifiedLinks(Map<LinkQualification, List<String>> qualifiedLinks) {
		this.qualifiedLinks = qualifiedLinks;
	}

	public List<String> getLinks() {
		return links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	public List<CodeFlag> getFlags() {
		return flags;
	}

	public void setFlags(List<CodeFlag> flags) {
		this.flags = flags;
	}

	public Map<String, Set<String>> getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(Map<String, Set<String>> searchTerms) {
		this.searchTerms = searchTerms;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CodeDto code1 = (CodeDto) o;
		return  Objects.equals(type, code1.type) &&
				Objects.equals(code, code1.code) &&
				Objects.equals(version, code1.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions, type, code, version, level);
	}
}
