//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.14 at 03:49:38 PM CEST 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20180301.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CD-TIMEUNITvalues.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-TIMEUNITvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="a"/>
 *     &lt;enumeration value="mo"/>
 *     &lt;enumeration value="wk"/>
 *     &lt;enumeration value="d"/>
 *     &lt;enumeration value="hr"/>
 *     &lt;enumeration value="min"/>
 *     &lt;enumeration value="s"/>
 *     &lt;enumeration value="ms"/>
 *     &lt;enumeration value="us"/>
 *     &lt;enumeration value="ns"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-TIMEUNITvalues")
@XmlEnum
public enum CDTIMEUNITvalues {

    @XmlEnumValue("a")
    A("a"),
    @XmlEnumValue("mo")
    MO("mo"),
    @XmlEnumValue("wk")
    WK("wk"),
    @XmlEnumValue("d")
    D("d"),
    @XmlEnumValue("hr")
    HR("hr"),
    @XmlEnumValue("min")
    MIN("min"),
    @XmlEnumValue("s")
    S("s"),
    @XmlEnumValue("ms")
    MS("ms"),
    @XmlEnumValue("us")
    US("us"),
    @XmlEnumValue("ns")
    NS("ns");
    private final String value;

    CDTIMEUNITvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDTIMEUNITvalues fromValue(String v) {
        for (CDTIMEUNITvalues c: CDTIMEUNITvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
