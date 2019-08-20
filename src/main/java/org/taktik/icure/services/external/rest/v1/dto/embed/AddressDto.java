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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.NotNull;
import org.taktik.icure.services.external.rest.v1.dto.EncryptableDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aduchate on 21/01/13, 14:43
 */
//@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
public class AddressDto implements Serializable, Location, Comparable<AddressDto>, EncryptableDto {

	protected String objectType;

	protected AddressType addressType;

    protected String descr;
    protected String street;
    protected String houseNumber;
    protected String postboxNumber;
    protected String postalCode;
    protected String city;
    protected String country;
	protected String encryptedSelf;

	protected List<TelecomDtoEmbed> telecoms = new ArrayList<>();

	@Override
	public String getObjectType() {
		return objectType;
	}

	@Override
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostboxNumber() {
        return postboxNumber;
    }

    public void setPostboxNumber(String postboxNumber) {
        this.postboxNumber = postboxNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public List<TelecomDtoEmbed> getTelecoms() {
		return telecoms;
	}

	public void setTelecoms(List<TelecomDtoEmbed> telecoms) {
		this.telecoms = telecoms;
	}

	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	@JsonIgnore
    String findMobile() {
		for (TelecomDtoEmbed t: telecoms) {
			if (TelecomType.mobile.equals(t.getTelecomType())) {
				return t.getTelecomNumber();
			}
		}
		return null;
	}

	@JsonIgnore
	void setMobile(String value) {
		for (TelecomDtoEmbed t: telecoms) {
			if (TelecomType.mobile.equals(t.getTelecomType())) {
				t.setTelecomNumber(value);
			}
		}
		if (value!=null) {
			telecoms.add(new TelecomDtoEmbed(TelecomType.mobile, value));
		}
	}

	@Override
	public int compareTo(@NotNull AddressDto o) {
		if (addressType == null && o.addressType == null) {
			return 0;
		} else if (addressType == null && o.addressType != null) {
			return -1;
		} else if (o.addressType == null) {
			return 1;
		} else {
			return addressType.ordinal() - o.addressType.ordinal();
		}
	}

//	void sortTelecoms() {
//		Collections.sort(telecoms, new Comparator<TelecomDtoEmbed>() {
//
//            @Override
//            public int compare(TelecomDtoEmbed arg0, TelecomDtoEmbed arg1) {
//                if (arg0.getTelecomType() == null && arg1.getTelecomType() == null) return 0;
//                else if (arg0.getTelecomType() != null && arg1.getTelecomType() == null) return -1;
//                else if (arg1.getTelecomType() != null && arg0.getTelecomType() == null) return 1;
//                return arg0.getTelecomType().compareTo(arg1.getTelecomType());
//            }
//        });
//
//	}

}
