//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.06.14 at 03:50:09 PM CEST 
//


package org.taktik.icure.be.ehealth.dto.kmehr.v20170601.be.fgov.ehealth.standards.kmehr.cd.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CD-INCAPACITYREASONvalues.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CD-INCAPACITYREASONvalues">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="sickness"/>
 *     &lt;enumeration value="accident"/>
 *     &lt;enumeration value="family"/>
 *     &lt;enumeration value="other"/>
 *     &lt;enumeration value="careencounter"/>
 *     &lt;enumeration value="illness"/>
 *     &lt;enumeration value="hospitalisation"/>
 *     &lt;enumeration value="pregnancy"/>
 *     &lt;enumeration value="workaccident"/>
 *     &lt;enumeration value="occupationaldisease"/>
 *     &lt;enumeration value="traveltofromworkaccident"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CD-INCAPACITYREASONvalues")
@XmlEnum
public enum CDINCAPACITYREASONvalues {

    @XmlEnumValue("sickness")
    SICKNESS("sickness"),
    @XmlEnumValue("accident")
    ACCIDENT("accident"),
    @XmlEnumValue("family")
    FAMILY("family"),
    @XmlEnumValue("other")
    OTHER("other"),
    @XmlEnumValue("careencounter")
    CAREENCOUNTER("careencounter"),
    @XmlEnumValue("illness")
    ILLNESS("illness"),
    @XmlEnumValue("hospitalisation")
    HOSPITALISATION("hospitalisation"),
    @XmlEnumValue("pregnancy")
    PREGNANCY("pregnancy"),
    @XmlEnumValue("workaccident")
    WORKACCIDENT("workaccident"),
    @XmlEnumValue("occupationaldisease")
    OCCUPATIONALDISEASE("occupationaldisease"),
    @XmlEnumValue("traveltofromworkaccident")
    TRAVELTOFROMWORKACCIDENT("traveltofromworkaccident");
    private final String value;

    CDINCAPACITYREASONvalues(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CDINCAPACITYREASONvalues fromValue(String v) {
        for (CDINCAPACITYREASONvalues c: CDINCAPACITYREASONvalues.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
