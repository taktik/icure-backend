/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.gui.layout;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto;
import org.taktik.icure.services.external.rest.v1.dto.gui.Code;
import org.taktik.icure.services.external.rest.v1.dto.gui.CodeType;
import org.taktik.icure.services.external.rest.v1.dto.gui.Editor;
import org.taktik.icure.services.external.rest.v1.dto.gui.FormDataOption;
import org.taktik.icure.services.external.rest.v1.dto.gui.FormPlanning;
import org.taktik.icure.services.external.rest.v1.dto.gui.Formula;
import org.taktik.icure.services.external.rest.v1.dto.gui.Suggest;

/**
 * Created by aduchate on 19/11/13, 10:50
 */
public class FormLayoutData implements Serializable {
    Boolean subForm;

    Boolean irrelevant;
	Boolean determinesSscontactName;
	String type;
    String name;
    Double sortOrder;

    Map<String,FormDataOption> options;
    String descr;
    String label;

    Editor editor;
	List<ContentDto> defaultValue;
    Integer defaultStatus;


    List<Suggest> suggest;
//  [6:28:02 PM] Antoine Duchateau: <Suggest class="org.taktik.icure.domain.base.Code" filterKey="type" filterValue="CD-ITEM"/>
//  [6:28:33 PM] Antoine Duchateau: <Suggest class="org.taktik.icure.domain.HealthcareParty" filterKey="speciality" filterValue="gp"/>

    List<FormPlanning> plannings;
    List<Code> tags;
    List<Code> codes;
    List<CodeType> codeTypes;
    List<Formula> formulas;

	public FormLayoutData() {
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public Boolean isIrrelevant() {
        return irrelevant;
    }

    public void setIrrelevant(Boolean irrelevant) {
        this.irrelevant = irrelevant;
    }

	public Boolean isDeterminesSscontactName() {
		return determinesSscontactName;
	}

	public void setDeterminesSscontactName(Boolean determinesSscontactName) {
		this.determinesSscontactName = determinesSscontactName;
	}

	public Boolean isSubForm() {
        return subForm;
    }

    public void setSubForm(Boolean subForm) {
        this.subForm = subForm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Map<String, FormDataOption> getOptions() {
        return options;
    }

    public void setOptions(Map<String, FormDataOption> options) {
        this.options = options;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public List<ContentDto> getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(List<ContentDto> defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(Integer defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public List<FormPlanning> getPlannings() {
        return plannings;
    }

    public void setPlannings(List<FormPlanning> plannings) {
        this.plannings = plannings;
    }

    public List<Code> getTags() {
        return tags;
    }

    public void setTags(List<Code> tags) {
        this.tags = tags;
    }

    public List<Code> getCodes() {
        return codes;
    }

    public void setCodes(List<Code> codes) {
        this.codes = codes;
    }

    public List<CodeType> getCodeTypes() {
        return codeTypes;
    }

    public void setCodeTypes(List<CodeType> codeTypes) {
        this.codeTypes = codeTypes;
    }

    public List<Suggest> getSuggest() {
        return suggest;
    }

    public void setSuggest(List<Suggest> suggest) {
        this.suggest = suggest;
    }
}
