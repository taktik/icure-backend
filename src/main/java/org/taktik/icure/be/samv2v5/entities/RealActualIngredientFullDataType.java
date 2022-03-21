/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2020.10.15 at 03:32:18 PM CEST
//


package org.taktik.icure.be.samv2v5.entities;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RealActualIngredientFullDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RealActualIngredientFullDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:be:fgov:ehealth:samws:v2:actual:common}RealActualIngredientKeyType">
 *       &lt;sequence>
 *         &lt;element name="Data" type="{urn:be:fgov:ehealth:samws:v2:export}RealActualIngredientDataType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="RealActualIngredientEquivalent" type="{urn:be:fgov:ehealth:samws:v2:export}RealActualIngredientEquivalentFullDataType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RealActualIngredientFullDataType", propOrder = {
    "data",
    "realActualIngredientEquivalent"
})
public class RealActualIngredientFullDataType
    extends RealActualIngredientKeyType
{

    @XmlElement(name = "Data")
    protected List<RealActualIngredientDataType> data;
    @XmlElement(name = "RealActualIngredientEquivalent")
    protected List<RealActualIngredientEquivalentFullDataType> realActualIngredientEquivalent;

    /**
     * Gets the value of the data property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the data property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getData().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RealActualIngredientDataType }
     *
     *
     */
    public List<RealActualIngredientDataType> getData() {
        if (data == null) {
            data = new ArrayList<RealActualIngredientDataType>();
        }
        return this.data;
    }

    /**
     * Gets the value of the realActualIngredientEquivalent property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the realActualIngredientEquivalent property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRealActualIngredientEquivalent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RealActualIngredientEquivalentFullDataType }
     *
     *
     */
    public List<RealActualIngredientEquivalentFullDataType> getRealActualIngredientEquivalent() {
        if (realActualIngredientEquivalent == null) {
            realActualIngredientEquivalent = new ArrayList<RealActualIngredientEquivalentFullDataType>();
        }
        return this.realActualIngredientEquivalent;
    }

}
