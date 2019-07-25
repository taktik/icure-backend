//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.14 at 03:48:40 PM CEST 
//


package org.taktik.icure.be.ehealth.dto.kmehr.v20140401.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CD-CONSENTvalues.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-CONSENTvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="local"/>
 *     &lt;enumeration value="prospective"/>
 *     &lt;enumeration value="retrospective"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-CONSENTvalues")
@XmlEnum
public enum CDCONSENTvalues {

    @XmlEnumValue("local")
    LOCAL("local"),
    @XmlEnumValue("prospective")
    PROSPECTIVE("prospective"),
    @XmlEnumValue("retrospective")
    RETROSPECTIVE("retrospective");
    private final String value;

    CDCONSENTvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDCONSENTvalues fromValue(String v) {
        for (CDCONSENTvalues c: CDCONSENTvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
