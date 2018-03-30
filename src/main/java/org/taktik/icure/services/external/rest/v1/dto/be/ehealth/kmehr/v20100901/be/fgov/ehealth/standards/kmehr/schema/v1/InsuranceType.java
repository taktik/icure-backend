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
// Généré le : 2015.03.05 à 11:47:56 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20100901.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20100901.be.fgov.ehealth.standards.kmehr.cd.v1.CDCARENETPERSONALPART;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20100901.be.fgov.ehealth.standards.kmehr.cd.v1.CDCARENETTHIRDPAYERCONTRACT;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20100901.be.fgov.ehealth.standards.kmehr.id.v1.IDINSURANCE;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20100901.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;


/**
 * <p>Classe Java pour insuranceType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="insuranceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-INSURANCE"/>
 *         &lt;element name="membership" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="siscard" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="begindate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="enddate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="cg1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cg2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="socialfranchiseperiod1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="socialfranchiseperiod2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="personalpart" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-KMEHR"/>
 *                   &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CARENET-PERSONAL-PART"/>
 *                   &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *                   &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="thirdpayercontract" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CARENET-THIRDPAYER-CONTRACT"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="begindatepayment" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="approvalnumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "insuranceType", propOrder = {
    "id",
    "membership",
    "siscard",
    "begindate",
    "enddate",
    "cg1",
    "cg2",
    "socialfranchiseperiod1",
    "socialfranchiseperiod2",
    "personalparts",
    "thirdpayercontract",
    "begindatepayment",
    "approvalnumber"
})
public class InsuranceType
    implements Serializable
{

    private final static long serialVersionUID = 20100901L;
    @XmlElement(required = true)
    protected IDINSURANCE id;
    @XmlElement(required = true)
    protected String membership;
    protected String siscard;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar begindate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar enddate;
    protected String cg1;
    protected String cg2;
    protected String socialfranchiseperiod1;
    protected String socialfranchiseperiod2;
    @XmlElement(name = "personalpart")
    protected List<InsuranceType.Personalpart> personalparts;
    protected InsuranceType.Thirdpayercontract thirdpayercontract;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar begindatepayment;
    protected String approvalnumber;

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return
     *     possible object is
     *     {@link IDINSURANCE }
     *     
     */
    public IDINSURANCE getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value
     *     allowed object is
     *     {@link IDINSURANCE }
     *     
     */
    public void setId(IDINSURANCE value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété membership.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMembership() {
        return membership;
    }

    /**
     * Définit la valeur de la propriété membership.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMembership(String value) {
        this.membership = value;
    }

    /**
     * Obtient la valeur de la propriété siscard.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSiscard() {
        return siscard;
    }

    /**
     * Définit la valeur de la propriété siscard.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSiscard(String value) {
        this.siscard = value;
    }

    /**
     * Obtient la valeur de la propriété begindate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBegindate() {
        return begindate;
    }

    /**
     * Définit la valeur de la propriété begindate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBegindate(XMLGregorianCalendar value) {
        this.begindate = value;
    }

    /**
     * Obtient la valeur de la propriété enddate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEnddate() {
        return enddate;
    }

    /**
     * Définit la valeur de la propriété enddate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEnddate(XMLGregorianCalendar value) {
        this.enddate = value;
    }

    /**
     * Obtient la valeur de la propriété cg1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCg1() {
        return cg1;
    }

    /**
     * Définit la valeur de la propriété cg1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCg1(String value) {
        this.cg1 = value;
    }

    /**
     * Obtient la valeur de la propriété cg2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCg2() {
        return cg2;
    }

    /**
     * Définit la valeur de la propriété cg2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCg2(String value) {
        this.cg2 = value;
    }

    /**
     * Obtient la valeur de la propriété socialfranchiseperiod1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialfranchiseperiod1() {
        return socialfranchiseperiod1;
    }

    /**
     * Définit la valeur de la propriété socialfranchiseperiod1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialfranchiseperiod1(String value) {
        this.socialfranchiseperiod1 = value;
    }

    /**
     * Obtient la valeur de la propriété socialfranchiseperiod2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSocialfranchiseperiod2() {
        return socialfranchiseperiod2;
    }

    /**
     * Définit la valeur de la propriété socialfranchiseperiod2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocialfranchiseperiod2(String value) {
        this.socialfranchiseperiod2 = value;
    }

    /**
     * Gets the value of the personalparts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personalparts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonalparts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InsuranceType.Personalpart }
     * 
     * 
     */
    public List<InsuranceType.Personalpart> getPersonalparts() {
        if (personalparts == null) {
            personalparts = new ArrayList<InsuranceType.Personalpart>();
        }
        return this.personalparts;
    }

    /**
     * Obtient la valeur de la propriété thirdpayercontract.
     * 
     * @return
     *     possible object is
     *     {@link InsuranceType.Thirdpayercontract }
     *     
     */
    public InsuranceType.Thirdpayercontract getThirdpayercontract() {
        return thirdpayercontract;
    }

    /**
     * Définit la valeur de la propriété thirdpayercontract.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuranceType.Thirdpayercontract }
     *     
     */
    public void setThirdpayercontract(InsuranceType.Thirdpayercontract value) {
        this.thirdpayercontract = value;
    }

    /**
     * Obtient la valeur de la propriété begindatepayment.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBegindatepayment() {
        return begindatepayment;
    }

    /**
     * Définit la valeur de la propriété begindatepayment.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBegindatepayment(XMLGregorianCalendar value) {
        this.begindatepayment = value;
    }

    /**
     * Obtient la valeur de la propriété approvalnumber.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApprovalnumber() {
        return approvalnumber;
    }

    /**
     * Définit la valeur de la propriété approvalnumber.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApprovalnumber(String value) {
        this.approvalnumber = value;
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
     *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-KMEHR"/>
     *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CARENET-PERSONAL-PART"/>
     *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
     *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
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
        "id",
        "cd",
        "date",
        "time"
    })
    public static class Personalpart
        implements Serializable
    {

        private final static long serialVersionUID = 20100901L;
        @XmlElement(required = true)
        protected IDKMEHR id;
        @XmlElement(required = true)
        protected CDCARENETPERSONALPART cd;
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar date;
        @XmlSchemaType(name = "time")
        protected XMLGregorianCalendar time;

        /**
         * Obtient la valeur de la propriété id.
         * 
         * @return
         *     possible object is
         *     {@link IDKMEHR }
         *     
         */
        public IDKMEHR getId() {
            return id;
        }

        /**
         * Définit la valeur de la propriété id.
         * 
         * @param value
         *     allowed object is
         *     {@link IDKMEHR }
         *     
         */
        public void setId(IDKMEHR value) {
            this.id = value;
        }

        /**
         * Obtient la valeur de la propriété cd.
         * 
         * @return
         *     possible object is
         *     {@link CDCARENETPERSONALPART }
         *     
         */
        public CDCARENETPERSONALPART getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDCARENETPERSONALPART }
         *     
         */
        public void setCd(CDCARENETPERSONALPART value) {
            this.cd = value;
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
     *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-CARENET-THIRDPAYER-CONTRACT"/>
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
    public static class Thirdpayercontract
        implements Serializable
    {

        private final static long serialVersionUID = 20100901L;
        @XmlElement(required = true)
        protected CDCARENETTHIRDPAYERCONTRACT cd;

        /**
         * Obtient la valeur de la propriété cd.
         * 
         * @return
         *     possible object is
         *     {@link CDCARENETTHIRDPAYERCONTRACT }
         *     
         */
        public CDCARENETTHIRDPAYERCONTRACT getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDCARENETTHIRDPAYERCONTRACT }
         *     
         */
        public void setCd(CDCARENETTHIRDPAYERCONTRACT value) {
            this.cd = value;
        }

    }

}
