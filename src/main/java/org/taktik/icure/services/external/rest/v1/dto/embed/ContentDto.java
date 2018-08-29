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

package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.io.Serializable;

public class ContentDto implements Serializable {
    String stringValue;
    Double numberValue;
    Boolean booleanValue;
    Long instantValue;
	Long fuzzyDateValue;
    String documentId;
    MeasureDto measureValue;
    MedicationDto medicationValue;
    byte[] binaryValue;

    public ContentDto() {
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Double getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Double numberValue) {
        this.numberValue = numberValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Long getInstantValue() {
        return instantValue;
    }

    public void setInstantValue(Long instantValue) {
        this.instantValue = instantValue;
    }

	public Long getFuzzyDateValue() {
		return fuzzyDateValue;
	}

	public void setFuzzyDateValue(Long fuzzyDateValue) {
		this.fuzzyDateValue = fuzzyDateValue;
	}

	public byte[] getBinaryValue() {
        return binaryValue;
    }

    public void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

	public MedicationDto getMedicationValue() {
		return medicationValue;
	}

	public void setMedicationValue(MedicationDto medicationValue) {
		this.medicationValue = medicationValue;
	}

    public MeasureDto getMeasureValue() {
        return measureValue;
    }

    public void setMeasureValue(MeasureDto measureValue) {
        this.measureValue = measureValue;
    }

	public static ContentDto fromNumberValue(Integer numberValue) {
		ContentDto contentDto = new ContentDto();
		if (numberValue != null) { contentDto.setNumberValue(numberValue.doubleValue()); }

		return contentDto;
	}

	public static ContentDto fromStringValue(String s) {
		ContentDto contentDto = new ContentDto();
		contentDto.setStringValue(s);

		return contentDto;
	}
}
