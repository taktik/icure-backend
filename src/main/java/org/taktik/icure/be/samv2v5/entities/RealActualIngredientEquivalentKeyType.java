//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.10.15 at 03:32:18 PM CEST 
//


package org.taktik.icure.be.samv2v5.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RealActualIngredientEquivalentKeyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RealActualIngredientEquivalentKeyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="sequenceNr" use="required" type="{urn:be:fgov:ehealth:samws:v2:core}PositiveShortType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealActualIngredientEquivalentKeyType", namespace = "urn:be:fgov:ehealth:samws:v2:actual:common")
@XmlSeeAlso({
    RealActualIngredientEquivalentFullDataType.class,
    RealActualIngredientEquivalentType.class
})
public class RealActualIngredientEquivalentKeyType {

    @XmlAttribute(name = "sequenceNr", required = true)
    protected short sequenceNr;

    /**
     * Gets the value of the sequenceNr property.
     * 
     */
    public short getSequenceNr() {
        return sequenceNr;
    }

    /**
     * Sets the value of the sequenceNr property.
     * 
     */
    public void setSequenceNr(short value) {
        this.sequenceNr = value;
    }

}
