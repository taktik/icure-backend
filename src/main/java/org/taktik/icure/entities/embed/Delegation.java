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

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by aduchate on 29/03/13, 18:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Delegation implements Serializable {
    //This is in no way the owner of a piece of date (patient, contact). It is the owner of the delegation.
    String owner; // owner id
    String delegatedTo; // delegatedTo id
    String key; // An arbitrary key (generated, patientId, any ID, etc.), usually prefixed with the entity ID followed by ":", encrypted using an exchange AES key.

	String tag; // A cleartext tag, unique to each client ('org.taktik.icure.delegation.Tag.sensitivemedicaldata' for example)

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDelegatedTo() {
		return delegatedTo;
	}

	public void setDelegatedTo(String delegatedTo) {
		this.delegatedTo = delegatedTo;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Delegation that = (Delegation) o;
		return Objects.equals(owner, that.owner) &&
				Objects.equals(delegatedTo, that.delegatedTo) &&
				Objects.equals(key, that.key) &&
				Objects.equals(tag, that.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(owner, delegatedTo, key, tag);
	}
}
