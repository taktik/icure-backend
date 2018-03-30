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
// Généré le : 2015.03.05 à 11:48:01 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;


/**
 * the item is used to describe atomic medical information.
 * 
 * <p>Classe Java pour itemType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="itemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confidentiality" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}confidentialityType" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-KMEHR" maxOccurs="unbounded"/>
 *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-ITEM" maxOccurs="unbounded"/>
 *         &lt;element name="content" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}contentType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="author" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}authorType" minOccurs="0"/>
 *         &lt;element name="beginmoment" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}momentType" minOccurs="0"/>
 *         &lt;element name="endmoment" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}momentType" minOccurs="0"/>
 *         &lt;element name="iscomplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isvalidated" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="lifecycle" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}lifecycleType" minOccurs="0"/>
 *         &lt;element name="isrelevant" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}severityType" minOccurs="0"/>
 *         &lt;element name="certainty" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}certaintyType" minOccurs="0"/>
 *         &lt;element name="temporality" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}temporalityType" minOccurs="0"/>
 *         &lt;element name="urgency" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}urgencyType" minOccurs="0"/>
 *         &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}quantityType" minOccurs="0"/>
 *         &lt;element name="frequency" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}frequencyType" minOccurs="0"/>
 *         &lt;element name="site" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}siteType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cost" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}costType" minOccurs="0"/>
 *         &lt;element name="dayperiod" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}dayperiodType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="duration" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}durationType" minOccurs="0"/>
 *         &lt;element name="posology" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element name="low" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                     &lt;element name="high" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                     &lt;element name="unit" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}administrationunitType"/>
 *                     &lt;element name="takes">
 *                       &lt;complexType>
 *                         &lt;complexContent>
 *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                             &lt;sequence>
 *                               &lt;element name="low" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                               &lt;element name="high" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;/sequence>
 *                           &lt;/restriction>
 *                         &lt;/complexContent>
 *                       &lt;/complexType>
 *                     &lt;/element>
 *                   &lt;/sequence>
 *                   &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="regimen" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded">
 *                   &lt;choice minOccurs="0">
 *                     &lt;element name="daynumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *                     &lt;element name="date" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}date"/>
 *                     &lt;element name="weekday" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}weekdayType"/>
 *                   &lt;/choice>
 *                   &lt;element name="daytime">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice>
 *                             &lt;element name="time" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}time"/>
 *                             &lt;element name="dayperiod" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}dayperiodType"/>
 *                           &lt;/choice>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}administrationquantityType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="deliverydate" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}date" minOccurs="0"/>
 *         &lt;element name="renewal" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}renewalType" minOccurs="0"/>
 *         &lt;element name="route" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}routeType" minOccurs="0"/>
 *         &lt;element name="batch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="instructionforoverdosing" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" minOccurs="0"/>
 *         &lt;element name="instructionforpatient" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" minOccurs="0"/>
 *         &lt;element name="instructionforreimbursement" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" minOccurs="0"/>
 *         &lt;element name="issubstitutionallowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="local" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}localitemattributeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="recorddatetime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lnk" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}lnkType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemType", propOrder = {
    "confidentiality",
    "ids",
    "cds",
    "contents",
    "texts",
    "author",
    "beginmoment",
    "endmoment",
    "iscomplete",
    "isvalidated",
    "lifecycle",
    "isrelevant",
    "severity",
    "certainty",
    "temporality",
    "urgency",
    "quantity",
    "frequency",
    "sites",
    "cost",
    "dayperiods",
    "duration",
    "posology",
    "regimen",
    "deliverydate",
    "renewal",
    "route",
    "batch",
    "instructionforoverdosing",
    "instructionforpatient",
    "instructionforreimbursement",
    "issubstitutionallowed",
    "locals",
    "recorddatetime",
    "lnks"
})
public class ItemType implements Serializable
{

    private final static long serialVersionUID = 20120401L;
    protected ConfidentialityType confidentiality;
    @XmlElement(name = "id", required = true)
    protected List<IDKMEHR> ids;
    @XmlElement(name = "cd", required = true)
    protected List<CDITEM> cds;
    @XmlElement(name = "content")
    protected List<ContentType> contents;
    @XmlElement(name = "text")
    protected List<TextType> texts;
    protected AuthorType author;
    protected MomentType beginmoment;
    protected MomentType endmoment;
    protected Boolean iscomplete;
    protected Boolean isvalidated;
    protected LifecycleType lifecycle;
    protected Boolean isrelevant;
    protected SeverityType severity;
    protected CertaintyType certainty;
    protected TemporalityType temporality;
    protected UrgencyType urgency;
    protected QuantityType quantity;
    protected FrequencyType frequency;
    @XmlElement(name = "site")
    protected List<SiteType> sites;
    protected CostType cost;
    @XmlElement(name = "dayperiod")
    protected List<DayperiodType> dayperiods;
    protected DurationType duration;
    protected ItemType.Posology posology;
    protected ItemType.Regimen regimen;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deliverydate;
    protected RenewalType renewal;
    protected RouteType route;
    protected String batch;
    protected TextType instructionforoverdosing;
    protected TextType instructionforpatient;
    protected TextType instructionforreimbursement;
    protected Boolean issubstitutionallowed;
    @XmlElement(name = "local")
    protected List<LocalitemattributeType> locals;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar recorddatetime;
    @XmlElement(name = "lnk")
    protected List<LnkType> lnks;

    /**
     * Obtient la valeur de la propriété confidentiality.
     * 
     * @return
     *     possible object is
     *     {@link ConfidentialityType }
     *     
     */
    public ConfidentialityType getConfidentiality() {
        return confidentiality;
    }

    /**
     * Définit la valeur de la propriété confidentiality.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfidentialityType }
     *     
     */
    public void setConfidentiality(ConfidentialityType value) {
        this.confidentiality = value;
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
     * {@link CDITEM }
     * 
     * 
     */
    public List<CDITEM> getCds() {
        if (cds == null) {
            cds = new ArrayList<CDITEM>();
        }
        return this.cds;
    }

    /**
     * Gets the value of the contents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContentType }
     * 
     * 
     */
    public List<ContentType> getContents() {
        if (contents == null) {
            contents = new ArrayList<ContentType>();
        }
        return this.contents;
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
     * Obtient la valeur de la propriété author.
     * 
     * @return
     *     possible object is
     *     {@link AuthorType }
     *     
     */
    public AuthorType getAuthor() {
        return author;
    }

    /**
     * Définit la valeur de la propriété author.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorType }
     *     
     */
    public void setAuthor(AuthorType value) {
        this.author = value;
    }

    /**
     * Obtient la valeur de la propriété beginmoment.
     * 
     * @return
     *     possible object is
     *     {@link MomentType }
     *     
     */
    public MomentType getBeginmoment() {
        return beginmoment;
    }

    /**
     * Définit la valeur de la propriété beginmoment.
     * 
     * @param value
     *     allowed object is
     *     {@link MomentType }
     *     
     */
    public void setBeginmoment(MomentType value) {
        this.beginmoment = value;
    }

    /**
     * Obtient la valeur de la propriété endmoment.
     * 
     * @return
     *     possible object is
     *     {@link MomentType }
     *     
     */
    public MomentType getEndmoment() {
        return endmoment;
    }

    /**
     * Définit la valeur de la propriété endmoment.
     * 
     * @param value
     *     allowed object is
     *     {@link MomentType }
     *     
     */
    public void setEndmoment(MomentType value) {
        this.endmoment = value;
    }

    /**
     * Obtient la valeur de la propriété iscomplete.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIscomplete() {
        return iscomplete;
    }

    /**
     * Définit la valeur de la propriété iscomplete.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIscomplete(Boolean value) {
        this.iscomplete = value;
    }

    /**
     * Obtient la valeur de la propriété isvalidated.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsvalidated() {
        return isvalidated;
    }

    /**
     * Définit la valeur de la propriété isvalidated.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsvalidated(Boolean value) {
        this.isvalidated = value;
    }

    /**
     * Obtient la valeur de la propriété lifecycle.
     * 
     * @return
     *     possible object is
     *     {@link LifecycleType }
     *     
     */
    public LifecycleType getLifecycle() {
        return lifecycle;
    }

    /**
     * Définit la valeur de la propriété lifecycle.
     * 
     * @param value
     *     allowed object is
     *     {@link LifecycleType }
     *     
     */
    public void setLifecycle(LifecycleType value) {
        this.lifecycle = value;
    }

    /**
     * Obtient la valeur de la propriété isrelevant.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsrelevant() {
        return isrelevant;
    }

    /**
     * Définit la valeur de la propriété isrelevant.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsrelevant(Boolean value) {
        this.isrelevant = value;
    }

    /**
     * Obtient la valeur de la propriété severity.
     * 
     * @return
     *     possible object is
     *     {@link SeverityType }
     *     
     */
    public SeverityType getSeverity() {
        return severity;
    }

    /**
     * Définit la valeur de la propriété severity.
     * 
     * @param value
     *     allowed object is
     *     {@link SeverityType }
     *     
     */
    public void setSeverity(SeverityType value) {
        this.severity = value;
    }

    /**
     * Obtient la valeur de la propriété certainty.
     * 
     * @return
     *     possible object is
     *     {@link CertaintyType }
     *     
     */
    public CertaintyType getCertainty() {
        return certainty;
    }

    /**
     * Définit la valeur de la propriété certainty.
     * 
     * @param value
     *     allowed object is
     *     {@link CertaintyType }
     *     
     */
    public void setCertainty(CertaintyType value) {
        this.certainty = value;
    }

    /**
     * Obtient la valeur de la propriété temporality.
     * 
     * @return
     *     possible object is
     *     {@link TemporalityType }
     *     
     */
    public TemporalityType getTemporality() {
        return temporality;
    }

    /**
     * Définit la valeur de la propriété temporality.
     * 
     * @param value
     *     allowed object is
     *     {@link TemporalityType }
     *     
     */
    public void setTemporality(TemporalityType value) {
        this.temporality = value;
    }

    /**
     * Obtient la valeur de la propriété urgency.
     * 
     * @return
     *     possible object is
     *     {@link UrgencyType }
     *     
     */
    public UrgencyType getUrgency() {
        return urgency;
    }

    /**
     * Définit la valeur de la propriété urgency.
     * 
     * @param value
     *     allowed object is
     *     {@link UrgencyType }
     *     
     */
    public void setUrgency(UrgencyType value) {
        this.urgency = value;
    }

    /**
     * Obtient la valeur de la propriété quantity.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Définit la valeur de la propriété quantity.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setQuantity(QuantityType value) {
        this.quantity = value;
    }

    /**
     * Obtient la valeur de la propriété frequency.
     * 
     * @return
     *     possible object is
     *     {@link FrequencyType }
     *     
     */
    public FrequencyType getFrequency() {
        return frequency;
    }

    /**
     * Définit la valeur de la propriété frequency.
     * 
     * @param value
     *     allowed object is
     *     {@link FrequencyType }
     *     
     */
    public void setFrequency(FrequencyType value) {
        this.frequency = value;
    }

    /**
     * Gets the value of the sites property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sites property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSites().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SiteType }
     * 
     * 
     */
    public List<SiteType> getSites() {
        if (sites == null) {
            sites = new ArrayList<SiteType>();
        }
        return this.sites;
    }

    /**
     * Obtient la valeur de la propriété cost.
     * 
     * @return
     *     possible object is
     *     {@link CostType }
     *     
     */
    public CostType getCost() {
        return cost;
    }

    /**
     * Définit la valeur de la propriété cost.
     * 
     * @param value
     *     allowed object is
     *     {@link CostType }
     *     
     */
    public void setCost(CostType value) {
        this.cost = value;
    }

    /**
     * Gets the value of the dayperiods property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dayperiods property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDayperiods().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DayperiodType }
     * 
     * 
     */
    public List<DayperiodType> getDayperiods() {
        if (dayperiods == null) {
            dayperiods = new ArrayList<DayperiodType>();
        }
        return this.dayperiods;
    }

    /**
     * Obtient la valeur de la propriété duration.
     * 
     * @return
     *     possible object is
     *     {@link DurationType }
     *     
     */
    public DurationType getDuration() {
        return duration;
    }

    /**
     * Définit la valeur de la propriété duration.
     * 
     * @param value
     *     allowed object is
     *     {@link DurationType }
     *     
     */
    public void setDuration(DurationType value) {
        this.duration = value;
    }

    /**
     * Obtient la valeur de la propriété posology.
     * 
     * @return
     *     possible object is
     *     {@link ItemType.Posology }
     *     
     */
    public ItemType.Posology getPosology() {
        return posology;
    }

    /**
     * Définit la valeur de la propriété posology.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemType.Posology }
     *     
     */
    public void setPosology(ItemType.Posology value) {
        this.posology = value;
    }

    /**
     * Obtient la valeur de la propriété regimen.
     * 
     * @return
     *     possible object is
     *     {@link ItemType.Regimen }
     *     
     */
    public ItemType.Regimen getRegimen() {
        return regimen;
    }

    /**
     * Définit la valeur de la propriété regimen.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemType.Regimen }
     *     
     */
    public void setRegimen(ItemType.Regimen value) {
        this.regimen = value;
    }

    /**
     * Obtient la valeur de la propriété deliverydate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeliverydate() {
        return deliverydate;
    }

    /**
     * Définit la valeur de la propriété deliverydate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeliverydate(XMLGregorianCalendar value) {
        this.deliverydate = value;
    }

    /**
     * Obtient la valeur de la propriété renewal.
     * 
     * @return
     *     possible object is
     *     {@link RenewalType }
     *     
     */
    public RenewalType getRenewal() {
        return renewal;
    }

    /**
     * Définit la valeur de la propriété renewal.
     * 
     * @param value
     *     allowed object is
     *     {@link RenewalType }
     *     
     */
    public void setRenewal(RenewalType value) {
        this.renewal = value;
    }

    /**
     * Obtient la valeur de la propriété route.
     * 
     * @return
     *     possible object is
     *     {@link RouteType }
     *     
     */
    public RouteType getRoute() {
        return route;
    }

    /**
     * Définit la valeur de la propriété route.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteType }
     *     
     */
    public void setRoute(RouteType value) {
        this.route = value;
    }

    /**
     * Obtient la valeur de la propriété batch.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatch() {
        return batch;
    }

    /**
     * Définit la valeur de la propriété batch.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatch(String value) {
        this.batch = value;
    }

    /**
     * Obtient la valeur de la propriété instructionforoverdosing.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInstructionforoverdosing() {
        return instructionforoverdosing;
    }

    /**
     * Définit la valeur de la propriété instructionforoverdosing.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInstructionforoverdosing(TextType value) {
        this.instructionforoverdosing = value;
    }

    /**
     * Obtient la valeur de la propriété instructionforpatient.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInstructionforpatient() {
        return instructionforpatient;
    }

    /**
     * Définit la valeur de la propriété instructionforpatient.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInstructionforpatient(TextType value) {
        this.instructionforpatient = value;
    }

    /**
     * Obtient la valeur de la propriété instructionforreimbursement.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInstructionforreimbursement() {
        return instructionforreimbursement;
    }

    /**
     * Définit la valeur de la propriété instructionforreimbursement.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInstructionforreimbursement(TextType value) {
        this.instructionforreimbursement = value;
    }

    /**
     * Obtient la valeur de la propriété issubstitutionallowed.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIssubstitutionallowed() {
        return issubstitutionallowed;
    }

    /**
     * Définit la valeur de la propriété issubstitutionallowed.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIssubstitutionallowed(Boolean value) {
        this.issubstitutionallowed = value;
    }

    /**
     * Gets the value of the locals property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locals property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocals().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocalitemattributeType }
     * 
     * 
     */
    public List<LocalitemattributeType> getLocals() {
        if (locals == null) {
            locals = new ArrayList<LocalitemattributeType>();
        }
        return this.locals;
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
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;choice>
     *         &lt;sequence>
     *           &lt;element name="low" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *           &lt;element name="high" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *           &lt;element name="unit" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}administrationunitType"/>
     *           &lt;element name="takes">
     *             &lt;complexType>
     *               &lt;complexContent>
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                   &lt;sequence>
     *                     &lt;element name="low" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                     &lt;element name="high" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;/sequence>
     *                 &lt;/restriction>
     *               &lt;/complexContent>
     *             &lt;/complexType>
     *           &lt;/element>
     *         &lt;/sequence>
     *         &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "text",
        "low",
        "high",
        "unit",
        "takes"
    })
    public static class Posology
        implements Serializable
    {

        private final static long serialVersionUID = 20120401L;
        protected TextType text;
        protected BigDecimal low;
        protected BigDecimal high;
        protected AdministrationunitType unit;
        protected ItemType.Posology.Takes takes;

        /**
         * Obtient la valeur de la propriété text.
         * 
         * @return
         *     possible object is
         *     {@link TextType }
         *     
         */
        public TextType getText() {
            return text;
        }

        /**
         * Définit la valeur de la propriété text.
         * 
         * @param value
         *     allowed object is
         *     {@link TextType }
         *     
         */
        public void setText(TextType value) {
            this.text = value;
        }

        /**
         * Obtient la valeur de la propriété low.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getLow() {
            return low;
        }

        /**
         * Définit la valeur de la propriété low.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setLow(BigDecimal value) {
            this.low = value;
        }

        /**
         * Obtient la valeur de la propriété high.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getHigh() {
            return high;
        }

        /**
         * Définit la valeur de la propriété high.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setHigh(BigDecimal value) {
            this.high = value;
        }

        /**
         * Obtient la valeur de la propriété unit.
         * 
         * @return
         *     possible object is
         *     {@link AdministrationunitType }
         *     
         */
        public AdministrationunitType getUnit() {
            return unit;
        }

        /**
         * Définit la valeur de la propriété unit.
         * 
         * @param value
         *     allowed object is
         *     {@link AdministrationunitType }
         *     
         */
        public void setUnit(AdministrationunitType value) {
            this.unit = value;
        }

        /**
         * Obtient la valeur de la propriété takes.
         * 
         * @return
         *     possible object is
         *     {@link ItemType.Posology.Takes }
         *     
         */
        public ItemType.Posology.Takes getTakes() {
            return takes;
        }

        /**
         * Définit la valeur de la propriété takes.
         * 
         * @param value
         *     allowed object is
         *     {@link ItemType.Posology.Takes }
         *     
         */
        public void setTakes(ItemType.Posology.Takes value) {
            this.takes = value;
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
         *         &lt;element name="low" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="high" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
            "low",
            "high"
        })
        public static class Takes
            implements Serializable
        {

            private final static long serialVersionUID = 20120401L;
            protected BigDecimal low;
            @XmlElement(required = true)
            protected BigDecimal high;

            /**
             * Obtient la valeur de la propriété low.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getLow() {
                return low;
            }

            /**
             * Définit la valeur de la propriété low.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setLow(BigDecimal value) {
                this.low = value;
            }

            /**
             * Obtient la valeur de la propriété high.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getHigh() {
                return high;
            }

            /**
             * Définit la valeur de la propriété high.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setHigh(BigDecimal value) {
                this.high = value;
            }

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
     *       &lt;sequence maxOccurs="unbounded">
     *         &lt;choice minOccurs="0">
     *           &lt;element name="daynumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
     *           &lt;element name="date" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}date"/>
     *           &lt;element name="weekday" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}weekdayType"/>
     *         &lt;/choice>
     *         &lt;element name="daytime">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice>
     *                   &lt;element name="time" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}time"/>
     *                   &lt;element name="dayperiod" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}dayperiodType"/>
     *                 &lt;/choice>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}administrationquantityType"/>
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
        "daynumbersAndQuantitiesAndDaytimes"
    })
    public static class Regimen
        implements Serializable
    {

        private final static long serialVersionUID = 20120401L;
        @XmlElements({
            @XmlElement(name = "daynumber", required = true, type = BigInteger.class),
            @XmlElement(name = "quantity", required = true, type = AdministrationquantityType.class),
            @XmlElement(name = "daytime", required = true, type = ItemType.Regimen.Daytime.class),
            @XmlElement(name = "date", required = true, type = XMLGregorianCalendar.class),
            @XmlElement(name = "weekday", required = true, type = WeekdayType.class)
        })
        protected List<Object> daynumbersAndQuantitiesAndDaytimes;

        /**
         * Gets the value of the daynumbersAndQuantitiesAndDaytimes property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the daynumbersAndQuantitiesAndDaytimes property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDaynumbersAndQuantitiesAndDaytimes().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BigInteger }
         * {@link AdministrationquantityType }
         * {@link ItemType.Regimen.Daytime }
         * {@link XMLGregorianCalendar }
         * {@link WeekdayType }
         * 
         * 
         */
        public List<Object> getDaynumbersAndQuantitiesAndDaytimes() {
            if (daynumbersAndQuantitiesAndDaytimes == null) {
                daynumbersAndQuantitiesAndDaytimes = new ArrayList<Object>();
            }
            return this.daynumbersAndQuantitiesAndDaytimes;
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
         *       &lt;choice>
         *         &lt;element name="time" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}time"/>
         *         &lt;element name="dayperiod" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}dayperiodType"/>
         *       &lt;/choice>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "dayperiod",
            "time"
        })
        public static class Daytime implements Serializable
        {

            private final static long serialVersionUID = 20120401L;
            protected DayperiodType dayperiod;
            @XmlSchemaType(name = "time")
            protected XMLGregorianCalendar time;

            /**
             * Obtient la valeur de la propriété dayperiod.
             * 
             * @return
             *     possible object is
             *     {@link DayperiodType }
             *     
             */
            public DayperiodType getDayperiod() {
                return dayperiod;
            }

            /**
             * Définit la valeur de la propriété dayperiod.
             * 
             * @param value
             *     allowed object is
             *     {@link DayperiodType }
             *     
             */
            public void setDayperiod(DayperiodType value) {
                this.dayperiod = value;
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

        }

    }

}
