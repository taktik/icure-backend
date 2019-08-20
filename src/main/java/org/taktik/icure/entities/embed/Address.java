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
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.Encryptable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by aduchate on 21/01/13, 14:43
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable, Comparable<Address>, Encryptable {

	protected AddressType addressType;

    protected String descr;
    protected String street;
    protected String houseNumber;
    protected String postboxNumber;
    protected String postalCode;
    protected String city;
    protected String country;
	protected String encryptedSelf;

	protected List<Telecom> telecoms = new LinkedList<>();

	public Address() {
	}

	public Address(AddressType addressType) {
		this.addressType = addressType;
	}


	public @Nullable AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public @Nullable String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public @Nullable String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public @Nullable String getPostboxNumber() {
        return postboxNumber;
    }

    public void setPostboxNumber(String postboxNumber) {
        this.postboxNumber = postboxNumber;
    }

    public @Nullable String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public @Nullable String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public @Nullable String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	public @Nullable String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public List<Telecom> getTelecoms() {
		return telecoms;
	}

	public void setTelecoms(List<Telecom> telecoms) {
		this.telecoms = telecoms;
	}

	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	public void mergeFrom(Address other) {
		if (this.descr == null && other.descr != null) { this.descr = other.descr; }
		if (this.street == null && other.street != null) { this.street = other.street; }
		if (this.houseNumber == null && other.houseNumber != null) { this.houseNumber = other.houseNumber; }
		if (this.postboxNumber == null && other.postboxNumber != null) { this.postboxNumber = other.postboxNumber; }
		if (this.postalCode == null && other.postalCode != null) { this.postalCode = other.postalCode; }
		if (this.city == null && other.city != null) { this.city = other.city; }
		if (this.country == null && other.country != null) { this.country = other.country; }
		if (this.encryptedSelf == null && other.encryptedSelf != null) { this.encryptedSelf = other.encryptedSelf; }

		for (Telecom fromTelecom :other.telecoms) {
			Optional<Telecom> destTelecom = this.getTelecoms().stream().filter(telecom -> telecom.getTelecomType() == fromTelecom.getTelecomType()).findAny();
			if (destTelecom.isPresent()) {
				destTelecom.orElseThrow(IllegalStateException::new).mergeFrom(fromTelecom);
			} else {
				this.getTelecoms().add(fromTelecom);
			}
		}
	}

	public void forceMergeFrom(Address other) {
		if (other.descr != null) { this.descr = other.descr; }
		if (other.street != null) { this.street = other.street; }
		if (other.houseNumber != null) { this.houseNumber = other.houseNumber; }
		if (other.postboxNumber != null) { this.postboxNumber = other.postboxNumber; }
		if (other.postalCode != null) { this.postalCode = other.postalCode; }
		if (other.city != null) { this.city = other.city; }
		if (other.country != null) { this.country = other.country; }
		if (other.encryptedSelf != null) { this.encryptedSelf = other.encryptedSelf; }

		for (Telecom fromTelecom:other.telecoms) {
			Optional<Telecom> destTelecom = this.getTelecoms().stream().filter(telecom -> telecom.getTelecomType() == fromTelecom.getTelecomType()).findAny();
			if (destTelecom.isPresent()) {
				destTelecom.orElseThrow(IllegalStateException::new).forceMergeFrom(fromTelecom);
			} else {
				this.getTelecoms().add(fromTelecom);
			}
		}
	}


	@JsonIgnore
    String findMobile() {
		for (Telecom t:telecoms) {
			if (TelecomType.mobile.equals(t.getTelecomType())) {
				return t.getTelecomNumber();
			}
		}
		return null;
	}

	@JsonIgnore
	void setMobile(String value) {
		for (Telecom t:telecoms) {
			if (TelecomType.mobile.equals(t.getTelecomType())) {
				t.setTelecomNumber(value);
			}
		}
		if (value!=null) {
			telecoms.add(new Telecom(TelecomType.mobile, value));
		}
	}

	@Override
	public int compareTo(Address other) {
		return this.addressType.compareTo(other.addressType);
	}


}
