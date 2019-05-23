//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2019.05.22 at 08:11:32 PM CEST
//


package org.taktik.icure.be.ehealth.samws.v2.actual.common;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AmppNihdiType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AmppNihdiType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:be:fgov:ehealth:samws:v2:actual:common}AmppKeyType">
 *       &lt;sequence>
 *         &lt;group ref="{urn:be:fgov:ehealth:samws:v2:actual:common}AmppNihdiFields"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmppNihdiType", propOrder = {
    "exFactoryPrice",
    "reimbursementCode"
})
@XmlSeeAlso({
    AddAmppNihdiType.class
})
public class AmppNihdiType
    extends AmppKeyType
    implements Serializable
{

    private final static long serialVersionUID = 2L;
    @XmlElement(name = "ExFactoryPrice")
    protected BigDecimal exFactoryPrice;
    @XmlElement(name = "ReimbursementCode")
    @XmlSchemaType(name = "integer")
    protected Integer reimbursementCode;

    /**
     * Gets the value of the exFactoryPrice property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getExFactoryPrice() {
        return exFactoryPrice;
    }

    /**
     * Sets the value of the exFactoryPrice property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setExFactoryPrice(BigDecimal value) {
        this.exFactoryPrice = value;
    }

    /**
     * Gets the value of the reimbursementCode property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getReimbursementCode() {
        return reimbursementCode;
    }

    /**
     * Sets the value of the reimbursementCode property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setReimbursementCode(Integer value) {
        this.reimbursementCode = value;
    }

}
