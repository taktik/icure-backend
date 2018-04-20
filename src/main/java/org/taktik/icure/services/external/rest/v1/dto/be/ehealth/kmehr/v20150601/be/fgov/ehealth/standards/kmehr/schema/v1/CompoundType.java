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
// Généré le : 2015.11.10 à 11:53:43 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150601.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150601.be.fgov.ehealth.standards.kmehr.cd.v1.CDQUANTITYPREFIX;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150601.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;


/**
 * <p>Classe Java pour compoundType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="compoundType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-KMEHR" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="medicinalproduct" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}medicinalProductType"/>
 *           &lt;element name="substance" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}substanceType"/>
 *         &lt;/choice>
 *         &lt;element name="quantityprefix" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-QUANTITYPREFIX"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="quantity" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}quantityType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compoundType", propOrder = {
    "ids",
    "substance",
    "medicinalproduct",
    "quantityprefix",
    "quantity"
})
public class CompoundType
    implements Serializable
{

    private final static long serialVersionUID = 20150601L;
    @XmlElement(name = "id")
    protected List<IDKMEHR> ids;
    protected SubstanceType substance;
    protected MedicinalProductType medicinalproduct;
    protected CompoundType.Quantityprefix quantityprefix;
    protected QuantityType quantity;

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
     * Obtient la valeur de la propriété substance.
     * 
     * @return
     *     possible object is
     *     {@link SubstanceType }
     *     
     */
    public SubstanceType getSubstance() {
        return substance;
    }

    /**
     * Définit la valeur de la propriété substance.
     * 
     * @param value
     *     allowed object is
     *     {@link SubstanceType }
     *     
     */
    public void setSubstance(SubstanceType value) {
        this.substance = value;
    }

    /**
     * Obtient la valeur de la propriété medicinalproduct.
     * 
     * @return
     *     possible object is
     *     {@link MedicinalProductType }
     *     
     */
    public MedicinalProductType getMedicinalproduct() {
        return medicinalproduct;
    }

    /**
     * Définit la valeur de la propriété medicinalproduct.
     * 
     * @param value
     *     allowed object is
     *     {@link MedicinalProductType }
     *     
     */
    public void setMedicinalproduct(MedicinalProductType value) {
        this.medicinalproduct = value;
    }

    /**
     * Obtient la valeur de la propriété quantityprefix.
     * 
     * @return
     *     possible object is
     *     {@link CompoundType.Quantityprefix }
     *     
     */
    public CompoundType.Quantityprefix getQuantityprefix() {
        return quantityprefix;
    }

    /**
     * Définit la valeur de la propriété quantityprefix.
     * 
     * @param value
     *     allowed object is
     *     {@link CompoundType.Quantityprefix }
     *     
     */
    public void setQuantityprefix(CompoundType.Quantityprefix value) {
        this.quantityprefix = value;
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
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-QUANTITYPREFIX"/>
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
    public static class Quantityprefix
        implements Serializable
    {

        private final static long serialVersionUID = 20150601L;
        @XmlElement(required = true)
        protected CDQUANTITYPREFIX cd;

        /**
         * Obtient la valeur de la propriété cd.
         * 
         * @return
         *     possible object is
         *     {@link CDQUANTITYPREFIX }
         *     
         */
        public CDQUANTITYPREFIX getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         * 
         * @param value
         *     allowed object is
         *     {@link CDQUANTITYPREFIX }
         *     
         */
        public void setCd(CDQUANTITYPREFIX value) {
            this.cd = value;
        }

    }

}
