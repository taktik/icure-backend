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

//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2015.03.05 à 11:48:06 AM CET 
//


package org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDINNCLUSTER;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;


/**
 * to specify the value of the item
 * 
 * <p>Classe Java pour contentType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="contentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-KMEHR" maxOccurs="unbounded"/>
 *           &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CONTENT" maxOccurs="unbounded"/>
 *           &lt;element name="decimal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *           &lt;element name="unsignedInt" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *           &lt;element name="boolean" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *           &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" maxOccurs="unbounded"/>
 *           &lt;choice>
 *             &lt;choice>
 *               &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}gYear"/>
 *               &lt;element name="yearmonth" type="{http://www.w3.org/2001/XMLSchema}gYearMonth"/>
 *             &lt;/choice>
 *             &lt;sequence>
 *               &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *               &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *             &lt;/sequence>
 *           &lt;/choice>
 *           &lt;element name="hcparty" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}hcpartyType"/>
 *           &lt;element name="person" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}personType"/>
 *           &lt;element name="insurance" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}insuranceType"/>
 *           &lt;element name="incapacity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}incapacityType"/>
 *           &lt;element name="error" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}errorType"/>
 *           &lt;choice>
 *             &lt;sequence>
 *               &lt;choice>
 *                 &lt;element name="medicinalproduct">
 *                   &lt;complexType>
 *                     &lt;complexContent>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;sequence>
 *                           &lt;element name="intendedcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK"/>
 *                           &lt;element name="deliveredcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK" minOccurs="0"/>
 *                           &lt;element name="intendedname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;element name="deliveredname" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *                         &lt;/sequence>
 *                       &lt;/restriction>
 *                     &lt;/complexContent>
 *                   &lt;/complexType>
 *                 &lt;/element>
 *                 &lt;element name="substanceproduct">
 *                   &lt;complexType>
 *                     &lt;complexContent>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                         &lt;sequence>
 *                           &lt;element name="intendedcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-INNCLUSTER"/>
 *                           &lt;element name="deliveredcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK" minOccurs="0"/>
 *                           &lt;element name="intendedname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;element name="deliveredname" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *                         &lt;/sequence>
 *                       &lt;/restriction>
 *                     &lt;/complexContent>
 *                   &lt;/complexType>
 *                 &lt;/element>
 *                 &lt;element name="compoundprescription" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}compoundprescriptionType"/>
 *               &lt;/choice>
 *             &lt;/sequence>
 *             &lt;element name="medication" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}medicationType"/>
 *           &lt;/choice>
 *           &lt;element name="holter" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}holterType"/>
 *           &lt;element name="ecg" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *           &lt;element name="bacteriology" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *           &lt;element name="lnk" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}lnkType" maxOccurs="unbounded"/>
 *           &lt;element name="location" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}locationBirthPlaceType"/>
 *         &lt;/choice>
 *         &lt;element name="unit" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}unitType" minOccurs="0"/>
 *         &lt;element name="minref" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}minrefType" minOccurs="0"/>
 *         &lt;element name="maxref" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}maxrefType" minOccurs="0"/>
 *         &lt;element name="refscope" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}refscopeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contentType", propOrder = {
    "location",
    "lnks",
    "bacteriology",
    "ecg",
    "holter",
    "medication",
    "compoundprescription",
    "substanceproduct",
    "medicinalproduct",
    "error",
    "incapacity",
    "insurance",
    "person",
    "hcparty",
    "date",
    "time",
    "yearmonth",
    "year",
    "texts",
    "_boolean",
    "unsignedInt",
    "decimal",
    "cds",
    "ids",
    "unit",
    "minref",
    "maxref",
    "refscopes"
})
public class ContentType
    implements Serializable
{

    private final static long serialVersionUID = 20121001L;
    protected LocationBirthPlaceType location;
    @XmlElement(name = "lnk")
    protected List<LnkType> lnks;
    protected TextType bacteriology;
    protected TextType ecg;
    protected HolterType holter;
    protected MedicationType medication;
    protected CompoundprescriptionType compoundprescription;
    protected ContentType.Substanceproduct substanceproduct;
    protected ContentType.Medicinalproduct medicinalproduct;
    protected ErrorType error;
    protected IncapacityType incapacity;
    protected InsuranceType insurance;
    protected PersonType person;
    protected HcpartyType hcparty;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar time;
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar yearmonth;
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar year;
    @XmlElement(name = "text")
    protected List<TextType> texts;
    @XmlElement(name = "boolean")
    protected Boolean _boolean;
    @XmlSchemaType(name = "unsignedInt")
    protected Long unsignedInt;
    protected BigDecimal decimal;
    @XmlElement(name = "cd")
    protected List<CDCONTENT> cds;
    @XmlElement(name = "id")
    protected List<IDKMEHR> ids;
    protected UnitType unit;
    protected MinrefType minref;
    protected MaxrefType maxref;
    @XmlElement(name = "refscope")
    protected List<RefscopeType> refscopes;

    /**
     * Obtient la valeur de la propriété location.
     *
     * @return
     *     possible object is
     *     {@link LocationBirthPlaceType }
     *
     */
    public LocationBirthPlaceType getLocation() {
        return location;
    }

    /**
     * Définit la valeur de la propriété location.
     *
     * @param value
     *     allowed object is
     *     {@link LocationBirthPlaceType }
     *
     */
    public void setLocation(LocationBirthPlaceType value) {
        this.location = value;
    }

    /**
     * Gets the value of the lnks property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lnks property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLnks().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LnkType }
     *
     *
     */
    public List<LnkType> getLnks() {
        if (lnks == null) {
            lnks = new ArrayList<LnkType>();
        }
        return this.lnks;
    }

    /**
     * Obtient la valeur de la propriété bacteriology.
     *
     * @return
     *     possible object is
     *     {@link TextType }
     *
     */
    public TextType getBacteriology() {
        return bacteriology;
    }

    /**
     * Définit la valeur de la propriété bacteriology.
     *
     * @param value
     *     allowed object is
     *     {@link TextType }
     *
     */
    public void setBacteriology(TextType value) {
        this.bacteriology = value;
    }

    /**
     * Obtient la valeur de la propriété ecg.
     *
     * @return
     *     possible object is
     *     {@link TextType }
     *
     */
    public TextType getEcg() {
        return ecg;
    }

    /**
     * Définit la valeur de la propriété ecg.
     *
     * @param value
     *     allowed object is
     *     {@link TextType }
     *
     */
    public void setEcg(TextType value) {
        this.ecg = value;
    }

    /**
     * Obtient la valeur de la propriété holter.
     *
     * @return
     *     possible object is
     *     {@link HolterType }
     *
     */
    public HolterType getHolter() {
        return holter;
    }

    /**
     * Définit la valeur de la propriété holter.
     *
     * @param value
     *     allowed object is
     *     {@link HolterType }
     *
     */
    public void setHolter(HolterType value) {
        this.holter = value;
    }

    /**
     * Obtient la valeur de la propriété medication.
     *
     * @return
     *     possible object is
     *     {@link MedicationType }
     *
     */
    public MedicationType getMedication() {
        return medication;
    }

    /**
     * Définit la valeur de la propriété medication.
     *
     * @param value
     *     allowed object is
     *     {@link MedicationType }
     *
     */
    public void setMedication(MedicationType value) {
        this.medication = value;
    }

    /**
     * Obtient la valeur de la propriété compoundprescription.
     *
     * @return
     *     possible object is
     *     {@link CompoundprescriptionType }
     *
     */
    public CompoundprescriptionType getCompoundprescription() {
        return compoundprescription;
    }

    /**
     * Définit la valeur de la propriété compoundprescription.
     *
     * @param value
     *     allowed object is
     *     {@link CompoundprescriptionType }
     *
     */
    public void setCompoundprescription(CompoundprescriptionType value) {
        this.compoundprescription = value;
    }

    /**
     * Obtient la valeur de la propriété substanceproduct.
     *
     * @return
     *     possible object is
     *     {@link ContentType.Substanceproduct }
     *
     */
    public ContentType.Substanceproduct getSubstanceproduct() {
        return substanceproduct;
    }

    /**
     * Définit la valeur de la propriété substanceproduct.
     *
     * @param value
     *     allowed object is
     *     {@link ContentType.Substanceproduct }
     *
     */
    public void setSubstanceproduct(ContentType.Substanceproduct value) {
        this.substanceproduct = value;
    }

    /**
     * Obtient la valeur de la propriété medicinalproduct.
     *
     * @return
     *     possible object is
     *     {@link ContentType.Medicinalproduct }
     *
     */
    public ContentType.Medicinalproduct getMedicinalproduct() {
        return medicinalproduct;
    }

    /**
     * Définit la valeur de la propriété medicinalproduct.
     *
     * @param value
     *     allowed object is
     *     {@link ContentType.Medicinalproduct }
     *
     */
    public void setMedicinalproduct(ContentType.Medicinalproduct value) {
        this.medicinalproduct = value;
    }

    /**
     * Obtient la valeur de la propriété error.
     *
     * @return
     *     possible object is
     *     {@link ErrorType }
     *
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * Définit la valeur de la propriété error.
     *
     * @param value
     *     allowed object is
     *     {@link ErrorType }
     *
     */
    public void setError(ErrorType value) {
        this.error = value;
    }

    /**
     * Obtient la valeur de la propriété incapacity.
     *
     * @return
     *     possible object is
     *     {@link IncapacityType }
     *
     */
    public IncapacityType getIncapacity() {
        return incapacity;
    }

    /**
     * Définit la valeur de la propriété incapacity.
     *
     * @param value
     *     allowed object is
     *     {@link IncapacityType }
     *
     */
    public void setIncapacity(IncapacityType value) {
        this.incapacity = value;
    }

    /**
     * Obtient la valeur de la propriété insurance.
     *
     * @return
     *     possible object is
     *     {@link InsuranceType }
     *
     */
    public InsuranceType getInsurance() {
        return insurance;
    }

    /**
     * Définit la valeur de la propriété insurance.
     *
     * @param value
     *     allowed object is
     *     {@link InsuranceType }
     *
     */
    public void setInsurance(InsuranceType value) {
        this.insurance = value;
    }

    /**
     * Obtient la valeur de la propriété person.
     *
     * @return
     *     possible object is
     *     {@link PersonType }
     *
     */
    public PersonType getPerson() {
        return person;
    }

    /**
     * Définit la valeur de la propriété person.
     *
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *
     */
    public void setPerson(PersonType value) {
        this.person = value;
    }

    /**
     * Obtient la valeur de la propriété hcparty.
     *
     * @return
     *     possible object is
     *     {@link HcpartyType }
     *
     */
    public HcpartyType getHcparty() {
        return hcparty;
    }

    /**
     * Définit la valeur de la propriété hcparty.
     *
     * @param value
     *     allowed object is
     *     {@link HcpartyType }
     *
     */
    public void setHcparty(HcpartyType value) {
        this.hcparty = value;
    }

    /**
     * Obtient la valeur de la propriété date.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Définit la valeur de la propriété date.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Obtient la valeur de la propriété time.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Définit la valeur de la propriété time.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Obtient la valeur de la propriété yearmonth.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getYearmonth() {
        return yearmonth;
    }

    /**
     * Définit la valeur de la propriété yearmonth.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setYearmonth(XMLGregorianCalendar value) {
        this.yearmonth = value;
    }

    /**
     * Obtient la valeur de la propriété year.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getYear() {
        return year;
    }

    /**
     * Définit la valeur de la propriété year.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setYear(XMLGregorianCalendar value) {
        this.year = value;
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
     * Obtient la valeur de la propriété boolean.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isBoolean() {
        return _boolean;
    }

    /**
     * Définit la valeur de la propriété boolean.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setBoolean(Boolean value) {
        this._boolean = value;
    }

    /**
     * Obtient la valeur de la propriété unsignedInt.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getUnsignedInt() {
        return unsignedInt;
    }

    /**
     * Définit la valeur de la propriété unsignedInt.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setUnsignedInt(Long value) {
        this.unsignedInt = value;
    }

    /**
     * Obtient la valeur de la propriété decimal.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getDecimal() {
        return decimal;
    }

    /**
     * Définit la valeur de la propriété decimal.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setDecimal(BigDecimal value) {
        this.decimal = value;
    }

    /**
     * Gets the value of the cds property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cds property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCds().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CDCONTENT }
     *
     *
     */
    public List<CDCONTENT> getCds() {
        if (cds == null) {
            cds = new ArrayList<CDCONTENT>();
        }
        return this.cds;
    }

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
     * {@link IDKMEHR }
     *
     *
     */
    public List<IDKMEHR> getIds() {
        if (ids == null) {
            ids = new ArrayList<IDKMEHR>();
        }
        return this.ids;
    }

    /**
     * Obtient la valeur de la propriété unit.
     *
     * @return
     *     possible object is
     *     {@link UnitType }
     *
     */
    public UnitType getUnit() {
        return unit;
    }

    /**
     * Définit la valeur de la propriété unit.
     *
     * @param value
     *     allowed object is
     *     {@link UnitType }
     *
     */
    public void setUnit(UnitType value) {
        this.unit = value;
    }

    /**
     * Obtient la valeur de la propriété minref.
     *
     * @return
     *     possible object is
     *     {@link MinrefType }
     *
     */
    public MinrefType getMinref() {
        return minref;
    }

    /**
     * Définit la valeur de la propriété minref.
     *
     * @param value
     *     allowed object is
     *     {@link MinrefType }
     *
     */
    public void setMinref(MinrefType value) {
        this.minref = value;
    }

    /**
     * Obtient la valeur de la propriété maxref.
     *
     * @return
     *     possible object is
     *     {@link MaxrefType }
     *
     */
    public MaxrefType getMaxref() {
        return maxref;
    }

    /**
     * Définit la valeur de la propriété maxref.
     *
     * @param value
     *     allowed object is
     *     {@link MaxrefType }
     *     
     */
    public void setMaxref(MaxrefType value) {
        this.maxref = value;
    }

    /**
     * Gets the value of the refscopes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refscopes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefscopes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RefscopeType }
     * 
     * 
     */
    public List<RefscopeType> getRefscopes() {
        if (refscopes == null) {
            refscopes = new ArrayList<RefscopeType>();
        }
        return this.refscopes;
    }


    /**
     * a medicinal product can be identified unambiguously by a CNK code identifying a package. The descriptive identification is only mandatory in case of absence of a package ID.
     * 
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="intendedcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK"/>
     *         &lt;element name="deliveredcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK" minOccurs="0"/>
     *         &lt;element name="intendedname" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="deliveredname" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
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
        "intendedcd",
        "deliveredcd",
        "intendedname",
        "deliveredname"
    })
    public static class Medicinalproduct
        implements Serializable
    {

        private final static long serialVersionUID = 20121001L;
        @XmlElement(required = true)
        protected CDDRUGCNK intendedcd;
        protected CDDRUGCNK deliveredcd;
        @XmlElement(required = true)
        protected String intendedname;
        protected Object deliveredname;

        /**
         * Obtient la valeur de la propriété intendedcd.
         * 
         * @return
         *     possible object is
         *     {@link CDDRUGCNK }
         *     
         */
        public CDDRUGCNK getIntendedcd() {
            return intendedcd;
        }

        /**
         * Définit la valeur de la propriété intendedcd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDDRUGCNK }
         *     
         */
        public void setIntendedcd(CDDRUGCNK value) {
            this.intendedcd = value;
        }

        /**
         * Obtient la valeur de la propriété deliveredcd.
         * 
         * @return
         *     possible object is
         *     {@link CDDRUGCNK }
         *     
         */
        public CDDRUGCNK getDeliveredcd() {
            return deliveredcd;
        }

        /**
         * Définit la valeur de la propriété deliveredcd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDDRUGCNK }
         *     
         */
        public void setDeliveredcd(CDDRUGCNK value) {
            this.deliveredcd = value;
        }

        /**
         * Obtient la valeur de la propriété intendedname.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIntendedname() {
            return intendedname;
        }

        /**
         * Définit la valeur de la propriété intendedname.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIntendedname(String value) {
            this.intendedname = value;
        }

        /**
         * Obtient la valeur de la propriété deliveredname.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getDeliveredname() {
            return deliveredname;
        }

        /**
         * Définit la valeur de la propriété deliveredname.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setDeliveredname(Object value) {
            this.deliveredname = value;
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
     *         &lt;element name="intendedcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-INNCLUSTER"/>
     *         &lt;element name="deliveredcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK" minOccurs="0"/>
     *         &lt;element name="intendedname" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="deliveredname" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
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
        "intendedcd",
        "deliveredcd",
        "intendedname",
        "deliveredname"
    })
    public static class Substanceproduct
        implements Serializable
    {

        private final static long serialVersionUID = 20121001L;
        @XmlElement(required = true)
        protected CDINNCLUSTER intendedcd;
        protected CDDRUGCNK deliveredcd;
        @XmlElement(required = true)
        protected String intendedname;
        protected Object deliveredname;

        /**
         * Obtient la valeur de la propriété intendedcd.
         * 
         * @return
         *     possible object is
         *     {@link CDINNCLUSTER }
         *     
         */
        public CDINNCLUSTER getIntendedcd() {
            return intendedcd;
        }

        /**
         * Définit la valeur de la propriété intendedcd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDINNCLUSTER }
         *     
         */
        public void setIntendedcd(CDINNCLUSTER value) {
            this.intendedcd = value;
        }

        /**
         * Obtient la valeur de la propriété deliveredcd.
         * 
         * @return
         *     possible object is
         *     {@link CDDRUGCNK }
         *     
         */
        public CDDRUGCNK getDeliveredcd() {
            return deliveredcd;
        }

        /**
         * Définit la valeur de la propriété deliveredcd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDDRUGCNK }
         *     
         */
        public void setDeliveredcd(CDDRUGCNK value) {
            this.deliveredcd = value;
        }

        /**
         * Obtient la valeur de la propriété intendedname.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIntendedname() {
            return intendedname;
        }

        /**
         * Définit la valeur de la propriété intendedname.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIntendedname(String value) {
            this.intendedname = value;
        }

        /**
         * Obtient la valeur de la propriété deliveredname.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getDeliveredname() {
            return deliveredname;
        }

        /**
         * Définit la valeur de la propriété deliveredname.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setDeliveredname(Object value) {
            this.deliveredname = value;
        }

    }

}
