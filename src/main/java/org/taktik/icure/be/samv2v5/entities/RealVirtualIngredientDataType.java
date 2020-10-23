//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.10.15 at 03:32:18 PM CEST 
//


package org.taktik.icure.be.samv2v5.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RealVirtualIngredientDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealVirtualIngredientDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:be:fgov:ehealth:samws:v2:export}DataPeriodType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:be:fgov:ehealth:samws:v2:virtual:common}RealVirtualIngredientFields"/>
 *         &lt;group ref="{urn:be:fgov:ehealth:samws:v2:export}RealVirtualIngredientReferenceFields"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealVirtualIngredientDataType", propOrder = {
    "type",
    "strength",
    "substance"
})
public class RealVirtualIngredientDataType
    extends DataPeriodType
{

    @XmlElement(name = "Type", namespace = "urn:be:fgov:ehealth:samws:v2:virtual:common", required = true)
    @XmlSchemaType(name = "string")
    protected IngredientTypeType type;
    @XmlElement(name = "Strength", namespace = "urn:be:fgov:ehealth:samws:v2:virtual:common")
    protected StrengthRangeType strength;
    @XmlElement(name = "Substance", required = true)
    protected SubstanceWithStandardsType substance;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link IngredientTypeType }
     *     
     */
    public IngredientTypeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link IngredientTypeType }
     *     
     */
    public void setType(IngredientTypeType value) {
        this.type = value;
    }

    /**
     * Gets the value of the strength property.
     * 
     * @return
     *     possible object is
     *     {@link StrengthRangeType }
     *     
     */
    public StrengthRangeType getStrength() {
        return strength;
    }

    /**
     * Sets the value of the strength property.
     * 
     * @param value
     *     allowed object is
     *     {@link StrengthRangeType }
     *     
     */
    public void setStrength(StrengthRangeType value) {
        this.strength = value;
    }

    /**
     * Gets the value of the substance property.
     * 
     * @return
     *     possible object is
     *     {@link SubstanceWithStandardsType }
     *     
     */
    public SubstanceWithStandardsType getSubstance() {
        return substance;
    }

    /**
     * Sets the value of the substance property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubstanceWithStandardsType }
     *     
     */
    public void setSubstance(SubstanceWithStandardsType value) {
        this.substance = value;
    }

}
