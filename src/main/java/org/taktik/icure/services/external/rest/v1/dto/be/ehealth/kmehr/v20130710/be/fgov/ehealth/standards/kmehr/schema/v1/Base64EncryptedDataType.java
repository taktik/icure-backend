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
// Généré le : 2015.03.05 à 11:48:09 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.cd.v1.CDENCRYPTIONMETHOD;


/**
 * <p>Classe Java pour Base64EncryptedDataType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Base64EncryptedDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-ENCRYPTION-METHOD"/>
 *         &lt;element name="Base64EncryptedValue" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}Base64EncryptedValueType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Base64EncryptedDataType", propOrder = {
    "cd",
    "base64EncryptedValue"
})
public class Base64EncryptedDataType
    implements Serializable
{

    private final static long serialVersionUID = 20130710L;
    @XmlElement(required = true)
    protected CDENCRYPTIONMETHOD cd;
    @XmlElement(name = "Base64EncryptedValue", required = true)
    protected Base64EncryptedValueType base64EncryptedValue;

    /**
     * Obtient la valeur de la propriété cd.
     * 
     * @return
     *     possible object is
     *     {@link CDENCRYPTIONMETHOD }
     *     
     */
    public CDENCRYPTIONMETHOD getCd() {
        return cd;
    }

    /**
     * Définit la valeur de la propriété cd.
     * 
     * @param value
     *     allowed object is
     *     {@link CDENCRYPTIONMETHOD }
     *     
     */
    public void setCd(CDENCRYPTIONMETHOD value) {
        this.cd = value;
    }

    /**
     * Obtient la valeur de la propriété base64EncryptedValue.
     * 
     * @return
     *     possible object is
     *     {@link Base64EncryptedValueType }
     *     
     */
    public Base64EncryptedValueType getBase64EncryptedValue() {
        return base64EncryptedValue;
    }

    /**
     * Définit la valeur de la propriété base64EncryptedValue.
     * 
     * @param value
     *     allowed object is
     *     {@link Base64EncryptedValueType }
     *     
     */
    public void setBase64EncryptedValue(Base64EncryptedValueType value) {
        this.base64EncryptedValue = value;
    }

}
