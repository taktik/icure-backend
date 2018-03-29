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

package org.taktik.icure.be.ehealth.logic.primoto.domain.be.fgov.riziv.primoto.request;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HealthcareProfessional" type="{http://riziv.fgov.be/Primoto/Request.xsd}NihdiNumberType"/>
 *         &lt;element name="YearConcerned" type="{http://riziv.fgov.be/Primoto/Request.xsd}YearConcernedType"/>
 *         &lt;element name="CreationDate" type="{http://riziv.fgov.be/Primoto/Request.xsd}CreationDateType"/>
 *         &lt;element name="SoftwareName" type="{http://riziv.fgov.be/Primoto/Request.xsd}SoftwareNameType"/>
 *         &lt;element name="SoftwareVersion" type="{http://riziv.fgov.be/Primoto/Request.xsd}SoftwareVersionType"/>
 *         &lt;element name="SoftwareSerialNumber" type="{http://riziv.fgov.be/Primoto/Request.xsd}SoftwareSerialNumberType" minOccurs="0"/>
 *         &lt;element name="SoftwareVendorDeclaration" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="PercentageSumehr" type="{http://riziv.fgov.be/Primoto/Request.xsd}Percentage"/>
 *         &lt;element name="PercentageNewMedicine" type="{http://riziv.fgov.be/Primoto/Request.xsd}Percentage"/>
 *         &lt;element name="AverageNewDiagnostic" type="{http://riziv.fgov.be/Primoto/Request.xsd}Average"/>
 *         &lt;element name="YearlyContactGroup" type="{http://riziv.fgov.be/Primoto/Request.xsd}TotalAmount"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "healthcareProfessional",
    "yearConcerned",
    "creationDate",
    "softwareName",
    "softwareVersion",
    "softwareSerialNumber",
    "softwareVendorDeclaration",
    "percentageSumehr",
    "percentageNewMedicine",
    "averageNewDiagnostic",
    "yearlyContactGroup"
})
@XmlRootElement(name = "PrimotoRequest", namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
public class PrimotoRequest {

    @XmlElement(name = "HealthcareProfessional", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected String healthcareProfessional;
    @XmlElement(name = "YearConcerned", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected BigInteger yearConcerned;
    @XmlElement(name = "CreationDate", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar creationDate;
    @XmlElement(name = "SoftwareName", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected String softwareName;
    @XmlElement(name = "SoftwareVersion", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected String softwareVersion;
    @XmlElement(name = "SoftwareSerialNumber", namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected String softwareSerialNumber;
    @XmlElement(name = "SoftwareVendorDeclaration", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected byte[] softwareVendorDeclaration;
    @XmlElement(name = "PercentageSumehr", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected BigDecimal percentageSumehr;
    @XmlElement(name = "PercentageNewMedicine", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected BigDecimal percentageNewMedicine;
    @XmlElement(name = "AverageNewDiagnostic", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected BigDecimal averageNewDiagnostic;
    @XmlElement(name = "YearlyContactGroup", required = true, namespace = "http://riziv.fgov.be/Primoto/Request.xsd")
    protected BigInteger yearlyContactGroup;

    /**
     * Obtient la valeur de la propriété healthcareProfessional.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHealthcareProfessional() {
        return healthcareProfessional;
    }

    /**
     * Définit la valeur de la propriété healthcareProfessional.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHealthcareProfessional(String value) {
        this.healthcareProfessional = value;
    }

    /**
     * Obtient la valeur de la propriété yearConcerned.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getYearConcerned() {
        return yearConcerned;
    }

    /**
     * Définit la valeur de la propriété yearConcerned.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setYearConcerned(BigInteger value) {
        this.yearConcerned = value;
    }

    /**
     * Obtient la valeur de la propriété creationDate.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Définit la valeur de la propriété creationDate.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Obtient la valeur de la propriété softwareName.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSoftwareName() {
        return softwareName;
    }

    /**
     * Définit la valeur de la propriété softwareName.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSoftwareName(String value) {
        this.softwareName = value;
    }

    /**
     * Obtient la valeur de la propriété softwareVersion.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Définit la valeur de la propriété softwareVersion.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSoftwareVersion(String value) {
        this.softwareVersion = value;
    }

    /**
     * Obtient la valeur de la propriété softwareSerialNumber.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSoftwareSerialNumber() {
        return softwareSerialNumber;
    }

    /**
     * Définit la valeur de la propriété softwareSerialNumber.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSoftwareSerialNumber(String value) {
        this.softwareSerialNumber = value;
    }

    /**
     * Obtient la valeur de la propriété softwareVendorDeclaration.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSoftwareVendorDeclaration() {
        return softwareVendorDeclaration;
    }

    /**
     * Définit la valeur de la propriété softwareVendorDeclaration.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSoftwareVendorDeclaration(byte[] value) {
        this.softwareVendorDeclaration = value;
    }

    /**
     * Obtient la valeur de la propriété percentageSumehr.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getPercentageSumehr() {
        return percentageSumehr;
    }

    /**
     * Définit la valeur de la propriété percentageSumehr.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setPercentageSumehr(BigDecimal value) {
        this.percentageSumehr = value;
    }

    /**
     * Obtient la valeur de la propriété percentageNewMedicine.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getPercentageNewMedicine() {
        return percentageNewMedicine;
    }

    /**
     * Définit la valeur de la propriété percentageNewMedicine.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setPercentageNewMedicine(BigDecimal value) {
        this.percentageNewMedicine = value;
    }

    /**
     * Obtient la valeur de la propriété averageNewDiagnostic.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getAverageNewDiagnostic() {
        return averageNewDiagnostic;
    }

    /**
     * Définit la valeur de la propriété averageNewDiagnostic.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setAverageNewDiagnostic(BigDecimal value) {
        this.averageNewDiagnostic = value;
    }

    /**
     * Obtient la valeur de la propriété yearlyContactGroup.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getYearlyContactGroup() {
        return yearlyContactGroup;
    }

    /**
     * Définit la valeur de la propriété yearlyContactGroup.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setYearlyContactGroup(BigInteger value) {
        this.yearlyContactGroup = value;
    }

}
