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

package org.taktik.icure.entities;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import org.apache.commons.codec.digest.DigestUtils;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.StoredDocument;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dashboard extends StoredDocument {
    protected byte[] data;
	protected String md5;
    protected transient String gsp;
    protected String group;
    protected String guid;
	protected String name;
	protected String descr;
    protected int version;
    protected List<Code> dataTags;
    protected Boolean patientDataNeeded;
    protected Boolean dietaryIntakes;
    protected Boolean desiredMenuCategories;
    protected Boolean prescribedMenuCategories;
    protected Boolean nutrimentsHistory;
    protected Boolean conditions;
    protected Boolean antecedents;
    protected Boolean pah;
    protected Boolean foods;
    protected Boolean prescribedAndForbiddenFoods;
    protected Boolean tastes;
    protected Boolean paq;
    protected Boolean nutrimentNeeds;
    protected Map<?, ?> history;
    
    protected String formTemplateId;

    public String getFormTemplateId() {
        return formTemplateId;
    }

    public void setFormTemplateId(String formTemplateId) {
        this.formTemplateId = formTemplateId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
	    this.md5 = DigestUtils.md5Hex(data);
    }

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getMd5() {
		return md5;
	}

	public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@JsonIgnore
    public String getGsp() {
        return gsp;
    }

    @JsonIgnore
    public void setGsp(String gsp) {
        this.gsp = gsp;
    }

	public List<Code> getDataTags() {
		return dataTags;
	}

	public void setDataTags(List<Code> dataTags) {
		this.dataTags = dataTags;
	}

	public Boolean getPatientDataNeeded() {
		return patientDataNeeded;
	}

	public void setPatientDataNeeded(Boolean patientDataNeeded) {
		this.patientDataNeeded = patientDataNeeded;
	}

	public Boolean getDietaryIntakes() {
		return dietaryIntakes;
	}

	public void setDietaryIntakes(Boolean dietaryIntakes) {
		this.dietaryIntakes = dietaryIntakes;
	}

	public Boolean getNutrimentsHistory() {
		return nutrimentsHistory;
	}

	public void setNutrimentsHistory(Boolean nutrimentsHistory) {
		this.nutrimentsHistory = nutrimentsHistory;
	}

	public Boolean getDesiredMenuCategories() {
		return desiredMenuCategories;
	}

	public void setDesiredMenuCategories(Boolean desiredMenuCategories) {
		this.desiredMenuCategories = desiredMenuCategories;
	}

	public Boolean getPrescribedMenuCategories() {
		return prescribedMenuCategories;
	}

	public void setPrescribedMenuCategories(Boolean prescribedMenuCategories) {
		this.prescribedMenuCategories = prescribedMenuCategories;
	}

	public Map<?, ?> getHistory() {
		return history;
	}

	public void setHistory(Map<?, ?> history) {
		this.history = history;
	}

	public Boolean getConditions() {
		return conditions;
	}

	public void setConditions(Boolean conditions) {
		this.conditions = conditions;
	}

	public Boolean getPah() {
		return pah;
	}

	public void setPah(Boolean pah) {
		this.pah = pah;
	}

	public Boolean getPaq() {
		return paq;
	}

	public void setPaq(Boolean paq) {
		this.paq = paq;
	}

	public Boolean getAntecedents() {
		return antecedents;
	}

	public void setAntecedents(Boolean antecedents) {
		this.antecedents = antecedents;
	}

	public Boolean getNutrimentNeeds() {
		return nutrimentNeeds;
	}

	public void setNutrimentNeeds(Boolean nutrimentNeeds) {
		this.nutrimentNeeds = nutrimentNeeds;
	}

	public Boolean getFoods() {
		return foods;
	}

	public void setFoods(Boolean foods) {
		this.foods = foods;
	}

	public Boolean getTastes() {
		return tastes;
	}

	public void setTastes(Boolean tastes) {
		this.tastes = tastes;
	}

	public Boolean getPrescribedAndForbiddenFoods() {
		return prescribedAndForbiddenFoods;
	}

	public void setPrescribedAndForbiddenFoods(Boolean prescribedAndForbiddenFoods) {
		this.prescribedAndForbiddenFoods = prescribedAndForbiddenFoods;
	}


}
