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
// Généré le : 2015.03.05 à 11:47:54 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20100601.be.fgov.ehealth.standards.kmehr.cd.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Classe Java pour CD-WEEKDAY complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="CD-WEEKDAY">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.ehealth.fgov.be/standards/kmehr/cd/v1>CD-WEEKDAYvalues">
 *       &lt;attribute name="S" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="CD-WEEKDAY" />
 *       &lt;attribute name="SV" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CD-WEEKDAY", propOrder = {
    "value"
})
public class CDWEEKDAY
    implements Serializable
{

    private final static long serialVersionUID = 20100601L;
    @XmlValue
    protected CDWEEKDAYvalues value;
    @XmlAttribute(name = "S", required = true)
    protected String s;
    @XmlAttribute(name = "SV", required = true)
    protected String sv;

    /**
     * Obtient la valeur de la propriété value.
     * 
     * @return
     *     possible object is
     *     {@link CDWEEKDAYvalues }
     *     
     */
    public CDWEEKDAYvalues getValue() {
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     * 
     * @param value
     *     allowed object is
     *     {@link CDWEEKDAYvalues }
     *     
     */
    public void setValue(CDWEEKDAYvalues value) {
        this.value = value;
    }

    /**
     * Obtient la valeur de la propriété s.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getS() {
        if (s == null) {
            return "CD-WEEKDAY";
        } else {
            return s;
        }
    }

    /**
     * Définit la valeur de la propriété s.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setS(String value) {
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

}
