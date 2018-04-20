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
// Généré le : 2015.03.05 à 11:48:01 AM CET 
//


package org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.schema.v1;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.cd.v1.CDMEDICATION;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20120401.be.fgov.ehealth.standards.kmehr.dt.v1.TextType;


/**
 * Deprecated at 01/01/2009, this complex type has been retained for backward compatibility only
 * 
 * <p>Classe Java pour medicationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="medicationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="cd" type="{http://www.ehealth.fgov.be/standards/kmehr/cd/v1}CD-MEDICATION"/>
 *           &lt;element name="inn" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *           &lt;element name="magistral" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType"/>
 *         &lt;/choice>
 *         &lt;element name="tradename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="presentation" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}presentationType" minOccurs="0"/>
 *         &lt;element name="strength" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}strengthType" minOccurs="0"/>
 *         &lt;element name="route" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}routeType" minOccurs="0"/>
 *         &lt;element name="batch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element name="numberofpackage" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *           &lt;element name="package" type="{http://www.ehealth.fgov.be/standards/kmehr/schema/v1}packageType" minOccurs="0"/>
 *           &lt;element name="quantityperpackage" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element name="instructionforoverdosing" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" minOccurs="0"/>
 *         &lt;element name="instructionforpatient" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" minOccurs="0"/>
 *         &lt;element name="instructionforreimbursement" type="{http://www.ehealth.fgov.be/standards/kmehr/dt/v1}textType" minOccurs="0"/>
 *         &lt;element name="issubstitutionallowed" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "medicationType", propOrder = {
    "magistral",
    "inn",
    "cd",
    "tradename",
    "presentation",
    "strength",
    "route",
    "batch",
    "numberofpackage",
    "_package",
    "quantityperpackage",
    "instructionforoverdosing",
    "instructionforpatient",
    "instructionforreimbursement",
    "issubstitutionallowed"
})
public class MedicationType
    implements Serializable
{

    private final static long serialVersionUID = 20120401L;
    protected TextType magistral;
    protected TextType inn;
    protected CDMEDICATION cd;
    protected String tradename;
    protected PresentationType presentation;
    protected StrengthType strength;
    protected RouteType route;
    protected String batch;
    protected BigDecimal numberofpackage;
    @XmlElement(name = "package")
    protected PackageType _package;
    protected BigDecimal quantityperpackage;
    protected TextType instructionforoverdosing;
    protected TextType instructionforpatient;
    protected TextType instructionforreimbursement;
    protected Boolean issubstitutionallowed;

    /**
     * Obtient la valeur de la propriété magistral.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getMagistral() {
        return magistral;
    }

    /**
     * Définit la valeur de la propriété magistral.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setMagistral(TextType value) {
        this.magistral = value;
    }

    /**
     * Obtient la valeur de la propriété inn.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInn() {
        return inn;
    }

    /**
     * Définit la valeur de la propriété inn.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInn(TextType value) {
        this.inn = value;
    }

    /**
     * Obtient la valeur de la propriété cd.
     * 
     * @return
     *     possible object is
     *     {@link CDMEDICATION }
     *     
     */
    public CDMEDICATION getCd() {
        return cd;
    }

    /**
     * Définit la valeur de la propriété cd.
     * 
     * @param value
     *     allowed object is
     *     {@link CDMEDICATION }
     *     
     */
    public void setCd(CDMEDICATION value) {
        this.cd = value;
    }

    /**
     * Obtient la valeur de la propriété tradename.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTradename() {
        return tradename;
    }

    /**
     * Définit la valeur de la propriété tradename.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTradename(String value) {
        this.tradename = value;
    }

    /**
     * Obtient la valeur de la propriété presentation.
     * 
     * @return
     *     possible object is
     *     {@link PresentationType }
     *     
     */
    public PresentationType getPresentation() {
        return presentation;
    }

    /**
     * Définit la valeur de la propriété presentation.
     * 
     * @param value
     *     allowed object is
     *     {@link PresentationType }
     *     
     */
    public void setPresentation(PresentationType value) {
        this.presentation = value;
    }

    /**
     * Obtient la valeur de la propriété strength.
     * 
     * @return
     *     possible object is
     *     {@link StrengthType }
     *     
     */
    public StrengthType getStrength() {
        return strength;
    }

    /**
     * Définit la valeur de la propriété strength.
     * 
     * @param value
     *     allowed object is
     *     {@link StrengthType }
     *     
     */
    public void setStrength(StrengthType value) {
        this.strength = value;
    }

    /**
     * Obtient la valeur de la propriété route.
     * 
     * @return
     *     possible object is
     *     {@link RouteType }
     *     
     */
    public RouteType getRoute() {
        return route;
    }

    /**
     * Définit la valeur de la propriété route.
     * 
     * @param value
     *     allowed object is
     *     {@link RouteType }
     *     
     */
    public void setRoute(RouteType value) {
        this.route = value;
    }

    /**
     * Obtient la valeur de la propriété batch.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatch() {
        return batch;
    }

    /**
     * Définit la valeur de la propriété batch.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatch(String value) {
        this.batch = value;
    }

    /**
     * Obtient la valeur de la propriété numberofpackage.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNumberofpackage() {
        return numberofpackage;
    }

    /**
     * Définit la valeur de la propriété numberofpackage.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNumberofpackage(BigDecimal value) {
        this.numberofpackage = value;
    }

    /**
     * Obtient la valeur de la propriété package.
     * 
     * @return
     *     possible object is
     *     {@link PackageType }
     *     
     */
    public PackageType getPackage() {
        return _package;
    }

    /**
     * Définit la valeur de la propriété package.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageType }
     *     
     */
    public void setPackage(PackageType value) {
        this._package = value;
    }

    /**
     * Obtient la valeur de la propriété quantityperpackage.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuantityperpackage() {
        return quantityperpackage;
    }

    /**
     * Définit la valeur de la propriété quantityperpackage.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuantityperpackage(BigDecimal value) {
        this.quantityperpackage = value;
    }

    /**
     * Obtient la valeur de la propriété instructionforoverdosing.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInstructionforoverdosing() {
        return instructionforoverdosing;
    }

    /**
     * Définit la valeur de la propriété instructionforoverdosing.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInstructionforoverdosing(TextType value) {
        this.instructionforoverdosing = value;
    }

    /**
     * Obtient la valeur de la propriété instructionforpatient.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInstructionforpatient() {
        return instructionforpatient;
    }

    /**
     * Définit la valeur de la propriété instructionforpatient.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInstructionforpatient(TextType value) {
        this.instructionforpatient = value;
    }

    /**
     * Obtient la valeur de la propriété instructionforreimbursement.
     * 
     * @return
     *     possible object is
     *     {@link TextType }
     *     
     */
    public TextType getInstructionforreimbursement() {
        return instructionforreimbursement;
    }

    /**
     * Définit la valeur de la propriété instructionforreimbursement.
     * 
     * @param value
     *     allowed object is
     *     {@link TextType }
     *     
     */
    public void setInstructionforreimbursement(TextType value) {
        this.instructionforreimbursement = value;
    }

    /**
     * Obtient la valeur de la propriété issubstitutionallowed.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIssubstitutionallowed() {
        return issubstitutionallowed;
    }

    /**
     * Définit la valeur de la propriété issubstitutionallowed.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIssubstitutionallowed(Boolean value) {
        this.issubstitutionallowed = value;
    }

}
