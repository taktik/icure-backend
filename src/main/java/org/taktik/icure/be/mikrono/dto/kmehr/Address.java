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

/*
 * Copyright (c) 2010. Taktik SA.
 *
 * This file is part of JoepieViewer.
 *
 * JoepieViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JoepieViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JoepieViewer.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.taktik.icure.be.mikrono.dto.kmehr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.taktik.icure.be.ehealth.logic.kmehr.KmehrUtils;

/**
 * Created by IntelliJ IDEA.
 * User: aduchate
 * Date: 15 sept. 2010
 * Time: 15:33:58
 * To change this template use File | Settings | File Templates.
 */
public class Address extends KmehrElement {
    String countryCode;

    String zip;
    String nis;
    String city;
    String district;
    String street;
    String houseNumber;
    String postboxNumber;

    String text;

    public Address() {
    }

    ;

    public Address(String type, String street, String houseNumber, String postboxNumber, String zip, String city, String country) {
        addType(type);
        this.street = street;
        this.houseNumber = houseNumber;
        this.postboxNumber = postboxNumber;
        this.zip = zip;
        this.city = city;
        this.countryCode = country;
    }

    public Address(String street, String houseNumber, String postboxNumber, String zip, String city, String country) {
        this("CD-ADDRESS:home", street, houseNumber, postboxNumber, zip, city, country);
    }

    public Address(String text) {
        this.text = text;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonIgnore
    public String getFullStreetAddress() {
        return ((text != null ? text : "") + (street != null ? " " + street : "") + (houseNumber != null ? ", " + houseNumber : "") + (postboxNumber != null ? " b" + postboxNumber : "")).trim().replaceAll("  ", " ");
    }

    @JsonIgnore
    public String getFullLocality() {
        return ((zip != null ? zip : "") + (city != null ? " " + city : "") + (district != null ? " (" + district + ") " : "")).trim().replaceAll("  ", " ");
    }

    @JsonIgnore
    public String getFullAddress() {
        return (getFullStreetAddress() + " " + getFullLocality() + " " + (countryCode != null ? KmehrUtils.getValue(countryCode) : "")).trim();
    }
}
