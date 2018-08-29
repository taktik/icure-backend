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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.utils.InstantDeserializer;
import org.taktik.icure.utils.InstantSerializer;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content implements Serializable {
    @JsonProperty("s")
    String stringValue;

    @JsonProperty("n")
    Double numberValue;

    @JsonProperty("b")
    Boolean booleanValue;

    @JsonProperty("i")
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
    Instant instantValue;

	@JsonProperty("dt")
	Long fuzzyDateValue;

	@JsonProperty("x")
    byte[] binaryValue;

    @JsonProperty("d")
    String documentId;

    @JsonProperty("m")
    Measure measureValue;

    @JsonProperty("p")
    Medication medicationValue;

	@JsonProperty("c")
	Map<String, Service> compoundValue;

	@JsonProperty("l")
	List<Service> multipleValue;

	@JsonIgnore
	byte[] compressedStringValue;

    public Content() {
    }

    public Content(String stringValue) {
        this.stringValue = stringValue;
    }

    public Content(Double numberValue) {
        this.numberValue = numberValue;
    }

    public Content(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Content(Instant instantValue) {
        this.instantValue = instantValue;
    }

    public Content(Measure measureValue) {
        this.measureValue = measureValue;
    }

    public Content(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    public Content(Medication medicationValue) {
        this.medicationValue = medicationValue;
    }

    public @Nullable String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public @Nullable Double getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Double numberValue) {
        this.numberValue = numberValue;
    }

    public @Nullable Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public @Nullable Instant getInstantValue() {
        return instantValue;
    }

    public void setInstantValue(Instant instantValue) {
        this.instantValue = instantValue;
    }

	public  @Nullable Long getFuzzyDateValue() {
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

    public @Nullable String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public @Nullable Measure getMeasureValue() {
        return measureValue;
    }

    public void setMeasureValue(Measure measureValue) {
        this.measureValue = measureValue;
    }

    public @Nullable Medication getMedicationValue() {
        return medicationValue;
    }

    public void setMedicationValue(Medication medicationValue) {
        this.medicationValue = medicationValue;
    }

	public Map<String, Service> getCompoundValue() {
		return compoundValue;
	}

	public void setCompoundValue(Map<String, Service> compoundValue) {
		this.compoundValue = compoundValue;
	}

	public List<Service> getMultipleValue() {
		return multipleValue;
	}

	public void setMultipleValue(List<Service> multipleValue) {
		this.multipleValue = multipleValue;
	}

	public byte[] getCompressedStringValue() {
		return compressedStringValue;
	}

	public void setCompressedStringValue(byte[] compressedStringValue) {
		this.compressedStringValue = compressedStringValue;
	}
}
