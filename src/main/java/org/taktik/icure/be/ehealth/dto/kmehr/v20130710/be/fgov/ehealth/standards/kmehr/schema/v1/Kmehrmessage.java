/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
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
// Généré le : 2015.03.05 à 11:48:09 AM CET 
//


package org.taktik.icure.be.ehealth.dto.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.taktik.icure.be.ehealth.dto.kmehr.v20130710.org.w3.xmldsig.Signature;
import org.taktik.icure.be.ehealth.dto.kmehr.v20130710.org.w3.xmlenc.EncryptedType;


/**
 * to  transfer medical information about one or several patients (using one folder per patient).
 * 
 * <p>Classe Java pour kmehrmessageType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="kmehrmessageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confidentiality" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}confidentialityType" minOccurs="0"/>
 *         &lt;element name="header" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}headerType"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="folder" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}folderType" maxOccurs="unbounded"/>
 *             &lt;element name="Signature" type="{http://www.w3.org/2000/09/xmldsig#}SignatureType" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="EncryptedData" type="{http://www.w3.org/2001/04/xmlenc#}EncryptedType"/>
 *           &lt;element name="Base64EncryptedData" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}Base64EncryptedDataType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "kmehrmessageType", propOrder = {
    "confidentiality",
    "header",
    "base64EncryptedData",
    "encryptedData",
    "folders",
    "signature"
})
@XmlRootElement(name = "kmehrmessage")
public class Kmehrmessage
    implements Serializable
{

    private final static long serialVersionUID = 20130710L;
    protected ConfidentialityType confidentiality;
    @XmlElement(required = true)
    protected HeaderType header;
    @XmlElement(name = "Base64EncryptedData")
    protected Base64EncryptedDataType base64EncryptedData;
    @XmlElement(name = "EncryptedData")
    protected EncryptedType encryptedData;
    @XmlElement(name = "folder")
    protected List<FolderType> folders;
    @XmlElementRef(name = "Signature", namespace = "http://www.ehealth.fgov.be/standards/kmehr/schema/v1", type = JAXBElement.class, required = false)
    protected JAXBElement<Signature> signature;

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
     * Obtient la valeur de la propriété header.
     *
     * @return
     *     possible object is
     *     {@link HeaderType }
     *
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Définit la valeur de la propriété header.
     *
     * @param value
     *     allowed object is
     *     {@link HeaderType }
     *
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Obtient la valeur de la propriété base64EncryptedData.
     *
     * @return
     *     possible object is
     *     {@link Base64EncryptedDataType }
     *
     */
    public Base64EncryptedDataType getBase64EncryptedData() {
        return base64EncryptedData;
    }

    /**
     * Définit la valeur de la propriété base64EncryptedData.
     *
     * @param value
     *     allowed object is
     *     {@link Base64EncryptedDataType }
     *
     */
    public void setBase64EncryptedData(Base64EncryptedDataType value) {
        this.base64EncryptedData = value;
    }

    /**
     * Obtient la valeur de la propriété encryptedData.
     *
     * @return
     *     possible object is
     *     {@link EncryptedType }
     *
     */
    public EncryptedType getEncryptedData() {
        return encryptedData;
    }

    /**
     * Définit la valeur de la propriété encryptedData.
     *
     * @param value
     *     allowed object is
     *     {@link EncryptedType }
     *
     */
    public void setEncryptedData(EncryptedType value) {
        this.encryptedData = value;
    }

    /**
     * Gets the value of the folders property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folders property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolders().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FolderType }
     *
     *
     */
    public List<FolderType> getFolders() {
        if (folders == null) {
            folders = new ArrayList<FolderType>();
        }
        return this.folders;
    }

    /**
     * Obtient la valeur de la propriété signature.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Signature }{@code >}
     *     
     */
    public JAXBElement<Signature> getSignature() {
        return signature;
    }

    /**
     * Définit la valeur de la propriété signature.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Signature }{@code >}
     *     
     */
    public void setSignature(JAXBElement<Signature> value) {
        this.signature = value;
    }

}
