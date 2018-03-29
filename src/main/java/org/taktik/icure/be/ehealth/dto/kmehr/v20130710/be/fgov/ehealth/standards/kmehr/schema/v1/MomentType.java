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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.taktik.icure.be.ehealth.dto.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;


/**
 * <p>Classe Java pour momentType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="momentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;choice>
 *           &lt;element name="year" type="{http://www.w3.org/2001/XMLSchema}gYear"/>
 *           &lt;element name="yearmonth" type="{http://www.w3.org/2001/XMLSchema}gYearMonth"/>
 *         &lt;/choice>
 *         &lt;sequence>
 *           &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *           &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "momentType", propOrder = {
    "text",
    "date",
    "time",
    "yearmonth",
    "year"
})
@XmlSeeAlso({
    DateType.class
})
public class MomentType
    implements Serializable
{

    private final static long serialVersionUID = 20130710L;
    protected TextType text;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar date;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar time;
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar yearmonth;
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar year;

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
     * Obtient la valeur de la propriété date.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Définit la valeur de la propriété date.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Obtient la valeur de la propriété time.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Définit la valeur de la propriété time.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Obtient la valeur de la propriété yearmonth.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getYearmonth() {
        return yearmonth;
    }

    /**
     * Définit la valeur de la propriété yearmonth.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setYearmonth(XMLGregorianCalendar value) {
        this.yearmonth = value;
    }

    /**
     * Obtient la valeur de la propriété year.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getYear() {
        return year;
    }

    /**
     * Définit la valeur de la propriété year.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setYear(XMLGregorianCalendar value) {
        this.year = value;
    }

}
