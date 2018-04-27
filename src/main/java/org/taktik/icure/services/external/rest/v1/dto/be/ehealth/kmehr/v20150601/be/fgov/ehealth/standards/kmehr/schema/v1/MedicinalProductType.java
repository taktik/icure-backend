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
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150601.be.fgov.ehealth.standards.kmehr.cd.v1.CDDRUGCNK;


/**
 * 
 *         a medicinal product can be identified
 *         unambiguously by a CNK code identifying
 *         a package. The descriptive
 *         identification is only mandatory in case
 *         of absence of a package ID.
 *       
 * 
 * <p>Classe Java pour medicinalProductType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="medicinalProductType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="intendedcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK" maxOccurs="unbounded"/>
 *         &lt;element name="deliveredcd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-DRUG-CNK" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="intendedname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="deliveredname" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "medicinalProductType", propOrder = {
    "intendedcds",
    "deliveredcds",
    "intendedname",
    "deliveredname"
})
public class MedicinalProductType
    implements Serializable
{

    private final static long serialVersionUID = 20150601L;
    @XmlElement(name = "intendedcd", required = true)
    protected List<CDDRUGCNK> intendedcds;
    @XmlElement(name = "deliveredcd")
    protected List<CDDRUGCNK> deliveredcds;
    @XmlElement(required = true)
    protected String intendedname;
    protected Object deliveredname;

    /**
     * Gets the value of the intendedcds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the intendedcds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntendedcds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CDDRUGCNK }
     * 
     * 
     */
    public List<CDDRUGCNK> getIntendedcds() {
        if (intendedcds == null) {
            intendedcds = new ArrayList<CDDRUGCNK>();
        }
        return this.intendedcds;
    }

    /**
     * Gets the value of the deliveredcds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deliveredcds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeliveredcds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CDDRUGCNK }
     * 
     * 
     */
    public List<CDDRUGCNK> getDeliveredcds() {
        if (deliveredcds == null) {
            deliveredcds = new ArrayList<CDDRUGCNK>();
        }
        return this.deliveredcds;
    }

    /**
     * Obtient la valeur de la propriété intendedname.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntendedname() {
        return intendedname;
    }

    /**
     * Définit la valeur de la propriété intendedname.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntendedname(String value) {
        this.intendedname = value;
    }

    /**
     * Obtient la valeur de la propriété deliveredname.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDeliveredname() {
        return deliveredname;
    }

    /**
     * Définit la valeur de la propriété deliveredname.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDeliveredname(Object value) {
        this.deliveredname = value;
    }

}
