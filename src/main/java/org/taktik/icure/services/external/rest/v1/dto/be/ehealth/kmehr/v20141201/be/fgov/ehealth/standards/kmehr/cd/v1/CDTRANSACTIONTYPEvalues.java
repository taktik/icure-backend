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
// Généré le : 2015.03.05 à 11:48:22 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20141201.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-TRANSACTION-TYPEvalues.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-TRANSACTION-TYPEvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="incapacity"/>
 *     &lt;enumeration value="incapacityextension"/>
 *     &lt;enumeration value="incapacityrelapse"/>
 *     &lt;enumeration value="nursing"/>
 *     &lt;enumeration value="physiotherapy"/>
 *     &lt;enumeration value="intermediarynursing"/>
 *     &lt;enumeration value="intermediaryphysiotherapy"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-TRANSACTION-TYPEvalues")
@XmlEnum
public enum CDTRANSACTIONTYPEvalues {

    @XmlEnumValue("incapacity")
    INCAPACITY("incapacity"),
    @XmlEnumValue("incapacityextension")
    INCAPACITYEXTENSION("incapacityextension"),
    @XmlEnumValue("incapacityrelapse")
    INCAPACITYRELAPSE("incapacityrelapse"),
    @XmlEnumValue("nursing")
    NURSING("nursing"),
    @XmlEnumValue("physiotherapy")
    PHYSIOTHERAPY("physiotherapy"),
    @XmlEnumValue("intermediarynursing")
    INTERMEDIARYNURSING("intermediarynursing"),
    @XmlEnumValue("intermediaryphysiotherapy")
    INTERMEDIARYPHYSIOTHERAPY("intermediaryphysiotherapy");
    private final String value;

    CDTRANSACTIONTYPEvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDTRANSACTIONTYPEvalues fromValue(String v) {
        for (CDTRANSACTIONTYPEvalues c: CDTRANSACTIONTYPEvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
