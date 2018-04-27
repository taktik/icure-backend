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

//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2015.11.10 à 11:53:46 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.cd.v1.CDCIVILSTATE;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.cd.v1.CDCOUNTRY;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT;


/**
 * <p>Classe Java pour personType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="personType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-PATIENT" maxOccurs="unbounded"/>
 *         &lt;element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="familyname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="birthdate" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}dateType" minOccurs="0"/>
 *         &lt;element name="birthlocation" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}addressTypeBase" minOccurs="0"/>
 *         &lt;element name="deathdate" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}dateType" minOccurs="0"/>
 *         &lt;element name="deathlocation" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}addressTypeBase" minOccurs="0"/>
 *         &lt;element name="sex" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}sexType"/>
 *         &lt;element name="nationality" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-COUNTRY"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="address" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}addressType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="telecom" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}telecomType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="usuallanguage" type="{http://www.w3.org/2001/XMLSchema}language" minOccurs="0"/>
 *         &lt;element name="profession" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}professionType" minOccurs="0"/>
 *         &lt;element name="insurancystatus" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}insuranceType" minOccurs="0"/>
 *         &lt;element name="insurancymembership" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}memberinsuranceType" minOccurs="0"/>
 *         &lt;element name="recorddatetime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="civilstate" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CIVILSTATE"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType", propOrder = {
    "ids",
    "firstnames",
    "familyname",
    "birthdate",
    "birthlocation",
    "deathdate",
    "deathlocation",
    "sex",
    "nationality",
    "addresses",
    "telecoms",
    "usuallanguage",
    "profession",
    "insurancystatus",
    "insurancymembership",
    "recorddatetime",
    "texts",
    "civilstate"
})
public class PersonType
    implements Serializable
{

    private final static long serialVersionUID = 20150901L;
    @XmlElement(name = "id", required = true)
    protected List<IDPATIENT> ids;
    @XmlElement(name = "firstname", required = true)
    protected List<String> firstnames;
    @XmlElement(required = true)
    protected String familyname;
    protected DateType birthdate;
    protected AddressTypeBase birthlocation;
    protected DateType deathdate;
    protected AddressTypeBase deathlocation;
    @XmlElement(required = true)
    protected SexType sex;
    protected PersonType.Nationality nationality;
    @XmlElement(name = "address")
    protected List<AddressType> addresses;
    @XmlElement(name = "telecom")
    protected List<TelecomType> telecoms;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String usuallanguage;
    protected ProfessionType profession;
    protected InsuranceType insurancystatus;
    protected MemberinsuranceType insurancymembership;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar recorddatetime;
    @XmlElement(name = "text")
    protected List<TextType> texts;
    protected PersonType.Civilstate civilstate;

    /**
     * Gets the value of the ids property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ids property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IDPATIENT }
     * 
     * 
     */
    public List<IDPATIENT> getIds() {
        if (ids == null) {
            ids = new ArrayList<IDPATIENT>();
        }
        return this.ids;
    }

    /**
     * Gets the value of the firstnames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the firstnames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFirstnames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFirstnames() {
        if (firstnames == null) {
            firstnames = new ArrayList<String>();
        }
        return this.firstnames;
    }

    /**
     * Obtient la valeur de la propriété familyname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyname() {
        return familyname;
    }

    /**
     * Définit la valeur de la propriété familyname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyname(String value) {
        this.familyname = value;
    }

    /**
     * Obtient la valeur de la propriété birthdate.
     * 
     * @return
     *     possible object is
     *     {@link DateType }
     *     
     */
    public DateType getBirthdate() {
        return birthdate;
    }

    /**
     * Définit la valeur de la propriété birthdate.
     * 
     * @param value
     *     allowed object is
     *     {@link DateType }
     *     
     */
    public void setBirthdate(DateType value) {
        this.birthdate = value;
    }

    /**
     * Obtient la valeur de la propriété birthlocation.
     * 
     * @return
     *     possible object is
     *     {@link AddressTypeBase }
     *     
     */
    public AddressTypeBase getBirthlocation() {
        return birthlocation;
    }

    /**
     * Définit la valeur de la propriété birthlocation.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressTypeBase }
     *     
     */
    public void setBirthlocation(AddressTypeBase value) {
        this.birthlocation = value;
    }

    /**
     * Obtient la valeur de la propriété deathdate.
     * 
     * @return
     *     possible object is
     *     {@link DateType }
     *     
     */
    public DateType getDeathdate() {
        return deathdate;
    }

    /**
     * Définit la valeur de la propriété deathdate.
     * 
     * @param value
     *     allowed object is
     *     {@link DateType }
     *     
     */
    public void setDeathdate(DateType value) {
        this.deathdate = value;
    }

    /**
     * Obtient la valeur de la propriété deathlocation.
     * 
     * @return
     *     possible object is
     *     {@link AddressTypeBase }
     *     
     */
    public AddressTypeBase getDeathlocation() {
        return deathlocation;
    }

    /**
     * Définit la valeur de la propriété deathlocation.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressTypeBase }
     *     
     */
    public void setDeathlocation(AddressTypeBase value) {
        this.deathlocation = value;
    }

    /**
     * Obtient la valeur de la propriété sex.
     * 
     * @return
     *     possible object is
     *     {@link SexType }
     *     
     */
    public SexType getSex() {
        return sex;
    }

    /**
     * Définit la valeur de la propriété sex.
     * 
     * @param value
     *     allowed object is
     *     {@link SexType }
     *     
     */
    public void setSex(SexType value) {
        this.sex = value;
    }

    /**
     * Obtient la valeur de la propriété nationality.
     * 
     * @return
     *     possible object is
     *     {@link PersonType.Nationality }
     *     
     */
    public PersonType.Nationality getNationality() {
        return nationality;
    }

    /**
     * Définit la valeur de la propriété nationality.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType.Nationality }
     *     
     */
    public void setNationality(PersonType.Nationality value) {
        this.nationality = value;
    }

    /**
     * Gets the value of the addresses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addresses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddresses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressType }
     * 
     * 
     */
    public List<AddressType> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<AddressType>();
        }
        return this.addresses;
    }

    /**
     * Gets the value of the telecoms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the telecoms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelecoms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TelecomType }
     * 
     * 
     */
    public List<TelecomType> getTelecoms() {
        if (telecoms == null) {
            telecoms = new ArrayList<TelecomType>();
        }
        return this.telecoms;
    }

    /**
     * Obtient la valeur de la propriété usuallanguage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuallanguage() {
        return usuallanguage;
    }

    /**
     * Définit la valeur de la propriété usuallanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuallanguage(String value) {
        this.usuallanguage = value;
    }

    /**
     * Obtient la valeur de la propriété profession.
     * 
     * @return
     *     possible object is
     *     {@link ProfessionType }
     *     
     */
    public ProfessionType getProfession() {
        return profession;
    }

    /**
     * Définit la valeur de la propriété profession.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfessionType }
     *     
     */
    public void setProfession(ProfessionType value) {
        this.profession = value;
    }

    /**
     * Obtient la valeur de la propriété insurancystatus.
     * 
     * @return
     *     possible object is
     *     {@link InsuranceType }
     *     
     */
    public InsuranceType getInsurancystatus() {
        return insurancystatus;
    }

    /**
     * Définit la valeur de la propriété insurancystatus.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuranceType }
     *     
     */
    public void setInsurancystatus(InsuranceType value) {
        this.insurancystatus = value;
    }

    /**
     * Obtient la valeur de la propriété insurancymembership.
     * 
     * @return
     *     possible object is
     *     {@link MemberinsuranceType }
     *     
     */
    public MemberinsuranceType getInsurancymembership() {
        return insurancymembership;
    }

    /**
     * Définit la valeur de la propriété insurancymembership.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberinsuranceType }
     *     
     */
    public void setInsurancymembership(MemberinsuranceType value) {
        this.insurancymembership = value;
    }

    /**
     * Obtient la valeur de la propriété recorddatetime.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRecorddatetime() {
        return recorddatetime;
    }

    /**
     * Définit la valeur de la propriété recorddatetime.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRecorddatetime(XMLGregorianCalendar value) {
        this.recorddatetime = value;
    }

    /**
     * Gets the value of the texts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the texts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTexts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TextType }
     * 
     * 
     */
    public List<TextType> getTexts() {
        if (texts == null) {
            texts = new ArrayList<TextType>();
        }
        return this.texts;
    }

    /**
     * Obtient la valeur de la propriété civilstate.
     * 
     * @return
     *     possible object is
     *     {@link PersonType.Civilstate }
     *     
     */
    public PersonType.Civilstate getCivilstate() {
        return civilstate;
    }

    /**
     * Définit la valeur de la propriété civilstate.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType.Civilstate }
     *     
     */
    public void setCivilstate(PersonType.Civilstate value) {
        this.civilstate = value;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CIVILSTATE"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "cd"
    })
    public static class Civilstate
        implements Serializable
    {

        private final static long serialVersionUID = 20150901L;
        @XmlElement(required = true)
        protected CDCIVILSTATE cd;

        /**
         * Obtient la valeur de la propriété cd.
         * 
         * @return
         *     possible object is
         *     {@link CDCIVILSTATE }
         *     
         */
        public CDCIVILSTATE getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDCIVILSTATE }
         *     
         */
        public void setCd(CDCIVILSTATE value) {
            this.cd = value;
        }

    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-COUNTRY"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "cd"
    })
    public static class Nationality
        implements Serializable
    {

        private final static long serialVersionUID = 20150901L;
        @XmlElement(required = true)
        protected CDCOUNTRY cd;

        /**
         * Obtient la valeur de la propriété cd.
         * 
         * @return
         *     possible object is
         *     {@link CDCOUNTRY }
         *     
         */
        public CDCOUNTRY getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDCOUNTRY }
         *     
         */
        public void setCd(CDCOUNTRY value) {
            this.cd = value;
        }

    }

}
