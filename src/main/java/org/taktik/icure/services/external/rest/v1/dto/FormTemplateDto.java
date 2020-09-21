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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.services.external.rest.v1.dto.embed.FormGroupDto;
import org.taktik.icure.services.external.rest.v1.dto.gui.layout.FormLayout;

import java.io.Serializable;
import java.util.List;

public class FormTemplateDto extends StoredDto implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(FormTemplateDto.class);

    protected FormGroupDto group;

    protected String name;
    protected String descr;

    protected Boolean hidden;

    protected CodeDto specialty;

    protected String guid;

    protected FormLayout layout;

    //Location in the form of a gpath/xpath like location with an optional action
    //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction[descr='Follow-up'] : add inside the follow-up plan of action of a specific healthElement
    //ex: healthElements[codes[type == 'ICD' and code == 'I80']].plansOfAction += [descr:'Follow-up'] : create a new planOfAction and add inside it
    protected String formInstancePreferredLocation;

    protected String keyboardShortcut;

    protected String shortReport;
    protected String mediumReport;
    protected String longReport;

    protected String disabled;

    protected List<String> reports;

    protected List<CodeDto> tags;

    public FormTemplateDto() {
	}

	public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        FormTemplateDto.logger = logger;
    }

    public FormGroupDto getGroup() {
        return group;
    }

    public void setGroup(FormGroupDto group) {
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

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public CodeDto getSpecialty() {
        return specialty;
    }

    public void setSpecialty(CodeDto specialty) {
        this.specialty = specialty;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public FormLayout getLayout() {
        return layout;
    }

    public void setLayout(FormLayout layout) {
        this.layout = layout;
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

    public String getLongReport() {
        return longReport;
    }

    public void setLongReport(String longReport) {
        this.longReport = longReport;
    }

    public String getMediumReport() {
        return mediumReport;
    }

    public void setMediumReport(String mediumReport) {
        this.mediumReport = mediumReport;
    }

    public String getShortReport() {
        return shortReport;
    }

    public void setShortReport(String shortReport) {
        this.shortReport = shortReport;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setReports(List<String> reports) {
        this.reports = reports;
    }

    public String getDisabled() { return disabled; }

    public void setDisabled(String disabled) { this.disabled = disabled; }

    public List<CodeDto> getTags() {
        return tags;
    }

    public void setTags(List<CodeDto> tags) {
        this.tags = tags;
    }
}
