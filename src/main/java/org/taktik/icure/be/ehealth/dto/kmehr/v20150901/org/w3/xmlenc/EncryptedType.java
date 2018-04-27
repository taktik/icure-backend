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


package org.taktik.icure.be.ehealth.dto.kmehr.v20150901.org.w3.xmlenc;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.taktik.icure.be.ehealth.dto.kmehr.v20150901.org.w3.xmldsig.KeyInfo;


/**
 * <p>Classe Java pour EncryptedType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="EncryptedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EncryptionMethod" type="{http://www.w3.org/2001/04/xmlenc#}EncryptionMethodType" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}KeyInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}CipherData"/>
 *         &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}EncryptionProperties" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="MimeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Encoding" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EncryptedType", propOrder = {
    "encryptionMethod",
    "keyInfo",
    "cipherData",
    "encryptionProperties"
})
@XmlSeeAlso({
    EncryptedData.class,
    EncryptedKey.class
})
public abstract class EncryptedType
    implements Serializable
{

    private final static long serialVersionUID = 20150901L;
    @XmlElement(name = "EncryptionMethod")
    protected EncryptionMethodType encryptionMethod;
    @XmlElement(name = "KeyInfo", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected KeyInfo keyInfo;
    @XmlElement(name = "CipherData", required = true)
    protected CipherData cipherData;
    @XmlElement(name = "EncryptionProperties")
    protected EncryptionProperties encryptionProperties;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anyURI")
    protected String type;
    @XmlAttribute(name = "MimeType")
    protected String mimeType;
    @XmlAttribute(name = "Encoding")
    @XmlSchemaType(name = "anyURI")
    protected String encoding;

    /**
     * Obtient la valeur de la propriété encryptionMethod.
     *
     * @return
     *     possible object is
     *     {@link EncryptionMethodType }
     *
     */
    public EncryptionMethodType getEncryptionMethod() {
        return encryptionMethod;
    }

    /**
     * Définit la valeur de la propriété encryptionMethod.
     *
     * @param value
     *     allowed object is
     *     {@link EncryptionMethodType }
     *
     */
    public void setEncryptionMethod(EncryptionMethodType value) {
        this.encryptionMethod = value;
    }

    /**
     * Obtient la valeur de la propriété keyInfo.
     *
     * @return
     *     possible object is
     *     {@link KeyInfo }
     *
     */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /**
     * Définit la valeur de la propriété keyInfo.
     *
     * @param value
     *     allowed object is
     *     {@link KeyInfo }
     *
     */
    public void setKeyInfo(KeyInfo value) {
        this.keyInfo = value;
    }

    /**
     * Obtient la valeur de la propriété cipherData.
     *
     * @return
     *     possible object is
     *     {@link CipherData }
     *
     */
    public CipherData getCipherData() {
        return cipherData;
    }

    /**
     * Définit la valeur de la propriété cipherData.
     *
     * @param value
     *     allowed object is
     *     {@link CipherData }
     *
     */
    public void setCipherData(CipherData value) {
        this.cipherData = value;
    }

    /**
     * Obtient la valeur de la propriété encryptionProperties.
     *
     * @return
     *     possible object is
     *     {@link EncryptionProperties }
     *
     */
    public EncryptionProperties getEncryptionProperties() {
        return encryptionProperties;
    }

    /**
     * Définit la valeur de la propriété encryptionProperties.
     *
     * @param value
     *     allowed object is
     *     {@link EncryptionProperties }
     *     
     */
    public void setEncryptionProperties(EncryptionProperties value) {
        this.encryptionProperties = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété mimeType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Définit la valeur de la propriété mimeType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Obtient la valeur de la propriété encoding.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Définit la valeur de la propriété encoding.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

}
