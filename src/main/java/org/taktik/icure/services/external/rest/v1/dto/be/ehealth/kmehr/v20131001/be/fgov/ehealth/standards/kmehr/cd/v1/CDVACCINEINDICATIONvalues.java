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
// Généré le : 2015.03.05 à 11:48:12 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20131001.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-VACCINEINDICATIONvalues.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-VACCINEINDICATIONvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="poliomyelitis"/>
 *     &lt;enumeration value="measles"/>
 *     &lt;enumeration value="rubella"/>
 *     &lt;enumeration value="mumps"/>
 *     &lt;enumeration value="seasonalinfluenza"/>
 *     &lt;enumeration value="hepatitisa"/>
 *     &lt;enumeration value="hepatitisb"/>
 *     &lt;enumeration value="rabies"/>
 *     &lt;enumeration value="varicella"/>
 *     &lt;enumeration value="rotavirus"/>
 *     &lt;enumeration value="papillomavirus"/>
 *     &lt;enumeration value="yellowfever"/>
 *     &lt;enumeration value="tickborneencephalitis"/>
 *     &lt;enumeration value="ej"/>
 *     &lt;enumeration value="diphteria"/>
 *     &lt;enumeration value="tetanus"/>
 *     &lt;enumeration value="pertussis"/>
 *     &lt;enumeration value="hib"/>
 *     &lt;enumeration value="meningitisc"/>
 *     &lt;enumeration value="meningitis"/>
 *     &lt;enumeration value="pneumonia23"/>
 *     &lt;enumeration value="pneumonia7"/>
 *     &lt;enumeration value="tuberculosis"/>
 *     &lt;enumeration value="typhoid"/>
 *     &lt;enumeration value="pandemics"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-VACCINEINDICATIONvalues")
@XmlEnum
public enum CDVACCINEINDICATIONvalues {

    @XmlEnumValue("poliomyelitis")
    POLIOMYELITIS("poliomyelitis"),
    @XmlEnumValue("measles")
    MEASLES("measles"),
    @XmlEnumValue("rubella")
    RUBELLA("rubella"),
    @XmlEnumValue("mumps")
    MUMPS("mumps"),
    @XmlEnumValue("seasonalinfluenza")
    SEASONALINFLUENZA("seasonalinfluenza"),
    @XmlEnumValue("hepatitisa")
    HEPATITISA("hepatitisa"),
    @XmlEnumValue("hepatitisb")
    HEPATITISB("hepatitisb"),
    @XmlEnumValue("rabies")
    RABIES("rabies"),
    @XmlEnumValue("varicella")
    VARICELLA("varicella"),
    @XmlEnumValue("rotavirus")
    ROTAVIRUS("rotavirus"),
    @XmlEnumValue("papillomavirus")
    PAPILLOMAVIRUS("papillomavirus"),
    @XmlEnumValue("yellowfever")
    YELLOWFEVER("yellowfever"),
    @XmlEnumValue("tickborneencephalitis")
    TICKBORNEENCEPHALITIS("tickborneencephalitis"),
    @XmlEnumValue("ej")
    EJ("ej"),
    @XmlEnumValue("diphteria")
    DIPHTERIA("diphteria"),
    @XmlEnumValue("tetanus")
    TETANUS("tetanus"),
    @XmlEnumValue("pertussis")
    PERTUSSIS("pertussis"),
    @XmlEnumValue("hib")
    HIB("hib"),
    @XmlEnumValue("meningitisc")
    MENINGITISC("meningitisc"),
    @XmlEnumValue("meningitis")
    MENINGITIS("meningitis"),
    @XmlEnumValue("pneumonia23")
    PNEUMONIA_23("pneumonia23"),
    @XmlEnumValue("pneumonia7")
    PNEUMONIA_7("pneumonia7"),
    @XmlEnumValue("tuberculosis")
    TUBERCULOSIS("tuberculosis"),
    @XmlEnumValue("typhoid")
    TYPHOID("typhoid"),
    @XmlEnumValue("pandemics")
    PANDEMICS("pandemics");
    private final String value;

    CDVACCINEINDICATIONvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDVACCINEINDICATIONvalues fromValue(String v) {
        for (CDVACCINEINDICATIONvalues c: CDVACCINEINDICATIONvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
