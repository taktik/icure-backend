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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;


/**
 * to specify the routing of the message
 * 
 * <p>Classe Java pour headerType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="headerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confidentiality" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}confidentialityType" minOccurs="0"/>
 *         &lt;element name="standard" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}standardType"/>
 *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-KMEHR" maxOccurs="unbounded"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *         &lt;element name="sender" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}senderType"/>
 *         &lt;element name="recipient" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}recipientType" maxOccurs="unbounded"/>
 *         &lt;element name="urgency" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}urgencyType" minOccurs="0"/>
 *         &lt;element name="acknowledgment" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}acknowledgmentType" minOccurs="0"/>
 *         &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "headerType", propOrder = {
    "confidentiality",
    "standard",
    "ids",
    "date",
    "time",
    "sender",
    "recipients",
    "urgency",
    "acknowledgment",
    "texts",
    "lnks"
})
public class HeaderType
    implements Serializable
{

    private final static long serialVersionUID = 20121001L;
    protected ConfidentialityType confidentiality;
    @XmlElement(required = true)
    protected StandardType standard;
    @XmlElement(name = "id", required = true)
    protected List<IDKMEHR> ids;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlElement(required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar time;
    @XmlElement(required = true)
    protected SenderType sender;
    @XmlElement(name = "recipient", required = true)
    protected List<RecipientType> recipients;
    protected UrgencyType urgency;
    protected AcknowledgmentType acknowledgment;
    @XmlElement(name = "text")
    protected List<TextType> texts;
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
     * Obtient la valeur de la propriété standard.
     *
     * @return
     *     possible object is
     *     {@link StandardType }
     *
     */
    public StandardType getStandard() {
        return standard;
    }

    /**
     * Définit la valeur de la propriété standard.
     *
     * @param value
     *     allowed object is
     *     {@link StandardType }
     *
     */
    public void setStandard(StandardType value) {
        this.standard = value;
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
     * Obtient la valeur de la propriété sender.
     *
     * @return
     *     possible object is
     *     {@link SenderType }
     *
     */
    public SenderType getSender() {
        return sender;
    }

    /**
     * Définit la valeur de la propriété sender.
     *
     * @param value
     *     allowed object is
     *     {@link SenderType }
     *
     */
    public void setSender(SenderType value) {
        this.sender = value;
    }

    /**
     * Gets the value of the recipients property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recipients property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecipients().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecipientType }
     *
     *
     */
    public List<RecipientType> getRecipients() {
        if (recipients == null) {
            recipients = new ArrayList<RecipientType>();
        }
        return this.recipients;
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
     * Obtient la valeur de la propriété acknowledgment.
     * 
     * @return
     *     possible object is
     *     {@link AcknowledgmentType }
     *     
     */
    public AcknowledgmentType getAcknowledgment() {
        return acknowledgment;
    }

    /**
     * Définit la valeur de la propriété acknowledgment.
     * 
     * @param value
     *     allowed object is
     *     {@link AcknowledgmentType }
     *     
     */
    public void setAcknowledgment(AcknowledgmentType value) {
        this.acknowledgment = value;
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

}
