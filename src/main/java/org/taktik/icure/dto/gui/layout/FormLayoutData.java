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

package org.taktik.icure.dto.gui.layout;




import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.dto.gui.Code;
import org.taktik.icure.dto.gui.CodeType;
import org.taktik.icure.dto.gui.Editor;
import org.taktik.icure.dto.gui.FormDataOption;
import org.taktik.icure.dto.gui.FormPlanning;
import org.taktik.icure.dto.gui.Formula;
import org.taktik.icure.dto.gui.Suggest;
import org.taktik.icure.dto.gui.type.Data;
import org.taktik.icure.entities.embed.Content;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by aduchate on 19/11/13, 10:50
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormLayoutData implements Serializable {
    public static final String OPTION_ALWAYS_INITIALIZED_IN_FORM = "AlwaysInitialized";
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


	List<Content> defaultValue;


    Integer defaultStatus;

    //Suggestions

    List<CodeType> codeTypes;

    //More versatile way
    //<Suggest class="org.taktik.icure.domain.base.Code" filterKey="type" filterValue="CD-ITEM"/>
    //<Suggest class="org.taktik.icure.domain.HealthcareParty" filterKey="speciality" filterValue="gp"/>

    List<Suggest> suggest;


    List<FormPlanning> plannings;

    List<Code> tags;

    List<Code> codes;

    List<Formula> formulas;

	public FormLayoutData() {
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
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

    public Boolean isSubForm() {
        return subForm;
    }

    public void setSubForm(Boolean subForm) {
        this.subForm = subForm;
    }

    public Boolean isIrrelevant() {
        return irrelevant;
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

    public List<Content> getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(List<Content> defaultValue) {
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
