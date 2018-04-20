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

package org.taktik.icure.services.external.rest.v1.dto.be.chapter4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.schema.v1.QuantityType;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Long: 11/11/13
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
    private Long start;
    private Long end;
    private Double unitNumber;
    private QuantityType quantity;
    private String ioRequestReference;

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

    public void setUnitNumber(Double unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void setStrength(QuantityType quantity) {
        this.quantity = quantity;
    }

    public Double getUnitNumber() {
        return unitNumber;
    }

	public QuantityType getQuantity() {
		return quantity;
	}

	public void setQuantity(QuantityType quantity) {
		this.quantity = quantity;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
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
}
