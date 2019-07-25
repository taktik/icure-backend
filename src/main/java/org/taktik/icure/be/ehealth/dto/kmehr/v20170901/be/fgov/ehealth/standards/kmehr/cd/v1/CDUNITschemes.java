//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.14 at 03:49:03 PM CEST 
//


package org.taktik.icure.be.ehealth.dto.kmehr.v20170901.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CD-UNITschemes.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-UNITschemes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CD-UNIT"/>
 *     &lt;enumeration value="CD-CURRENCY"/>
 *     &lt;enumeration value="UCUM"/>
 *     &lt;enumeration value="CD-TIMEUNIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-UNITschemes")
@XmlEnum
public enum CDUNITschemes {

    @XmlEnumValue("CD-UNIT")
    CD_UNIT("CD-UNIT", "1.7"),
    @XmlEnumValue("CD-CURRENCY")
    CD_CURRENCY("CD-CURRENCY", "1.0"),
    UCUM("UCUM", "1.0"),
    @XmlEnumValue("CD-TIMEUNIT")
    CD_TIMEUNIT("CD-TIMEUNIT", "2.1");
    private final String value; //
    private final String version;
    CDUNITschemes(String v, String vs) {
        value = v;
        version = vs;
    }

    public String value() {
        return value;
    } //

    public String version() {
        return version;
    }

    public static CDUNITschemes fromValue(String v) {
        for (CDUNITschemes c: CDUNITschemes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
