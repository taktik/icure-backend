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
 * <p>Classe Java pour CD-ITEM-MAAvalues.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-ITEM-MAAvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="agreementtype"/>
 *     &lt;enumeration value="requesttype"/>
 *     &lt;enumeration value="responsetype"/>
 *     &lt;enumeration value="agreementstartdate"/>
 *     &lt;enumeration value="agreementenddate"/>
 *     &lt;enumeration value="consultationstartdate"/>
 *     &lt;enumeration value="consultationenddate"/>
 *     &lt;enumeration value="careproviderreference"/>
 *     &lt;enumeration value="iorequestreference"/>
 *     &lt;enumeration value="decisionreference"/>
 *     &lt;enumeration value="refusaljustification"/>
 *     &lt;enumeration value="chapter4reference"/>
 *     &lt;enumeration value="chapter4annexreference"/>
 *     &lt;enumeration value="unitnumber"/>
 *     &lt;enumeration value="strength"/>
 *     &lt;enumeration value="restunitnumber"/>
 *     &lt;enumeration value="reststrength"/>
 *     &lt;enumeration value="orphandrugdeliveryplace"/>
 *     &lt;enumeration value="orphandrugdeliveryid"/>
 *     &lt;enumeration value="coveragetype"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-ITEM-MAAvalues")
@XmlEnum
public enum CDITEMMAAvalues {

    @XmlEnumValue("agreementtype")
    AGREEMENTTYPE("agreementtype"),
    @XmlEnumValue("requesttype")
    REQUESTTYPE("requesttype"),
    @XmlEnumValue("responsetype")
    RESPONSETYPE("responsetype"),
    @XmlEnumValue("agreementstartdate")
    AGREEMENTSTARTDATE("agreementstartdate"),
    @XmlEnumValue("agreementenddate")
    AGREEMENTENDDATE("agreementenddate"),
    @XmlEnumValue("consultationstartdate")
    CONSULTATIONSTARTDATE("consultationstartdate"),
    @XmlEnumValue("consultationenddate")
    CONSULTATIONENDDATE("consultationenddate"),
    @XmlEnumValue("careproviderreference")
    CAREPROVIDERREFERENCE("careproviderreference"),
    @XmlEnumValue("iorequestreference")
    IOREQUESTREFERENCE("iorequestreference"),
    @XmlEnumValue("decisionreference")
    DECISIONREFERENCE("decisionreference"),
    @XmlEnumValue("refusaljustification")
    REFUSALJUSTIFICATION("refusaljustification"),
    @XmlEnumValue("chapter4reference")
    CHAPTER_4_REFERENCE("chapter4reference"),
    @XmlEnumValue("chapter4annexreference")
    CHAPTER_4_ANNEXREFERENCE("chapter4annexreference"),
    @XmlEnumValue("unitnumber")
    UNITNUMBER("unitnumber"),
    @XmlEnumValue("strength")
    STRENGTH("strength"),
    @XmlEnumValue("restunitnumber")
    RESTUNITNUMBER("restunitnumber"),
    @XmlEnumValue("reststrength")
    RESTSTRENGTH("reststrength"),
    @XmlEnumValue("orphandrugdeliveryplace")
    ORPHANDRUGDELIVERYPLACE("orphandrugdeliveryplace"),
    @XmlEnumValue("orphandrugdeliveryid")
    ORPHANDRUGDELIVERYID("orphandrugdeliveryid"),
    @XmlEnumValue("coveragetype")
    COVERAGETYPE("coveragetype");
    private final String value;

    CDITEMMAAvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDITEMMAAvalues fromValue(String v) {
        for (CDITEMMAAvalues c: CDITEMMAAvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
