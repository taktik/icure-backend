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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.FormGroup;

import java.util.List;

/**
 * Created by aduchate on 09/07/13, 16:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormTemplate extends StoredDocument {
	@JsonIgnore
	protected byte[] layout;

	protected String layoutAttachmentId;

    protected FormGroup group;

    protected String name;
    protected String descr;

    protected String disabled;

    protected Code specialty; //Always CD-HCPARTY

    //Globally unique and consistent accross all DBs that get their formTemplate from a icure cloud library
    //The id is not guaranteed to be consistent accross dbs
    protected String guid;

	protected String author; //userId

    //Location in the form of a gpath/xpath like location with an optional action
    //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction[descr='Follow-up'] : add inside the follow-up plan of action of a specific healthElement
    //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction += [descr:'Follow-up'] : create a new planOfAction and add inside it
    protected String formInstancePreferredLocation;

    protected String keyboardShortcut;

    protected String shortReport;
    protected String mediumReport;
    protected String longReport;

    protected List<String> reports;

    protected List<Code> tags;

    @JsonIgnore
	private transient boolean attachmentDirty = false;

	@JsonIgnore
	public boolean isAttachmentDirty() {
		return attachmentDirty;
	}

	@JsonIgnore
	public void setAttachmentDirty(boolean attachmentDirty) {
		this.attachmentDirty = attachmentDirty;
	}

	public void setLayout(byte[] data) {
        this.layout = data;
    }

	public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public FormGroup getGroup() {
        return group;
    }

    public void setGroup(FormGroup group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public Code getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Code specialty) {
        this.specialty = specialty;
    }

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

	public byte[] getLayout() {
		return layout;
	}

	public String getLayoutAttachmentId() {
		return layoutAttachmentId;
	}

	public void setLayoutAttachmentId(String layoutAttachmentId) {
		this.layoutAttachmentId = layoutAttachmentId;
	}

    public String getFormInstancePreferredLocation() {
        return formInstancePreferredLocation;
    }

    public void setFormInstancePreferredLocation(String formInstancePreferredLocation) {
        this.formInstancePreferredLocation = formInstancePreferredLocation;
    }

    public String getKeyboardShortcut() {
        return keyboardShortcut;
    }

    public void setKeyboardShortcut(String keyboardShortcut) {
        this.keyboardShortcut = keyboardShortcut;
    }

    public String getShortReport() {
        return shortReport;
    }

    public void setShortReport(String shortReport) {
        this.shortReport = shortReport;
    }

    public String getMediumReport() {
        return mediumReport;
    }

    public void setMediumReport(String mediumReport) {
        this.mediumReport = mediumReport;
    }

    public String getLongReport() {
        return longReport;
    }

    public void setLongReport(String longReport) {
        this.longReport = longReport;
    }

	public List<String> getReports() {
		return reports;
	}

	public void setReports(List<String> reports) {
		this.reports = reports;
	}

    public List<Code> getTags() {
        return tags;
    }

    public void setTags(List<Code> tags) {
        this.tags = tags;
    }
}
