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

package org.taktik.icure.services.external.rest.v1.dto.be.chapter4;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 11/06/13
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public class AgreementRequest implements Serializable {
    String patientId;
    Boolean continuous;
    Boolean incomplete;
    Long start;
    Long end;
    String decisionReference;
	String ioReference;
    String paragraph;
    List<String> verses;
    List<Appendix> appendices;
    String civicsVersion;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Boolean getContinuous() {
        return continuous;
    }

    public void setContinuous(Boolean continuous) {
        this.continuous = continuous;
    }

    public Boolean getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(Boolean incomplete) {
        this.incomplete = incomplete;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

	public String getIoReference() {
		return ioReference;
	}

	public void setIoReference(String ioReference) {
		this.ioReference = ioReference;
	}

	public String getDecisionReference() {
        return decisionReference;
    }

    public void setDecisionReference(String decisionReference) {
        this.decisionReference = decisionReference;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public List<String> getVerses() {
        return verses;
    }

    public void setVerses(List<String> verses) {
        this.verses = verses;
    }

    public List<Appendix> getAppendices() {
        return appendices;
    }

    public void setAppendices(List<Appendix> appendices) {
        this.appendices = appendices;
    }

    public String getCivicsVersion() {
        return civicsVersion;
    }

    public void setCivicsVersion(String civicsVersion) {
        this.civicsVersion = civicsVersion;
    }
}
