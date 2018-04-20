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


package org.taktik.icure.be.ehealth.dto.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.id.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour ID-PROFESSION complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="ID-PROFESSION">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="S" use="required" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-PROFESSIONschemes" />
 *       &lt;attribute name="SV" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SL" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ID-PROFESSION", propOrder = {
    "value"
})
public class IDPROFESSION
    implements Serializable
{

    private final static long serialVersionUID = 20150901L;
    @XmlValue
    protected String value;
    @XmlAttribute(name = "S", required = true)
    protected IDPROFESSIONschemes s;
    @XmlAttribute(name = "SV", required = true)
    protected String sv = "1.0";
    @XmlAttribute(name = "SL")
    protected String sl;

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété s.
     *
     * @return
     *     possible object is
     *     {@link IDPROFESSIONschemes }
     *
     */
    public IDPROFESSIONschemes getS() {
        return s;
    }

    /**
     * Définit la valeur de la propriété s.
     *
     * @param value
     *     allowed object is
     *     {@link IDPROFESSIONschemes }
     *
     */
    public void setS(IDPROFESSIONschemes value) {
        this.s = value;
    }

    /**
     * Obtient la valeur de la propriété sv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSV() {
        return sv;
    }

    /**
     * Définit la valeur de la propriété sv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSV(String value) {
        this.sv = value;
    }

    /**
     * Obtient la valeur de la propriété sl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSL() {
        return sl;
    }

    /**
     * Définit la valeur de la propriété sl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSL(String value) {
        this.sl = value;
    }

}
