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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.entities.embed.InsuranceParameter;
import org.taktik.icure.logic.InsuranceLogic;

public class BelgianMedicalHouseInvoicingFormat {
	private InsuranceLogic insuranceLogic;
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    int recordNumber;
    Writer w;

    public BelgianMedicalHouseInvoicingFormat(Writer w) {
        super();
        this.w = w;
    }

	private String getInsurabilityParameters(Patient patient, InsuranceParameter parameter) {
		if (patient.getInsurabilities() != null && patient.getInsurabilities().size()>0) {
			Map<String, String> parameters = patient.getInsurabilities().get(0).getParameters();
			return parameters.get(parameter.name());
		}
		return null;
	}

	private String getInsuranceCode(Patient patient) {
		if (patient.getInsurabilities() != null && patient.getInsurabilities().size()>0) {
			Insurance insurance = insuranceLogic.getInsurance(patient.getInsurabilities().get(0).getInsuranceId());
			return insurance == null ? null : insurance.getCode();
		}
		return null;
	}


	public void writeFileHeader(InvoiceSender is,
                                Long fileVersion, Long sendingNumber, Integer invoicingYear, Integer invoicingMonth,
                                String invoiceRef) throws IOException {
        recordNumber = 1;

        WriterSession ws = new WriterSession(w);

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert(formattedCreationDate.length()==8);

        ws.registerField("EnregistrementType",10,"N",2);
        ws.registerField("NumeroOrdreEnregistrement",recordNumber,"N",6);
        ws.registerField("NombreNumerosComptesFinanciers",0,"N",1);
        ws.registerField("VersionFichier",fileVersion,"N",7);
        ws.registerField("NumeroCompteFinancierAPartie1et2",is.getIbanNumber().substring(is.getIbanNumber().length()-12),"N",12);
        ws.registerField("Reserve",null,"N",4);
        ws.registerField("NumeroDeLenvoi",sendingNumber,"N",3);
        ws.registerField("NumeroCompteFinancierB",0,"N",12);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("CodeSuppressionFacturePapier",0,"N",1);
        ws.registerField("CodeFichierDeDecompte",0,"N",1);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("ContenuDeLaFacturation",000,"N",3);
        ws.registerField("NumeroTiersPayant",is.getInamiNumber(),"N",12);
        ws.registerField("NumeroDaccreditationCin",0,"N",12);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("Reserve",null,"N",4);
        ws.registerField("Reserve",null,"N",3);
        ws.registerField("Reserve",null,"N",12);
        ws.registerField("Reserve",null,"N",7);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("AnneeDeFacturation",invoicingYear,"N",5);
        ws.registerField("MoisDeFacturation",invoicingMonth,"N",2);
        ws.registerField("Reserve",null,"N",5);
        ws.registerField("DateDeCreationPartie1et2",formattedCreationDate,"N",8);
        ws.registerField("Reserve",null,"N",10);
        ws.registerField("ReferenceDeLetablissement",invoicingYear*100+invoicingMonth,"A",25);
        ws.registerField("Reserve",null,"N",2);
        ws.registerField("Reserve",null,"N",2);
        ws.registerField("BicCompteFinancierAPartie1_2_3et4",is.getBicNumber(),"A",11);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("IbanCompteFinancierAPartie1_2_3_4_5et6",is.getIbanNumber(),"A",34);
        ws.registerField("Reserve",null,"N",6);
        ws.registerField("BicCompteFinancierB",null,"A",11);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("Reserve",null,"N",4);
        ws.registerField("Reserve",null,"N",26);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("Reserve",null,"N",8);
        ws.registerField("Reserve",null,"N",1);
        ws.registerField("IbanCompteFinancierBPartie1_2_3et4",null,"A",34);
        ws.registerField("Reserve",null,"N",8);
        ws.registerField("Reserve",null,"N",8);
        ws.registerField("Reserve",null,"N",8);
        ws.registerField("Reserve",null,"N",4);
        ws.registerField("Reserve",null,"N",4);
        ws.registerField("Reserve",null,"N",4);
        ws.registerField("Reserve",null,"N",6);
        ws.registerField("Reserve",null,"N",2);

        ws.writeFieldsWithCheckSum();
    }

    public void writeRecordHeader(InvoiceSender is,
                                  Long fileVersion, Long invoiceNumber, Integer invoicingYear, Integer invoicingMonth,
                                  String invoiceRef, Patient patient, Date patientDateInscription) throws IOException {
        WriterSession ws = new WriterSession(w);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert(formattedCreationDate.length()==8);

		String tc1String = getInsurabilityParameters(patient, InsuranceParameter.tc1);
		Integer ct1 = tc1String != null ? Integer.valueOf(tc1String) : 0;
		String tc2String = getInsurabilityParameters(patient, InsuranceParameter.tc2);
		Integer ct2 = tc2String != null? Integer.valueOf(tc2String) : 0;
        String noSIS = patient.getSsin() != null ? patient.getSsin():"";
        noSIS = noSIS.replaceAll("[^0-9]", "");

        ws.registerField("EnregistrementDeType20",20,"N",2);
        ws.registerField("NumeroDordreDeLenregistrement",recordNumber,"N",6);
        ws.registerField("AutorisationTiersPayant",0,"N",1);
        ws.registerField("HeureDadmission",0,"N",7);
        ws.registerField("DateDadmission",0,"N",8);
        ws.registerField("DateDeSortiePartie1",0,"N",4);
        ws.registerField("DateDeSortiePartie2",0,"N",4);
        ws.registerField("NumeroMutualiteDaffiliation",getInsuranceCode(patient).replaceAll("[^0-9]", ""),"N",3);
        ws.registerField("IdentificationBeneficiairePartie1et2",noSIS,"N",13);
        ws.registerField("SexeBeneficiaire",patient.getGender()==null||patient.getGender().equals(Gender.male)?1:2,"N",1);
        ws.registerField("TypeFacture",3,"N",1);
        ws.registerField("TypeDeFacturation",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Service721Bis",0,"N",3);
        ws.registerField("NumeroDeLetablissementQuiFacture",is.getInamiNumber(),"N",12);
        ws.registerField("EtablissementDeSejour",0,"N",12);
        ws.registerField("CodeLeveeDelaiDePrescription",0,"N",1);
        ws.registerField("CausesDuTraitement",0,"N",4);
        ws.registerField("NumeroMutualiteDeDestination",getInsuranceCode(patient),"N",3);
        ws.registerField("NumeroDadmission",0,"N",12);
        ws.registerField("DateAccordTraitementPartie1et2",df.format(patientDateInscription),"N",8);
        ws.registerField("HeureDeSortie",0,"N",5);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("NumeroDeLaFactureIndividuellePartie1et2",invoiceNumber,"N",12);
        ws.registerField("ApplicationFranchiseSociale",0,"N",1);
        ws.registerField("Ct1Ct2",ct1*1000+ct2,"N",10);
        ws.registerField("ReferenceDeLetablissement",invoiceRef,"A",25);
        ws.registerField("NumeroDeFacturePrecedentePartie1_2et3",0,"N",12);
        ws.registerField("FlagIdentificationDuBeneficiaire",1,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("NumeroEnvoiPrecedentPartie1_2et3",0,"N",3);
        ws.registerField("NumeroMutualiteFacturationPrecedente",0,"N",3);
        ws.registerField("ReferenceMutualiteNumeroDeCompteFinancierAPartie1et2",null,"A",22);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("AnneeEtMoisDeFacturationPrecedente",0,"N",6);
        ws.registerField("DonneesDeReferenceReseauOuCarteSisPartie1_2_3_4et5",0,"A",48);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("ReferenceMutualiteNumeroCompteFinancierBPartie1_2et3",null,"A",22);
        ws.registerField("Reserve",0,"N",12);
        ws.registerField("DateDebutAssurabilite",0,"N",8);
        ws.registerField("DateFinAssurabilite",0,"N",8);
        ws.registerField("DateCommunicationInformation",0,"N",8);
        ws.registerField("MafAnneeEnCours",0,"N",4);
        ws.registerField("MafAnneeEnCours1",0,"N",4);
        ws.registerField("MafAnneeEnCours2",0,"N",4);
        ws.registerField("Reserve",0,"N",6);
        ws.registerField("Reserve",0,"N",2);

        ws.writeFieldsWithCheckSum();
    }
    public void writeRecordContent(InvoiceSender is,
                                   Long fileVersion, Long invoiceNumber, Integer invoicingYear, Integer invoicingMonth,
                                   String invoiceRef, Long dispenserCode, Patient patient, long codeNomenclature, long amount) throws IOException {
        WriterSession ws = new WriterSession(w);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert(formattedCreationDate.length()==8);

        NumberFormat nf = new DecimalFormat("00000000000");

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, invoicingYear);
        c.set(Calendar.MONTH, invoicingMonth-1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);

        int lastDay = c.get(Calendar.DAY_OF_MONTH);
        String noSIS = patient.getSsin() != null ? patient.getSsin():"";
        noSIS = noSIS.replaceAll("[^0-9]", "");

        ws.registerField("EnregistrementDeType50",50,"N",2);
        ws.registerField("NumeroDordreDeLenregistrement",recordNumber,"N",6);
        ws.registerField("NormePrestationPourcentage",0,"N",1);
        ws.registerField("CodeNomenclatureOuPseudoCodeNomenclature",codeNomenclature,"N",7);
        ws.registerField("DatePremierePrestationEffectuee",invoicingYear*10000+invoicingMonth*100+01,"N",8);
        ws.registerField("DateDernierePrestationEffectueePartie1et2",invoicingYear*10000+invoicingMonth*100+lastDay,"N",8);
        ws.registerField("NumeroMutualiteDaffiliation",getInsuranceCode(patient),"N",3);
        ws.registerField("IdentificationBeneficiairePartie1et2",noSIS,"N",13);
        ws.registerField("SexeBeneficiaire",patient.getGender()==null||patient.getGender().equals("M")?1:2,"N",1);
        ws.registerField("Accouchement",0,"N",1);
        ws.registerField("ReferenceNumeroDeCompteFinancier",0,"N",1);
        ws.registerField("NuitWeekEndJourFerie",0,"N",1);
        ws.registerField("CodeService",990,"N",3);
        ws.registerField("LieuDePrestation",is.getInamiNumber(),"N",12);
        ws.registerField("IdentificationDuDispensateur",/*dispenserCode*/0,"N",12);
        ws.registerField("NormeDispensateur",0,"N",1);
        ws.registerField("PrestationRelativePartie1et2",0,"N",7);
        ws.registerField("SigneMontantInterventionDeLassurance","+"+nf.format(amount),"A",12);
        ws.registerField("DatePrescriptionPartie1et2",0,"N",8);
        ws.registerField("SigneNombreDunites","+0001","A",5);
        ws.registerField("NombreDeCoupes",0,"N",2);
        ws.registerField("IdentificationPrescripteurPartie1et2",0,"N",12);
        ws.registerField("NormePrescripteur",0,"N",1);
        ws.registerField("SigneInterventionPersonnellePatient","+000000000","A",10);
        ws.registerField("ReferenceDeLetablissement",invoiceRef,"A",25);
        ws.registerField("DentTraitee",0,"N",2);
        ws.registerField("SigneMontantSupplementPartie1et2","+000000000","A",10);
        ws.registerField("ExceptionTiersPayant",0,"N",1);
        ws.registerField("CodeFacturationInterventionPersonnelleOuSupplement",0,"N",1);
        ws.registerField("MembreTraite",0,"N",1);
        ws.registerField("PrestataireConventionne",0,"N",1);
        ws.registerField("HeureDePrestationPartie1et2",0,"N",4);
        ws.registerField("IdentificationAdministrateurDuSang",0,"N",12);
        ws.registerField("NumeroDeLattestationDadministrationPartie1et2",0,"N",12);
        ws.registerField("NumeroBonDeDelivranceOuSacPartie1et2",null,"A",12);
        ws.registerField("CodeImplantPartie1",0,"N",12);
        ws.registerField("LibelleDuProduitPartie1et2",null,"A",30);
        ws.registerField("NormePlafond",0,"N",1);
        ws.registerField("DateAccordPrestation",0,"N",8);
        ws.registerField("Transplantation",0,"N",1);
        ws.registerField("IdentificationDeLaideSoignantZoneReservee",0,"N",12);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("SiteHospitalier",0,"N",6);
        ws.registerField("IdentificationAssociationBassinDeSoins",0,"N",12);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("CodeNotificationImplantPartie1et2",0,"N",12);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",6);
        ws.registerField("Reserve",0,"N",2);

        ws.writeFieldsWithCheckSum();
    }
    public void writeRecordFooter( InvoiceSender is,
                                   Long fileVersion, Long invoiceNumber, Integer invoicingYear, Integer invoicingMonth,
                                   String invoiceRef, Patient patient, List<Long> codesNomenclature, long amount) throws IOException {
        WriterSession ws = new WriterSession(w);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert(formattedCreationDate.length()==8);

        String noSIS = patient.getSsin() != null ? patient.getSsin():"";
        noSIS = noSIS.replaceAll("[^0-9]", "");

        NumberFormat nf = new DecimalFormat("00000000000");

        ws.registerField("EnregistrementDeType80",80,"N",2);
        ws.registerField("NumeroDordreDeLenregistrement",recordNumber,"N",6);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("HeureDadmission",0,"N",7);
        ws.registerField("DateDadmission",0,"N",8);
        ws.registerField("DateDeSortiePartie1et2",0,"N",8);
        ws.registerField("NumeroMutualiteDaffiliation",getInsuranceCode(patient).replaceAll("[^0-9]", ""),"N",3);
        ws.registerField("IdentificationBeneficiairePartie1",noSIS,"N",13);
        ws.registerField("SexeBeneficiaire",patient.getGender().equals(Gender.male)?1:2,"N",1);
        ws.registerField("TypeFacture",3,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Service721Bis",0,"N",3);
        ws.registerField("NumeroDeLetablissementQuiFacture",is.getInamiNumber(),"N",12);
        ws.registerField("SigneMontantDeCompteFinancierB","+00000000000","A",12);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("CausesDuTraitement",0,"N",4);
        ws.registerField("NumeroMutualiteDeDestination",getInsuranceCode(patient),"N",3);
        ws.registerField("SigneMontantDeCompteFinancierA","+"+nf.format(amount),"A",12);
        ws.registerField("DateDeLaFacturePartie1et2",formattedCreationDate,"N",8);
        ws.registerField("HeureDeSortie",0,"N",5);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("NumeroDeLaFactureIndividuellePartie1et2",invoiceNumber,"N",12);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("SigneInterventionPersonnellePatient","+000000000","A",10);
        ws.registerField("ReferenceDeLetablissement",invoiceRef,"A",25);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("SigneMontantSupplementPartie1et2","+000000000","A",10);
        ws.registerField("FlagIdentificationDuBeneficiaire",1,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",3);
        ws.registerField("SigneAcompteNumeroCompteFinancierA","+00000000000","A",12);
        ws.registerField("Reserve",0,"N",10);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("Reserve",0,"N",6);
        ws.registerField("Reserve",0,"N",6);
        ws.registerField("Reserve",0,"N",11);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",26);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",12);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",6);
        ws.registerField("Reserve",0,"N",12);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",6);

        BigInteger cs = BigInteger.ZERO;
        for (long val:codesNomenclature) {
            cs = cs.add(BigInteger.valueOf(val));
        }
        long modulo = cs.mod(BigInteger.valueOf(97)).longValue();
        ws.registerField("ChiffresDeControleDeLaFacture",modulo==0?97:modulo,"N",2);

        ws.writeFieldsWithCheckSum();
    }
    public void writeFileFooter(InvoiceSender is,
                                Long fileVersion, Long sendingNumber, Integer invoicingYear, Integer invoicingMonth,
                                String invoiceRef, List<Long> codesNomenclature, Long amount) throws IOException {
        WriterSession ws = new WriterSession(w);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert(formattedCreationDate.length()==8);
        NumberFormat nf = new DecimalFormat("00000000000");

        ws.registerField("EnregistrementDeType90",90,"N",2);
        ws.registerField("NumeroDordreDeLenregistrement",recordNumber,"N",6);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",7);
        ws.registerField("NumeroCompteFinancierAPartie1et2",is.getIbanNumber().substring(is.getIbanNumber().length()-12),"N",12);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("NumeroDenvoi",sendingNumber,"N",3);
        ws.registerField("NumeroCompteFinancierB",0,"N",12);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",3);
        ws.registerField("NumeroTiersPayant",is.getInamiNumber(),"N",12);
        ws.registerField("SigneMontantTotalNumeroCompteFinancierB","+00000000000","A",12);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",3);
        ws.registerField("SigneMontantTotalNumeroCompteFinancierA","+"+nf.format(amount),"A",12);
        ws.registerField("Reserve",0,"N",7);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("AnneeDeFacturation",invoicingYear,"N",5);
        ws.registerField("MoisDeFacturation",invoicingMonth,"N",2);
        ws.registerField("Reserve",0,"N",5);
        ws.registerField("Reserve",0,"N",7);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",10);
        ws.registerField("ReferenceDeLetablissement",invoicingYear*100+invoicingMonth,"A",25);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("Reserve",0,"N",2);
        ws.registerField("BicCompteFinancierAPartie1_2_3et4",is.getBicNumber(),"A",11);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("IbanCompteFinancierAPartie1_2_3_4_5et6",is.getIbanNumber(),"A",34);
        ws.registerField("Reserve",0,"N",6);
        ws.registerField("BicCompteFinancierB",null,"A",11);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",26);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",1);
        ws.registerField("IbanCompteFinancierBPartie1_2_3et4",null,"A",34);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",8);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",4);
        ws.registerField("Reserve",0,"N",6);

        BigInteger cs = BigInteger.ZERO;
        for (long val:codesNomenclature) {
            cs = cs.add(BigInteger.valueOf(val));
        }
        long modulo = cs.mod(BigInteger.valueOf(97)).longValue();
        ws.registerField("ChiffresDeControleDeLenvoi",modulo==0?97:modulo,"N",2);

        ws.writeFieldsWithCheckSum();
    }

		@Autowired
	public void setInsuranceLogic(InsuranceLogic insuranceLogic) {
		this.insuranceLogic = insuranceLogic;
	}
}
