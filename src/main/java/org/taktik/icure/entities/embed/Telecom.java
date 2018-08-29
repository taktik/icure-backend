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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Created by aduchate on 21/01/13, 14:47
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Telecom implements Serializable, Comparable<Telecom> {

    protected TelecomType telecomType;
    protected String telecomNumber;
    protected String telecomDescription;

	public Telecom() {
	}

	public Telecom(TelecomType telecomType, String telecomNumber) {
		this.telecomType = telecomType;
		this.telecomNumber = telecomNumber;
	}

	public Telecom(TelecomType telecomType, String telecomNumber, String telecomDescription) {
		this.telecomType = telecomType;
		this.telecomNumber = telecomNumber;
		this.telecomDescription = telecomDescription;
	}

	public @Nullable TelecomType getTelecomType() {
        return telecomType;
    }

    public void setTelecomType(TelecomType telecomType) {
        this.telecomType = telecomType;
    }

    public @Nullable String getTelecomNumber() {
        return telecomNumber;
    }

    public void setTelecomNumber(String telecomNumber) {
        this.telecomNumber = telecomNumber;
    }

	public @Nullable String getTelecomDescription() {
		return telecomDescription;
	}

	public void setTelecomDescription(String telecomDescription) {
		this.telecomDescription = telecomDescription;
	}

	@Override
	public int compareTo(Telecom other) {
		return this.telecomType.compareTo(other.telecomType);
	}

	public void mergeFrom(Telecom other) {
		if (this.telecomNumber == null && other.telecomNumber != null) { this.telecomNumber = other.telecomNumber; }
	}

	public void forceMergeFrom(Telecom other) {
		if (other.telecomNumber != null) { this.telecomNumber = other.telecomNumber; }
	}

}
