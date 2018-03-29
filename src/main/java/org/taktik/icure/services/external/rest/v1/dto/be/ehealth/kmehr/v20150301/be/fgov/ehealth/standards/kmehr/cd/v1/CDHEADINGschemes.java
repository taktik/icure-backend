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
// Généré le : 2015.11.10 à 11:53:40 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150301.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-HEADINGschemes.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-HEADINGschemes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CD-HEADING"/>
 *     &lt;enumeration value="CD-HEADING-LAB"/>
 *     &lt;enumeration value="CD-SITE"/>
 *     &lt;enumeration value="LOCAL"/>
 *     &lt;enumeration value="CD-HEADING-REG"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-HEADINGschemes")
@XmlEnum
public enum CDHEADINGschemes {

    @XmlEnumValue("CD-HEADING")
    CD_HEADING("CD-HEADING"),
    @XmlEnumValue("CD-HEADING-LAB")
    CD_HEADING_LAB("CD-HEADING-LAB"),
    @XmlEnumValue("CD-SITE")
    CD_SITE("CD-SITE"),
    LOCAL("LOCAL"),
    @XmlEnumValue("CD-HEADING-REG")
    CD_HEADING_REG("CD-HEADING-REG");
    private final String value;

    CDHEADINGschemes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDHEADINGschemes fromValue(String v) {
        for (CDHEADINGschemes c: CDHEADINGschemes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
