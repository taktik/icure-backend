/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: aduchate
 * Date: 15 sept. 2010
 * Time: 12:06:22
 * To change this template use File | Settings | File Templates.
 */
public class Person extends KmehrElement {
  String firstname;
  String familyname;
  Date birthdate;
  Address birthlocation;
  Date deathdate;
  Address deathlocation;
  String sex;
  String nationality;

  List<Address> addresses = new ArrayList<>();
  List<Telecom> telecoms = new ArrayList<>();

  String usuallanguage;
  String profession;
  Date recorddatetime;
  List<String> comments = new ArrayList<String>();

  public void addAddress(Address a) {
    addresses.add(a);
  }

  public void addTelecom(Telecom t) {
    telecoms.add(t);
  }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Address getBirthlocation() {
        return birthlocation;
    }

    public void setBirthlocation(Address birthlocation) {
        this.birthlocation = birthlocation;
    }

    public Date getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(Date deathdate) {
        this.deathdate = deathdate;
    }

    public Address getDeathlocation() {
        return deathlocation;
    }

    public void setDeathlocation(Address deathlocation) {
        this.deathlocation = deathlocation;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

   public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Telecom> getTelecoms() {
        return telecoms;
    }

    public void setTelecoms(List<Telecom> telecoms) {
        this.telecoms = telecoms;
    }

    public String getUsuallanguage() {
        return usuallanguage;
    }

    public void setUsuallanguage(String usuallanguage) {
        this.usuallanguage = usuallanguage;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Date getRecorddatetime() {
        return recorddatetime;
    }

    public void setRecorddatetime(Date recorddatetime) {
        this.recorddatetime = recorddatetime;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

}
