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
// Généré le : 2015.03.05 à 11:48:19 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20141001.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20141001.be.fgov.ehealth.standards.kmehr.id.v1.IDINSURANCE;


/**
 * <p>Classe Java pour memberinsuranceType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="memberinsuranceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.ehealth.fgov.be/standards/kmehr/id/v1}ID-INSURANCE"/>
 *         &lt;element name="membership" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberinsuranceType", propOrder = {
    "id",
    "membership"
})
public class MemberinsuranceType
    implements Serializable
{

    private final static long serialVersionUID = 20141001L;
    @XmlElement(required = true)
    protected IDINSURANCE id;
    @XmlElement(required = true)
    protected Object membership;

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return
     *     possible object is
     *     {@link IDINSURANCE }
     *     
     */
    public IDINSURANCE getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value
     *     allowed object is
     *     {@link IDINSURANCE }
     *     
     */
    public void setId(IDINSURANCE value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété membership.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getMembership() {
        return membership;
    }

    /**
     * Définit la valeur de la propriété membership.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setMembership(Object value) {
        this.membership = value;
    }

}
