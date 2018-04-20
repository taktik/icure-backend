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
// Généré le : 2015.03.05 à 11:48:06 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;


/**
 * frequency of applying the periodic posology, only in case of a constant scheme during a period of time
 * 
 * <p>Classe Java pour frequencyType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="frequencyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="nominator">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}timequantityType"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="denominator">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}timequantityType"/>
 *                   &lt;/sequence>
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="decimal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *           &lt;element name="unit" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}unitType"/>
 *         &lt;/sequence>
 *         &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *         &lt;element name="periodicity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}periodicityType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "frequencyType", propOrder = {
    "periodicity",
    "text",
    "decimal",
    "unit",
    "nominator",
    "denominator"
})
public class FrequencyType
    implements Serializable
{

    private final static long serialVersionUID = 20121001L;
    protected PeriodicityType periodicity;
    protected TextType text;
    protected BigDecimal decimal;
    protected UnitType unit;
    protected FrequencyType.Nominator nominator;
    protected FrequencyType.Denominator denominator;

    /**
     * Obtient la valeur de la propriété periodicity.
     * 
     * @return
     *     possible object is
     *     {@link PeriodicityType }
     *     
     */
    public PeriodicityType getPeriodicity() {
        return periodicity;
    }

    /**
     * Définit la valeur de la propriété periodicity.
     * 
     * @param value
     *     allowed object is
     *     {@link PeriodicityType }
     *     
     */
    public void setPeriodicity(PeriodicityType value) {
        this.periodicity = value;
    }

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
     * Obtient la valeur de la propriété nominator.
     * 
     * @return
     *     possible object is
     *     {@link FrequencyType.Nominator }
     *     
     */
    public FrequencyType.Nominator getNominator() {
        return nominator;
    }

    /**
     * Définit la valeur de la propriété nominator.
     * 
     * @param value
     *     allowed object is
     *     {@link FrequencyType.Nominator }
     *     
     */
    public void setNominator(FrequencyType.Nominator value) {
        this.nominator = value;
    }

    /**
     * Obtient la valeur de la propriété denominator.
     * 
     * @return
     *     possible object is
     *     {@link FrequencyType.Denominator }
     *     
     */
    public FrequencyType.Denominator getDenominator() {
        return denominator;
    }

    /**
     * Définit la valeur de la propriété denominator.
     * 
     * @param value
     *     allowed object is
     *     {@link FrequencyType.Denominator }
     *     
     */
    public void setDenominator(FrequencyType.Denominator value) {
        this.denominator = value;
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
     *         &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}timequantityType"/>
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
        "quantity"
    })
    public static class Denominator
        implements Serializable
    {

        private final static long serialVersionUID = 20121001L;
        @XmlElement(required = true)
        protected TimequantityType quantity;

        /**
         * Obtient la valeur de la propriété quantity.
         * 
         * @return
         *     possible object is
         *     {@link TimequantityType }
         *     
         */
        public TimequantityType getQuantity() {
            return quantity;
        }

        /**
         * Définit la valeur de la propriété quantity.
         * 
         * @param value
         *     allowed object is
         *     {@link TimequantityType }
         *     
         */
        public void setQuantity(TimequantityType value) {
            this.quantity = value;
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
     *         &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}timequantityType"/>
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
        "quantity"
    })
    public static class Nominator
        implements Serializable
    {

        private final static long serialVersionUID = 20121001L;
        @XmlElement(required = true)
        protected TimequantityType quantity;

        /**
         * Obtient la valeur de la propriété quantity.
         * 
         * @return
         *     possible object is
         *     {@link TimequantityType }
         *     
         */
        public TimequantityType getQuantity() {
            return quantity;
        }

        /**
         * Définit la valeur de la propriété quantity.
         * 
         * @param value
         *     allowed object is
         *     {@link TimequantityType }
         *     
         */
        public void setQuantity(TimequantityType value) {
            this.quantity = value;
        }

    }

}
