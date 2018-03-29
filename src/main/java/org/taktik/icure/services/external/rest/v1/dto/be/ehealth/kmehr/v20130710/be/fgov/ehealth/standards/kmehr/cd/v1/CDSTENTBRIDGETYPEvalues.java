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


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-STENT-BRIDGETYPEvalues.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-STENT-BRIDGETYPEvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="saphena1"/>
 *     &lt;enumeration value="saphena2"/>
 *     &lt;enumeration value="saphena3"/>
 *     &lt;enumeration value="saphena4"/>
 *     &lt;enumeration value="saphena5"/>
 *     &lt;enumeration value="lima"/>
 *     &lt;enumeration value="rima"/>
 *     &lt;enumeration value="gepa"/>
 *     &lt;enumeration value="freeima"/>
 *     &lt;enumeration value="radialis"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-STENT-BRIDGETYPEvalues")
@XmlEnum
public enum CDSTENTBRIDGETYPEvalues {

    @XmlEnumValue("saphena1")
    SAPHENA_1("saphena1"),
    @XmlEnumValue("saphena2")
    SAPHENA_2("saphena2"),
    @XmlEnumValue("saphena3")
    SAPHENA_3("saphena3"),
    @XmlEnumValue("saphena4")
    SAPHENA_4("saphena4"),
    @XmlEnumValue("saphena5")
    SAPHENA_5("saphena5"),
    @XmlEnumValue("lima")
    LIMA("lima"),
    @XmlEnumValue("rima")
    RIMA("rima"),
    @XmlEnumValue("gepa")
    GEPA("gepa"),
    @XmlEnumValue("freeima")
    FREEIMA("freeima"),
    @XmlEnumValue("radialis")
    RADIALIS("radialis");
    private final String value;

    CDSTENTBRIDGETYPEvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDSTENTBRIDGETYPEvalues fromValue(String v) {
        for (CDSTENTBRIDGETYPEvalues c: CDSTENTBRIDGETYPEvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
