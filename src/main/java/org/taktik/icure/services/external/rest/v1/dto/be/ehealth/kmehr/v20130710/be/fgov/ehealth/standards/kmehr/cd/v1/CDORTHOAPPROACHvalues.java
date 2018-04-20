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
// Généré le : 2015.03.05 à 11:48:09 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-ORTHO-APPROACHvalues.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-ORTHO-APPROACHvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="lateral"/>
 *     &lt;enumeration value="posterior"/>
 *     &lt;enumeration value="anterior"/>
 *     &lt;enumeration value="bytrochanterotomy"/>
 *     &lt;enumeration value="withfemoralosteotomy"/>
 *     &lt;enumeration value="other"/>
 *     &lt;enumeration value="subvastus"/>
 *     &lt;enumeration value="midvastus"/>
 *     &lt;enumeration value="parapatellarlateral"/>
 *     &lt;enumeration value="parapatellarmedial"/>
 *     &lt;enumeration value="tuberositasosteotomie"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-ORTHO-APPROACHvalues")
@XmlEnum
public enum CDORTHOAPPROACHvalues {

    @XmlEnumValue("lateral")
    LATERAL("lateral"),
    @XmlEnumValue("posterior")
    POSTERIOR("posterior"),
    @XmlEnumValue("anterior")
    ANTERIOR("anterior"),
    @XmlEnumValue("bytrochanterotomy")
    BYTROCHANTEROTOMY("bytrochanterotomy"),
    @XmlEnumValue("withfemoralosteotomy")
    WITHFEMORALOSTEOTOMY("withfemoralosteotomy"),
    @XmlEnumValue("other")
    OTHER("other"),
    @XmlEnumValue("subvastus")
    SUBVASTUS("subvastus"),
    @XmlEnumValue("midvastus")
    MIDVASTUS("midvastus"),
    @XmlEnumValue("parapatellarlateral")
    PARAPATELLARLATERAL("parapatellarlateral"),
    @XmlEnumValue("parapatellarmedial")
    PARAPATELLARMEDIAL("parapatellarmedial"),
    @XmlEnumValue("tuberositasosteotomie")
    TUBEROSITASOSTEOTOMIE("tuberositasosteotomie");
    private final String value;

    CDORTHOAPPROACHvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDORTHOAPPROACHvalues fromValue(String v) {
        for (CDORTHOAPPROACHvalues c: CDORTHOAPPROACHvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
