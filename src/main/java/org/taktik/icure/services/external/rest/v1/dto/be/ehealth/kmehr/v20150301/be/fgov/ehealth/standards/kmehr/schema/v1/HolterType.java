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
// Généré le : 2015.11.10 à 11:53:40 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150301.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour holterType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="holterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FCAVG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FCMAX" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}FCMAXType" minOccurs="0"/>
 *         &lt;element name="FCMIN" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}FCMINType" minOccurs="0"/>
 *         &lt;element name="FCAVGD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FCAVGN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RRMAX" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}RRMAXType" minOccurs="0"/>
 *         &lt;element name="RRMIN" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}RRMINType" minOccurs="0"/>
 *         &lt;element name="QRSTOT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BRADY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PAUSE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LONG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ESV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DBLV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SALVV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BGV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TGV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TACHY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ESSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DBLSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SALVSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BGSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TGSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TACHYSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RRINST" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "holterType", propOrder = {
    "fcavg",
    "fcmax",
    "fcmin",
    "fcavgd",
    "fcavgn",
    "rrmax",
    "rrmin",
    "qrstot",
    "brady",
    "pause",
    "_long",
    "esv",
    "dblv",
    "salvv",
    "bgv",
    "tgv",
    "tachy",
    "essv",
    "dblsv",
    "salvsv",
    "bgsv",
    "tgsv",
    "tachysv",
    "rrinst"
})
public class HolterType
    implements Serializable
{

    private final static long serialVersionUID = 20150301L;
    @XmlElement(name = "FCAVG")
    protected String fcavg;
    @XmlElement(name = "FCMAX")
    protected FCMAXType fcmax;
    @XmlElement(name = "FCMIN")
    protected FCMINType fcmin;
    @XmlElement(name = "FCAVGD")
    protected String fcavgd;
    @XmlElement(name = "FCAVGN")
    protected String fcavgn;
    @XmlElement(name = "RRMAX")
    protected RRMAXType rrmax;
    @XmlElement(name = "RRMIN")
    protected RRMINType rrmin;
    @XmlElement(name = "QRSTOT")
    protected String qrstot;
    @XmlElement(name = "BRADY")
    protected String brady;
    @XmlElement(name = "PAUSE")
    protected String pause;
    @XmlElement(name = "LONG")
    protected String _long;
    @XmlElement(name = "ESV")
    protected String esv;
    @XmlElement(name = "DBLV")
    protected String dblv;
    @XmlElement(name = "SALVV")
    protected String salvv;
    @XmlElement(name = "BGV")
    protected String bgv;
    @XmlElement(name = "TGV")
    protected String tgv;
    @XmlElement(name = "TACHY")
    protected String tachy;
    @XmlElement(name = "ESSV")
    protected String essv;
    @XmlElement(name = "DBLSV")
    protected String dblsv;
    @XmlElement(name = "SALVSV")
    protected String salvsv;
    @XmlElement(name = "BGSV")
    protected String bgsv;
    @XmlElement(name = "TGSV")
    protected String tgsv;
    @XmlElement(name = "TACHYSV")
    protected String tachysv;
    @XmlElement(name = "RRINST")
    protected String rrinst;

    /**
     * Obtient la valeur de la propriété fcavg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCAVG() {
        return fcavg;
    }

    /**
     * Définit la valeur de la propriété fcavg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCAVG(String value) {
        this.fcavg = value;
    }

    /**
     * Obtient la valeur de la propriété fcmax.
     * 
     * @return
     *     possible object is
     *     {@link FCMAXType }
     *     
     */
    public FCMAXType getFCMAX() {
        return fcmax;
    }

    /**
     * Définit la valeur de la propriété fcmax.
     * 
     * @param value
     *     allowed object is
     *     {@link FCMAXType }
     *     
     */
    public void setFCMAX(FCMAXType value) {
        this.fcmax = value;
    }

    /**
     * Obtient la valeur de la propriété fcmin.
     * 
     * @return
     *     possible object is
     *     {@link FCMINType }
     *     
     */
    public FCMINType getFCMIN() {
        return fcmin;
    }

    /**
     * Définit la valeur de la propriété fcmin.
     * 
     * @param value
     *     allowed object is
     *     {@link FCMINType }
     *     
     */
    public void setFCMIN(FCMINType value) {
        this.fcmin = value;
    }

    /**
     * Obtient la valeur de la propriété fcavgd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCAVGD() {
        return fcavgd;
    }

    /**
     * Définit la valeur de la propriété fcavgd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCAVGD(String value) {
        this.fcavgd = value;
    }

    /**
     * Obtient la valeur de la propriété fcavgn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFCAVGN() {
        return fcavgn;
    }

    /**
     * Définit la valeur de la propriété fcavgn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFCAVGN(String value) {
        this.fcavgn = value;
    }

    /**
     * Obtient la valeur de la propriété rrmax.
     * 
     * @return
     *     possible object is
     *     {@link RRMAXType }
     *     
     */
    public RRMAXType getRRMAX() {
        return rrmax;
    }

    /**
     * Définit la valeur de la propriété rrmax.
     * 
     * @param value
     *     allowed object is
     *     {@link RRMAXType }
     *     
     */
    public void setRRMAX(RRMAXType value) {
        this.rrmax = value;
    }

    /**
     * Obtient la valeur de la propriété rrmin.
     * 
     * @return
     *     possible object is
     *     {@link RRMINType }
     *     
     */
    public RRMINType getRRMIN() {
        return rrmin;
    }

    /**
     * Définit la valeur de la propriété rrmin.
     * 
     * @param value
     *     allowed object is
     *     {@link RRMINType }
     *     
     */
    public void setRRMIN(RRMINType value) {
        this.rrmin = value;
    }

    /**
     * Obtient la valeur de la propriété qrstot.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQRSTOT() {
        return qrstot;
    }

    /**
     * Définit la valeur de la propriété qrstot.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQRSTOT(String value) {
        this.qrstot = value;
    }

    /**
     * Obtient la valeur de la propriété brady.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBRADY() {
        return brady;
    }

    /**
     * Définit la valeur de la propriété brady.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBRADY(String value) {
        this.brady = value;
    }

    /**
     * Obtient la valeur de la propriété pause.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAUSE() {
        return pause;
    }

    /**
     * Définit la valeur de la propriété pause.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPAUSE(String value) {
        this.pause = value;
    }

    /**
     * Obtient la valeur de la propriété long.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLONG() {
        return _long;
    }

    /**
     * Définit la valeur de la propriété long.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLONG(String value) {
        this._long = value;
    }

    /**
     * Obtient la valeur de la propriété esv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESV() {
        return esv;
    }

    /**
     * Définit la valeur de la propriété esv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESV(String value) {
        this.esv = value;
    }

    /**
     * Obtient la valeur de la propriété dblv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBLV() {
        return dblv;
    }

    /**
     * Définit la valeur de la propriété dblv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBLV(String value) {
        this.dblv = value;
    }

    /**
     * Obtient la valeur de la propriété salvv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSALVV() {
        return salvv;
    }

    /**
     * Définit la valeur de la propriété salvv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSALVV(String value) {
        this.salvv = value;
    }

    /**
     * Obtient la valeur de la propriété bgv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBGV() {
        return bgv;
    }

    /**
     * Définit la valeur de la propriété bgv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBGV(String value) {
        this.bgv = value;
    }

    /**
     * Obtient la valeur de la propriété tgv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTGV() {
        return tgv;
    }

    /**
     * Définit la valeur de la propriété tgv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTGV(String value) {
        this.tgv = value;
    }

    /**
     * Obtient la valeur de la propriété tachy.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTACHY() {
        return tachy;
    }

    /**
     * Définit la valeur de la propriété tachy.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTACHY(String value) {
        this.tachy = value;
    }

    /**
     * Obtient la valeur de la propriété essv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESSV() {
        return essv;
    }

    /**
     * Définit la valeur de la propriété essv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESSV(String value) {
        this.essv = value;
    }

    /**
     * Obtient la valeur de la propriété dblsv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBLSV() {
        return dblsv;
    }

    /**
     * Définit la valeur de la propriété dblsv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBLSV(String value) {
        this.dblsv = value;
    }

    /**
     * Obtient la valeur de la propriété salvsv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSALVSV() {
        return salvsv;
    }

    /**
     * Définit la valeur de la propriété salvsv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSALVSV(String value) {
        this.salvsv = value;
    }

    /**
     * Obtient la valeur de la propriété bgsv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBGSV() {
        return bgsv;
    }

    /**
     * Définit la valeur de la propriété bgsv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBGSV(String value) {
        this.bgsv = value;
    }

    /**
     * Obtient la valeur de la propriété tgsv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTGSV() {
        return tgsv;
    }

    /**
     * Définit la valeur de la propriété tgsv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTGSV(String value) {
        this.tgsv = value;
    }

    /**
     * Obtient la valeur de la propriété tachysv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTACHYSV() {
        return tachysv;
    }

    /**
     * Définit la valeur de la propriété tachysv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTACHYSV(String value) {
        this.tachysv = value;
    }

    /**
     * Obtient la valeur de la propriété rrinst.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRRINST() {
        return rrinst;
    }

    /**
     * Définit la valeur de la propriété rrinst.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRRINST(String value) {
        this.rrinst = value;
    }

}
