/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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


package org.taktik.icure.be.ehealth.dto.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-TRANSACTION-MYCARENETvalues.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-TRANSACTION-MYCARENETvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="gmd"/>
 *     &lt;enumeration value="gmdclosure"/>
 *     &lt;enumeration value="gmdextension"/>
 *     &lt;enumeration value="tariff"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-TRANSACTION-MYCARENETvalues")
@XmlEnum
public enum CDTRANSACTIONMYCARENETvalues {

    @XmlEnumValue("gmd")
    GMD("gmd"),
    @XmlEnumValue("gmdclosure")
    GMDCLOSURE("gmdclosure"),
    @XmlEnumValue("gmdextension")
    GMDEXTENSION("gmdextension"),
    @XmlEnumValue("tariff")
    TARIFF("tariff");
    private final String value;

    CDTRANSACTIONMYCARENETvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDTRANSACTIONMYCARENETvalues fromValue(String v) {
        for (CDTRANSACTIONMYCARENETvalues c: CDTRANSACTIONMYCARENETvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
