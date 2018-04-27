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


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20150601.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour CD-TRANSACTIONschemes.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-TRANSACTIONschemes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CD-TRANSACTION"/>
 *     &lt;enumeration value="CD-TRANSACTION-CARENET"/>
 *     &lt;enumeration value="CD-TRANSACTION-MAA"/>
 *     &lt;enumeration value="CD-CHAPTER4APPENDIX"/>
 *     &lt;enumeration value="CD-TRANSACTION-REG"/>
 *     &lt;enumeration value="CD-TRANSACTION-MYCARENET"/>
 *     &lt;enumeration value="CD-TRANSACTION-TYPE"/>
 *     &lt;enumeration value="LOCAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-TRANSACTIONschemes")
@XmlEnum
public enum CDTRANSACTIONschemes {

    @XmlEnumValue("CD-TRANSACTION")
    CD_TRANSACTION("CD-TRANSACTION"),
    @XmlEnumValue("CD-TRANSACTION-CARENET")
    CD_TRANSACTION_CARENET("CD-TRANSACTION-CARENET"),
    @XmlEnumValue("CD-TRANSACTION-MAA")
    CD_TRANSACTION_MAA("CD-TRANSACTION-MAA"),
    @XmlEnumValue("CD-CHAPTER4APPENDIX")
    CD_CHAPTER_4_APPENDIX("CD-CHAPTER4APPENDIX"),
    @XmlEnumValue("CD-TRANSACTION-REG")
    CD_TRANSACTION_REG("CD-TRANSACTION-REG"),
    @XmlEnumValue("CD-TRANSACTION-MYCARENET")
    CD_TRANSACTION_MYCARENET("CD-TRANSACTION-MYCARENET"),
    @XmlEnumValue("CD-TRANSACTION-TYPE")
    CD_TRANSACTION_TYPE("CD-TRANSACTION-TYPE"),
    LOCAL("LOCAL");
    private final String value;

    CDTRANSACTIONschemes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDTRANSACTIONschemes fromValue(String v) {
        for (CDTRANSACTIONschemes c: CDTRANSACTIONschemes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
