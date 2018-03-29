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

package org.taktik.icure.be.ehealth.dto.chapter4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto;
import org.taktik.icure.services.external.rest.v1.dto.MessageDto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 11/11/13
 * Time: 08:04
 * To change this template use File | Settings | File Templates.
 */
public class AgreementTransaction implements Serializable {
	private String responseType;
    private String paragraph;
    private boolean accepted;
    private boolean inTreatment;
    private String careProviderReference;
    private String decisionReference;
    private Date start;
    private Date end;
    private Double unitNumber;
	private Double quantityValue;
    private String quantityUnit;
    private String ioRequestReference;
	@Nullable
    private byte[] content;
	@Nullable
	public DocumentDto document;
	@Nullable
	public MessageDto message;

	public AgreementTransaction() {
    }

    public AgreementTransaction(String metas) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String,String> foo = (Map<String,String>) mapper.readValue(metas, Object.class);
            this.paragraph = foo.get("paragraph");
            this.careProviderReference = foo.get("myref");
            this.decisionReference = foo.get("dref");
            this.ioRequestReference = foo.get("iorref");
            this.inTreatment = "intreatment".equals(foo.get("response"));
            this.accepted = "accepted".equals(foo.get("response"));
        } catch (IOException e) {
            //Fail silently
        }
    }

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isInTreatment() {
        return inTreatment;
    }

    public void setInTreatment(boolean inTreatment) {
        this.inTreatment = inTreatment;
    }

    public String getCareProviderReference() {
        return careProviderReference;
    }

    public void setCareProviderReference(String careProviderReference) {
        this.careProviderReference = careProviderReference;
    }

    public String getDecisionReference() {
        return decisionReference;
    }

    public void setDecisionReference(String decisionReference) {
        this.decisionReference = decisionReference;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setUnitNumber(Double unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Double getUnitNumber() {
        return unitNumber;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public void setIoRequestReference(String ioRequestReference) {
        this.ioRequestReference = ioRequestReference;
    }

    public String getIoRequestReference() {
        return ioRequestReference;
    }

	@Nullable
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Nullable
	public DocumentDto getDocument() {
		return document;
	}

	public void setDocument(@Nullable DocumentDto document) {
		this.document = document;
	}

	@Nullable
	public MessageDto getMessage() {
		return message;
	}

	public void setMessage(@Nullable MessageDto message) {
		this.message = message;
	}
}
