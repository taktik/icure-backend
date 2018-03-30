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


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDMESSAGE;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDSTANDARD;


/**
 * to specify the version of the kmehr specification to which this message complies
 * 
 * <p>Classe Java pour standardType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="standardType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-STANDARD"/>
 *         &lt;element name="specialisation" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-MESSAGE"/>
 *                   &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "standardType", propOrder = {
    "cd",
    "specialisation"
})
public class StandardType
    implements Serializable
{

    private final static long serialVersionUID = 20121001L;
    @XmlElement(required = true)
    protected CDSTANDARD cd;
    protected StandardType.Specialisation specialisation;

    /**
     * Obtient la valeur de la propriété cd.
     * 
     * @return
     *     possible object is
     *     {@link CDSTANDARD }
     *     
     */
    public CDSTANDARD getCd() {
        return cd;
    }

    /**
     * Définit la valeur de la propriété cd.
     * 
     * @param value
     *     allowed object is
     *     {@link CDSTANDARD }
     *     
     */
    public void setCd(CDSTANDARD value) {
        this.cd = value;
    }

    /**
     * Obtient la valeur de la propriété specialisation.
     * 
     * @return
     *     possible object is
     *     {@link StandardType.Specialisation }
     *     
     */
    public StandardType.Specialisation getSpecialisation() {
        return specialisation;
    }

    /**
     * Définit la valeur de la propriété specialisation.
     * 
     * @param value
     *     allowed object is
     *     {@link StandardType.Specialisation }
     *     
     */
    public void setSpecialisation(StandardType.Specialisation value) {
        this.specialisation = value;
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
     *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-MESSAGE"/>
     *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "cd",
        "version"
    })
    public static class Specialisation
        implements Serializable
    {

        private final static long serialVersionUID = 20121001L;
        @XmlElement(required = true)
        protected CDMESSAGE cd;
        @XmlElement(required = true)
        protected String version;

        /**
         * Obtient la valeur de la propriété cd.
         * 
         * @return
         *     possible object is
         *     {@link CDMESSAGE }
         *     
         */
        public CDMESSAGE getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDMESSAGE }
         *     
         */
        public void setCd(CDMESSAGE value) {
            this.cd = value;
        }

        /**
         * Obtient la valeur de la propriété version.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getVersion() {
            return version;
        }

        /**
         * Définit la valeur de la propriété version.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setVersion(String value) {
            this.version = value;
        }

    }

}
