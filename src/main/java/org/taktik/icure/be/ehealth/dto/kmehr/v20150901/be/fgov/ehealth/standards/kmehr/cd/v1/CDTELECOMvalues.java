//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.14 at 03:49:25 PM CEST 
//


package org.taktik.icure.be.ehealth.dto.kmehr.v20150901.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CD-TELECOMvalues.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-TELECOMvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="phone"/>
 *     &lt;enumeration value="mobile"/>
 *     &lt;enumeration value="fax"/>
 *     &lt;enumeration value="email"/>
 *     &lt;enumeration value="carenet"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-TELECOMvalues")
@XmlEnum
public enum CDTELECOMvalues {

    @XmlEnumValue("phone")
    PHONE("phone"),
    @XmlEnumValue("mobile")
    MOBILE("mobile"),
    @XmlEnumValue("fax")
    FAX("fax"),
    @XmlEnumValue("email")
    EMAIL("email"),
    @XmlEnumValue("carenet")
    CARENET("carenet");
    private final String value;

    CDTELECOMvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDTELECOMvalues fromValue(String v) {
        for (CDTELECOMvalues c: CDTELECOMvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
