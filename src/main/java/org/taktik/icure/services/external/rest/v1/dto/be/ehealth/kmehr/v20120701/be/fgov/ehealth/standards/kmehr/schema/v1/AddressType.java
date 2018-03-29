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
// Généré le : 2015.03.05 à 11:48:04 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120701.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour addressType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="addressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}addressTypeBase">
 *       &lt;sequence>
 *         &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-ADDRESS" maxOccurs="unbounded"/>
 *         &lt;choice>
 *           &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;sequence>
 *             &lt;element name="country" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}countryType"/>
 *             &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *             &lt;element name="nis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *             &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *             &lt;element name="district" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *             &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *             &lt;element name="housenumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *             &lt;element name="postboxnumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *             &lt;element name="text" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addressType")
public class AddressType
    extends AddressTypeBase
    implements Serializable
{

    private final static long serialVersionUID = 20120701L;

}
