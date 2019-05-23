//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2019.05.22 at 08:11:32 PM CEST
//


package org.taktik.icure.be.ehealth.samws.v2.consultation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.taktik.icure.be.ehealth.samws.v2.actual.common.PackAmountType;
import org.taktik.icure.be.ehealth.samws.v2.core.QuantityType;


/**
 * <p>Java class for ConsultAmppType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConsultAmppType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Orphan" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="LeafletLink" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="SpcLink" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="RmaPatientLink" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="RmaProfessionalLink" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="ParallelCircuit" type="{urn:be:fgov:ehealth:samws:v2:actual:common}ParallelCircuitType" minOccurs="0"/>
 *         &lt;element name="ParallelDistributor" type="{urn:be:fgov:ehealth:samws:v2:core}String255Type" minOccurs="0"/>
 *         &lt;element name="PackMultiplier" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="PackAmount" type="{urn:be:fgov:ehealth:samws:v2:actual:common}PackAmountType" minOccurs="0"/>
 *         &lt;element name="PackDisplayValue" type="{urn:be:fgov:ehealth:samws:v2:core}String255Type" minOccurs="0"/>
 *         &lt;element name="AuthorisationNr" type="{urn:be:fgov:ehealth:samws:v2:core}String50Type"/>
 *         &lt;element name="SingleUse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="SpeciallyRegulated" type="{urn:be:fgov:ehealth:samws:v2:actual:common}SpeciallyRegulatedType" minOccurs="0"/>
 *         &lt;element name="AbbreviatedName" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="PrescriptionName" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="Note" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="PosologyNote" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="CrmLink" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultTextType" minOccurs="0"/>
 *         &lt;element name="ExFactoryPrice" type="{urn:be:fgov:ehealth:samws:v2:core}Decimal10d4Type" minOccurs="0"/>
 *         &lt;element name="ReimbursementCode" type="{urn:be:fgov:ehealth:samws:v2:actual:common}ReimbursementCodeType" minOccurs="0"/>
 *         &lt;element name="Atc" type="{urn:be:fgov:ehealth:samws:v2:consultation}AtcClassificationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DeliveryModus" type="{urn:be:fgov:ehealth:samws:v2:consultation}DeliveryModusType"/>
 *         &lt;element name="DeliveryModusSpecification" type="{urn:be:fgov:ehealth:samws:v2:consultation}DeliveryModusSpecificationType" minOccurs="0"/>
 *         &lt;element name="NoGenericPrescriptionReason" type="{urn:be:fgov:ehealth:samws:v2:consultation}NoGenericPrescriptionReasonType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DefinedDailyDose" type="{urn:be:fgov:ehealth:samws:v2:core}QuantityType" minOccurs="0"/>
 *         &lt;element name="DistributorActorNr" type="{urn:be:fgov:ehealth:samws:v2:core}CompanyActorNrType" minOccurs="0"/>
 *         &lt;element name="AmppComponent" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultAmppComponentType" maxOccurs="unbounded"/>
 *         &lt;element name="Commercialization" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultCommercializationType" minOccurs="0"/>
 *         &lt;element name="SupplyProblem" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultSupplyProblemType" minOccurs="0"/>
 *         &lt;element name="DerogationImport" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultDerogationImportType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Dmpp" type="{urn:be:fgov:ehealth:samws:v2:consultation}ConsultDmppType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{urn:be:fgov:ehealth:samws:v2:consultation}validityPeriod"/>
 *       &lt;attribute name="ctiExtended" use="required" type="{urn:be:fgov:ehealth:samws:v2:core}CtiExtendedType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultAmppType", propOrder = {
    "orphan",
    "leafletLink",
    "spcLink",
    "rmaPatientLink",
    "rmaProfessionalLink",
    "parallelCircuit",
    "parallelDistributor",
    "packMultiplier",
    "packAmount",
    "packDisplayValue",
    "authorisationNr",
    "singleUse",
    "speciallyRegulated",
    "abbreviatedName",
    "prescriptionName",
    "note",
    "posologyNote",
    "crmLink",
    "exFactoryPrice",
    "reimbursementCode",
    "atcs",
    "deliveryModus",
    "deliveryModusSpecification",
    "noGenericPrescriptionReasons",
    "definedDailyDose",
    "distributorActorNr",
    "amppComponents",
    "commercialization",
    "supplyProblem",
    "derogationImports",
    "dmpps"
})
public class ConsultAmppType
    implements Serializable
{

    private final static long serialVersionUID = 2L;
    @XmlElement(name = "Orphan")
    protected boolean orphan;
    @XmlElement(name = "LeafletLink")
    protected ConsultTextType leafletLink;
    @XmlElement(name = "SpcLink")
    protected ConsultTextType spcLink;
    @XmlElement(name = "RmaPatientLink")
    protected ConsultTextType rmaPatientLink;
    @XmlElement(name = "RmaProfessionalLink")
    protected ConsultTextType rmaProfessionalLink;
    @XmlElement(name = "ParallelCircuit")
    @XmlSchemaType(name = "integer")
    protected Integer parallelCircuit;
    @XmlElement(name = "ParallelDistributor")
    protected String parallelDistributor;
    @XmlElement(name = "PackMultiplier")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger packMultiplier;
    @XmlElement(name = "PackAmount")
    protected PackAmountType packAmount;
    @XmlElement(name = "PackDisplayValue")
    protected String packDisplayValue;
    @XmlElement(name = "AuthorisationNr", required = true)
    protected String authorisationNr;
    @XmlElement(name = "SingleUse")
    protected Boolean singleUse;
    @XmlElement(name = "SpeciallyRegulated")
    @XmlSchemaType(name = "integer")
    protected Integer speciallyRegulated;
    @XmlElement(name = "AbbreviatedName")
    protected ConsultTextType abbreviatedName;
    @XmlElement(name = "PrescriptionName")
    protected ConsultTextType prescriptionName;
    @XmlElement(name = "Note")
    protected ConsultTextType note;
    @XmlElement(name = "PosologyNote")
    protected ConsultTextType posologyNote;
    @XmlElement(name = "CrmLink")
    protected ConsultTextType crmLink;
    @XmlElement(name = "ExFactoryPrice")
    protected BigDecimal exFactoryPrice;
    @XmlElement(name = "ReimbursementCode")
    @XmlSchemaType(name = "integer")
    protected Integer reimbursementCode;
    @XmlElement(name = "Atc")
    protected List<AtcClassificationType> atcs;
    @XmlElement(name = "DeliveryModus", required = true)
    protected DeliveryModusType deliveryModus;
    @XmlElement(name = "DeliveryModusSpecification")
    protected DeliveryModusSpecificationType deliveryModusSpecification;
    @XmlElement(name = "NoGenericPrescriptionReason")
    protected List<NoGenericPrescriptionReasonType> noGenericPrescriptionReasons;
    @XmlElement(name = "DefinedDailyDose")
    protected QuantityType definedDailyDose;
    @XmlElement(name = "DistributorActorNr")
    protected String distributorActorNr;
    @XmlElement(name = "AmppComponent", required = true)
    protected List<ConsultAmppComponentType> amppComponents;
    @XmlElement(name = "Commercialization")
    protected ConsultCommercializationType commercialization;
    @XmlElement(name = "SupplyProblem")
    protected ConsultSupplyProblemType supplyProblem;
    @XmlElement(name = "DerogationImport")
    protected List<ConsultDerogationImportType> derogationImports;
    @XmlElement(name = "Dmpp")
    protected List<ConsultDmppType> dmpps;
    @XmlAttribute(name = "ctiExtended", required = true)
    protected String ctiExtended;
    @XmlAttribute(name = "StartDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlAttribute(name = "EndDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;

    /**
     * Gets the value of the orphan property.
     *
     */
    public boolean isOrphan() {
        return orphan;
    }

    /**
     * Sets the value of the orphan property.
     *
     */
    public void setOrphan(boolean value) {
        this.orphan = value;
    }

    /**
     * Gets the value of the leafletLink property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getLeafletLink() {
        return leafletLink;
    }

    /**
     * Sets the value of the leafletLink property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setLeafletLink(ConsultTextType value) {
        this.leafletLink = value;
    }

    /**
     * Gets the value of the spcLink property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getSpcLink() {
        return spcLink;
    }

    /**
     * Sets the value of the spcLink property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setSpcLink(ConsultTextType value) {
        this.spcLink = value;
    }

    /**
     * Gets the value of the rmaPatientLink property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getRmaPatientLink() {
        return rmaPatientLink;
    }

    /**
     * Sets the value of the rmaPatientLink property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setRmaPatientLink(ConsultTextType value) {
        this.rmaPatientLink = value;
    }

    /**
     * Gets the value of the rmaProfessionalLink property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getRmaProfessionalLink() {
        return rmaProfessionalLink;
    }

    /**
     * Sets the value of the rmaProfessionalLink property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setRmaProfessionalLink(ConsultTextType value) {
        this.rmaProfessionalLink = value;
    }

    /**
     * Gets the value of the parallelCircuit property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getParallelCircuit() {
        return parallelCircuit;
    }

    /**
     * Sets the value of the parallelCircuit property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setParallelCircuit(Integer value) {
        this.parallelCircuit = value;
    }

    /**
     * Gets the value of the parallelDistributor property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getParallelDistributor() {
        return parallelDistributor;
    }

    /**
     * Sets the value of the parallelDistributor property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setParallelDistributor(String value) {
        this.parallelDistributor = value;
    }

    /**
     * Gets the value of the packMultiplier property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getPackMultiplier() {
        return packMultiplier;
    }

    /**
     * Sets the value of the packMultiplier property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setPackMultiplier(BigInteger value) {
        this.packMultiplier = value;
    }

    /**
     * Gets the value of the packAmount property.
     *
     * @return
     *     possible object is
     *     {@link PackAmountType }
     *
     */
    public PackAmountType getPackAmount() {
        return packAmount;
    }

    /**
     * Sets the value of the packAmount property.
     *
     * @param value
     *     allowed object is
     *     {@link PackAmountType }
     *
     */
    public void setPackAmount(PackAmountType value) {
        this.packAmount = value;
    }

    /**
     * Gets the value of the packDisplayValue property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPackDisplayValue() {
        return packDisplayValue;
    }

    /**
     * Sets the value of the packDisplayValue property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPackDisplayValue(String value) {
        this.packDisplayValue = value;
    }

    /**
     * Gets the value of the authorisationNr property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthorisationNr() {
        return authorisationNr;
    }

    /**
     * Sets the value of the authorisationNr property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthorisationNr(String value) {
        this.authorisationNr = value;
    }

    /**
     * Gets the value of the singleUse property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isSingleUse() {
        return singleUse;
    }

    /**
     * Sets the value of the singleUse property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setSingleUse(Boolean value) {
        this.singleUse = value;
    }

    /**
     * Gets the value of the speciallyRegulated property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getSpeciallyRegulated() {
        return speciallyRegulated;
    }

    /**
     * Sets the value of the speciallyRegulated property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setSpeciallyRegulated(Integer value) {
        this.speciallyRegulated = value;
    }

    /**
     * Gets the value of the abbreviatedName property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getAbbreviatedName() {
        return abbreviatedName;
    }

    /**
     * Sets the value of the abbreviatedName property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setAbbreviatedName(ConsultTextType value) {
        this.abbreviatedName = value;
    }

    /**
     * Gets the value of the prescriptionName property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getPrescriptionName() {
        return prescriptionName;
    }

    /**
     * Sets the value of the prescriptionName property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setPrescriptionName(ConsultTextType value) {
        this.prescriptionName = value;
    }

    /**
     * Gets the value of the note property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setNote(ConsultTextType value) {
        this.note = value;
    }

    /**
     * Gets the value of the posologyNote property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getPosologyNote() {
        return posologyNote;
    }

    /**
     * Sets the value of the posologyNote property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setPosologyNote(ConsultTextType value) {
        this.posologyNote = value;
    }

    /**
     * Gets the value of the crmLink property.
     *
     * @return
     *     possible object is
     *     {@link ConsultTextType }
     *
     */
    public ConsultTextType getCrmLink() {
        return crmLink;
    }

    /**
     * Sets the value of the crmLink property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultTextType }
     *
     */
    public void setCrmLink(ConsultTextType value) {
        this.crmLink = value;
    }

    /**
     * Gets the value of the exFactoryPrice property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getExFactoryPrice() {
        return exFactoryPrice;
    }

    /**
     * Sets the value of the exFactoryPrice property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setExFactoryPrice(BigDecimal value) {
        this.exFactoryPrice = value;
    }

    /**
     * Gets the value of the reimbursementCode property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getReimbursementCode() {
        return reimbursementCode;
    }

    /**
     * Sets the value of the reimbursementCode property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setReimbursementCode(Integer value) {
        this.reimbursementCode = value;
    }

    /**
     * Gets the value of the atcs property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atcs property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtcs().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AtcClassificationType }
     *
     *
     */
    public List<AtcClassificationType> getAtcs() {
        if (atcs == null) {
            atcs = new ArrayList<AtcClassificationType>();
        }
        return this.atcs;
    }

    /**
     * Gets the value of the deliveryModus property.
     *
     * @return
     *     possible object is
     *     {@link DeliveryModusType }
     *
     */
    public DeliveryModusType getDeliveryModus() {
        return deliveryModus;
    }

    /**
     * Sets the value of the deliveryModus property.
     *
     * @param value
     *     allowed object is
     *     {@link DeliveryModusType }
     *
     */
    public void setDeliveryModus(DeliveryModusType value) {
        this.deliveryModus = value;
    }

    /**
     * Gets the value of the deliveryModusSpecification property.
     *
     * @return
     *     possible object is
     *     {@link DeliveryModusSpecificationType }
     *
     */
    public DeliveryModusSpecificationType getDeliveryModusSpecification() {
        return deliveryModusSpecification;
    }

    /**
     * Sets the value of the deliveryModusSpecification property.
     *
     * @param value
     *     allowed object is
     *     {@link DeliveryModusSpecificationType }
     *
     */
    public void setDeliveryModusSpecification(DeliveryModusSpecificationType value) {
        this.deliveryModusSpecification = value;
    }

    /**
     * Gets the value of the noGenericPrescriptionReasons property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noGenericPrescriptionReasons property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoGenericPrescriptionReasons().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NoGenericPrescriptionReasonType }
     *
     *
     */
    public List<NoGenericPrescriptionReasonType> getNoGenericPrescriptionReasons() {
        if (noGenericPrescriptionReasons == null) {
            noGenericPrescriptionReasons = new ArrayList<NoGenericPrescriptionReasonType>();
        }
        return this.noGenericPrescriptionReasons;
    }

    /**
     * Gets the value of the definedDailyDose property.
     *
     * @return
     *     possible object is
     *     {@link QuantityType }
     *
     */
    public QuantityType getDefinedDailyDose() {
        return definedDailyDose;
    }

    /**
     * Sets the value of the definedDailyDose property.
     *
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *
     */
    public void setDefinedDailyDose(QuantityType value) {
        this.definedDailyDose = value;
    }

    /**
     * Gets the value of the distributorActorNr property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDistributorActorNr() {
        return distributorActorNr;
    }

    /**
     * Sets the value of the distributorActorNr property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDistributorActorNr(String value) {
        this.distributorActorNr = value;
    }

    /**
     * Gets the value of the amppComponents property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the amppComponents property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAmppComponents().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConsultAmppComponentType }
     *
     *
     */
    public List<ConsultAmppComponentType> getAmppComponents() {
        if (amppComponents == null) {
            amppComponents = new ArrayList<ConsultAmppComponentType>();
        }
        return this.amppComponents;
    }

    /**
     * Gets the value of the commercialization property.
     *
     * @return
     *     possible object is
     *     {@link ConsultCommercializationType }
     *
     */
    public ConsultCommercializationType getCommercialization() {
        return commercialization;
    }

    /**
     * Sets the value of the commercialization property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultCommercializationType }
     *
     */
    public void setCommercialization(ConsultCommercializationType value) {
        this.commercialization = value;
    }

    /**
     * Gets the value of the supplyProblem property.
     *
     * @return
     *     possible object is
     *     {@link ConsultSupplyProblemType }
     *
     */
    public ConsultSupplyProblemType getSupplyProblem() {
        return supplyProblem;
    }

    /**
     * Sets the value of the supplyProblem property.
     *
     * @param value
     *     allowed object is
     *     {@link ConsultSupplyProblemType }
     *
     */
    public void setSupplyProblem(ConsultSupplyProblemType value) {
        this.supplyProblem = value;
    }

    /**
     * Gets the value of the derogationImports property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the derogationImports property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDerogationImports().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConsultDerogationImportType }
     *
     *
     */
    public List<ConsultDerogationImportType> getDerogationImports() {
        if (derogationImports == null) {
            derogationImports = new ArrayList<ConsultDerogationImportType>();
        }
        return this.derogationImports;
    }

    /**
     * Gets the value of the dmpps property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dmpps property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDmpps().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConsultDmppType }
     *
     *
     */
    public List<ConsultDmppType> getDmpps() {
        if (dmpps == null) {
            dmpps = new ArrayList<ConsultDmppType>();
        }
        return this.dmpps;
    }

    /**
     * Gets the value of the ctiExtended property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCtiExtended() {
        return ctiExtended;
    }

    /**
     * Sets the value of the ctiExtended property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCtiExtended(String value) {
        this.ctiExtended = value;
    }

    /**
     * Gets the value of the startDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

}
