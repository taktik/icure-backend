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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.taktik.icure.db.StringUtils;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.embed.Gender;
import org.taktik.icure.entities.embed.InsuranceParameter;
import org.taktik.icure.logic.InsuranceLogic;

public class BelgianInsuranceInvoicingFormat {
    private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private int recordNumber;
    private Writer writer;

	private InsuranceLogic insuranceLogic;

    public BelgianInsuranceInvoicingFormat(Writer writer, InsuranceLogic insuranceLogic) {
        this.writer = writer;
		this.insuranceLogic = insuranceLogic;
	}

	private String getInsurabilityParameters(Patient patient, InsuranceParameter parameter) {
		if (patient.getInsurabilities() != null && patient.getInsurabilities().size()>0) {
			Map<String, String> parameters = patient.getInsurabilities().get(0).getParameters();
			return parameters.get(parameter.name());
		}
		return null;
	}

	public String getDestCode(String affCode, InvoiceSender invoiceSender) {
        String firstCode = affCode.substring(0, 3).replaceAll("[^0-9]", "");
        if (affCode.startsWith("3")) {
			return Arrays.asList("305", "315", "317", "319", "323", "325").contains(firstCode) ? (invoiceSender.isSpecialist() ? "317" : "319") : firstCode;
		} else if (affCode.startsWith("4")) {
			return "400";
		}
		return firstCode;
	}
    //022464328
    public void write920000(InvoiceSender sender, Long numericalRef, String batchRef, int fileVersion, Long sendingNumber, Integer invoicingYear, Integer invoicingMonth, boolean isTest) throws IOException {
        WriterSession ws = new WriterSession(writer);

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert (formattedCreationDate.length() == 8);

        ws.registerField("Type", 920000, "N", 6);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("N version format message premire version 01", 1, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Type message 12 prod/92 test", fileVersion, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Statut message = code erreur si erreur !! IN - OUT !!", 0, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Rfrence numrique message prestataire", numericalRef, "N", 14);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Reference message OA", 0, "N", 14);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("reserve", 0, "N", 15);
        ws.registerField("Lien T10 Z22&23 Anne et mois facturation", invoicingYear * 100 + invoicingMonth, "N", 6);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T10 Z7 Numro d'envoi", sendingNumber, "N", 3);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T10 Z25 Date cration facture", formattedCreationDate, "N", 8);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Rfrence facture", batchRef, "A", 13);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T10 Z4 Numro version instructions 0001999 production / 9991999 test", isTest?9991999:1999, "N", 7);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Nom personne contact", StringUtils.removeDiacriticalMarks(sender.lastName), "A", 45);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Prnom personne de contact", StringUtils.removeDiacriticalMarks(sender.firstName), "A", 24);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Numro telephone personne contact", sender.phoneNumber, "N", 10);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Type de facture : 01 hospit / 03 ambulatoire / 09 mixte", 3, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Type facturation : 01 1 fichier 1 compte / 02 1 fichier 2 comptes", 1, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("reserve", 0, "N", 20);

        ws.writeFieldsWithoutCheckSum();
    }

    public void write950000(String oa, Long numericalRef, long recordsCount, List<Long> codesNomenclature, long amount) throws IOException {
        WriterSession ws = new WriterSession(writer);

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert (formattedCreationDate.length() == 8);

        BigInteger cs = BigInteger.ZERO;
        for (long val : codesNomenclature) {
            cs = cs.add(BigInteger.valueOf(val));
        }
        long modulo = cs.mod(BigInteger.valueOf(97)).longValue();
        modulo = modulo == 0 ? 97 : modulo;

        ws.registerField("Type", 95, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T10 N mutualit", oa, "N", 3);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("n facture recapitulative", numericalRef*100 + Long.valueOf(oa.substring(0,1)+oa.substring(oa.length()-1)), "N", 12);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Signe montant demand compte A", (amount>=0?"+":"-"), "A", 1);
        ws.registerField("Montant demand compte A", Math.abs(amount), "N", 11);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Signe montant demand compte B", "+", "A", 1);
        ws.registerField("Montant demand compte B", 0, "N", 11);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Signe Montant demand A + B", (amount>=0?"+":"-"), "A", 1);
        ws.registerField("Montant demand compte A + B = lien Cpt A", Math.abs(amount), "N", 11);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Nombre d'enregistrement", recordsCount, "N", 8);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T80 Z98 N contrle par mutuelle", modulo, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Reserve", "", "A", 271);

        ws.writeFieldsWithoutCheckSum();
    }

    public void write960000(String oa, long recordsCount, List<Long> codesNomenclature, long amount) throws IOException {
        WriterSession ws = new WriterSession(writer);

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert (formattedCreationDate.length() == 8);

        BigInteger cs = BigInteger.ZERO;
        for (long val : codesNomenclature) {
            cs = cs.add(BigInteger.valueOf(val));
        }
        long modulo = cs.mod(BigInteger.valueOf(97)).longValue();
        modulo = modulo == 0 ? 97 : modulo;

        ws.registerField("Type", 96, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T10 N mutualit", oa.substring(0,1)+"99", "N", 3);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("n facture recapitulative", 0, "N", 12);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Signe montant demand compte A", (amount>=0?"+":"-"), "A", 1);
        ws.registerField("Montant demand compte A", Math.abs(amount), "N", 11);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Signe montant demand compte B", "+", "A", 1);
        ws.registerField("Montant demand compte B", 0, "N", 11);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Signe Montant semand A + B", (amount>=0?"+":"-"), "A", 1);
        ws.registerField("Montant demand compte A + B = lien Cpt A", Math.abs(amount), "N", 11);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Nombre d'enregistrement", recordsCount, "N", 8);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Lien T80 Z98 N contrle par mutuelle", modulo, "N", 2);
        ws.registerField("Code erreur", 0, "N", 2);
        ws.registerField("Reserve", "", "A", 271);

        ws.writeFieldsWithoutCheckSum();
    }

    public void writeFileHeader(InvoiceSender is, Long fileVersion, Long sendingNumber, Integer invoicingYear, Integer invoicingMonth, String batchRef) throws IOException {
        recordNumber = 1;

        WriterSession ws = new WriterSession(writer);

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert (formattedCreationDate.length() == 8);

        ws.registerField("EnregistrementType", 10, "N", 2);
        ws.registerField("NumeroOrdreEnregistrement", recordNumber, "N", 6);
        ws.registerField("NombreNumerosComptesFinanciers", 0, "N", 1);
        ws.registerField("VersionFichier", fileVersion, "N", 7);
        ws.registerField("NumeroCompteFinancierAPartie1et2", 0, "N", 12);
        ws.registerField("Reserve", null, "N", 4);
        ws.registerField("NumeroDeLenvoi", sendingNumber, "N", 3);
        ws.registerField("NumeroCompteFinancierB", 0, "N", 12);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("CodeSuppressionFacturePapier", 0, "N", 1);
        ws.registerField("CodeFichierDeDecompte", 0, "N", 1);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("Z13 ? ContenuDeLaFacturation", 40, "N", 3);
        ws.registerField("NumeroTiersPayant", is.getInamiNumber(), "N", 12);
        ws.registerField("NumeroDaccreditationCin", 0, "N", 12);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("Reserve", null, "N", 4);
        ws.registerField("Reserve", null, "N", 3);
        ws.registerField("Reserve", null, "N", 12);
        ws.registerField("Reserve", null, "N", 7);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("AnneeDeFacturation", invoicingYear, "N", 5);
        ws.registerField("MoisDeFacturation", invoicingMonth, "N", 2);
        ws.registerField("Reserve", null, "N", 5);
        ws.registerField("DateDeCreationPartie1et2", formattedCreationDate, "N", 8);
        ws.registerField("BCE", is.bce, "N", 10);
        ws.registerField("ReferenceDeLetablissement", batchRef, "A", 25);
        ws.registerField("Reserve", null, "N", 2);
        ws.registerField("Reserve", null, "N", 2);
        ws.registerField("BicCompteFinancierAPartie1_2_3et4", is.getBicNumber(), "A", 11);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("IbanCompteFinancierAPartie1_2_3_4_5et6", is.getIbanNumber(), "A", 34);
        ws.registerField("Reserve", null, "N", 6);
        ws.registerField("BicCompteFinancierB", null, "A", 11);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("Reserve", null, "N", 4);
        ws.registerField("Reserve", null, "N", 26);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("Reserve", null, "N", 8);
        ws.registerField("Reserve", null, "N", 1);
        ws.registerField("IbanCompteFinancierBPartie1_2_3et4", null, "A", 34);
        ws.registerField("Reserve", null, "N", 8);
        ws.registerField("Reserve", null, "N", 8);
        ws.registerField("Reserve", null, "N", 8);
        ws.registerField("Reserve", null, "N", 4);
        ws.registerField("Reserve", null, "N", 4);
        ws.registerField("Reserve", null, "N", 4);
        ws.registerField("Reserve", null, "N", 6);
        ws.registerField("Reserve", null, "N", 2);

        ws.writeFieldsWithCheckSum();
    }

    public void writeRecordHeader(InvoiceSender is, Long invoiceNumber, InvoicingTreatmentReasonCode treatmentReason,
								  String invoiceRef, Patient patient, String insuranceCode, boolean ignorePrescriptionDate) throws IOException {

		WriterSession ws = new WriterSession(writer);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert (formattedCreationDate.length() == 8);

		String tc1String = getInsurabilityParameters(patient, InsuranceParameter.tc1);
		Integer ct1 = tc1String != null ? Integer.valueOf(tc1String) : 0;
		String tc2String = getInsurabilityParameters(patient, InsuranceParameter.tc2);
		Integer ct2 = tc2String != null? Integer.valueOf(tc2String) : 0;
        String noSIS = patient.getSsin() != null ? patient.getSsin() : "";
        noSIS = noSIS.replaceAll("[^0-9]", "");

        ws.registerField("EnregistrementDeType20", 20, "N", 2);
        ws.registerField("NumeroDordreDeLenregistrement", recordNumber, "N", 6);
        ws.registerField("AutorisationTiersPayant", 0, "N", 1);
        ws.registerField("HeureDadmission", 0, "N", 7);
        ws.registerField("DateDadmission", 0, "N", 8);
        ws.registerField("DateDeSortiePartie1", 0, "N", 4);
        ws.registerField("DateDeSortiePartie2", 0, "N", 4);

        //Silly rules for this field
        String affCode = insuranceCode;

        if (affCode.startsWith("2") || affCode.startsWith("5")) {
            affCode = "000";
        }

        ws.registerField("NumeroMutualiteDaffiliation", affCode, "N", 3);
        ws.registerField("IdentificationBeneficiairePartie1et2", noSIS, "N", 13);
        ws.registerField("SexeBeneficiaire", patient.getGender() == null || patient.getGender().equals(Gender.male) ? 1 : 2, "N", 1);
        ws.registerField("TypeFacture", 3, "N", 1);
        ws.registerField("TypeDeFacturation", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Service721Bis",0, "N", 3);
        ws.registerField("NumeroDeLetablissementQuiFacture", is.getInamiNumber(), "N", 12);
        ws.registerField("EtablissementDeSejour", 0, "N", 12);
        ws.registerField("CodeLeveeDelaiDePrescription", ignorePrescriptionDate ? 1:0, "N", 1);
        ws.registerField("CausesDuTraitement", treatmentReason.getCode(), "N", 4);

        //Silly rules for this field
        String destCode = getDestCode(insuranceCode, is);

        ws.registerField("NumeroMutualiteDeDestination", destCode, "N", 3);
        ws.registerField("NumeroDadmission", 0, "N", 12);
        ws.registerField("DateAccordTraitementPartie1et2", 0, "N", 8);
        ws.registerField("HeureDeSortie", 0, "N", 5);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("NumeroDeLaFactureIndividuellePartie1et2", invoiceNumber, "N", 12);
        ws.registerField("ApplicationFranchiseSociale", 0, "N", 1);
        ws.registerField("Ct1Ct2", ct1 * 1000 + ct2, "N", 10);
        ws.registerField("ReferenceDeLetablissement", invoiceRef, "A", 25);
        ws.registerField("NumeroDeFacturePrecedentePartie1_2et3", 0, "N", 12);
        ws.registerField("FlagIdentificationDuBeneficiaire", 1, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("NumeroEnvoiPrecedentPartie1_2et3", 0, "N", 3);
        ws.registerField("NumeroMutualiteFacturationPrecedente", 0, "N", 3);
        ws.registerField("ReferenceMutualiteNumeroDeCompteFinancierAPartie1et2", null, "A", 22);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("AnneeEtMoisDeFacturationPrecedente", 0, "N", 6);
        ws.registerField("DonneesDeReferenceReseauOuCarteSisPartie1_2_3_4et5", 0, "N", 48);  //Forced to N so that it is padded with 0s
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("ReferenceMutualiteNumeroCompteFinancierBPartie1_2et3", null, "A", 22);
        ws.registerField("Reserve", 0, "N", 12);
        ws.registerField("DateDebutAssurabilite", 0, "N", 8);
        ws.registerField("DateFinAssurabilite", 0, "N", 8);
        ws.registerField("DateCommunicationInformation", 0, "N", 8);
        ws.registerField("MafAnneeEnCours", 0, "N", 4);
        ws.registerField("MafAnneeEnCours1", 0, "N", 4);
        ws.registerField("MafAnneeEnCours2", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 6);
        ws.registerField("Reserve", 0, "N", 2);

        ws.writeFieldsWithCheckSum();
    }

	public void writeRecordContent(InvoiceSender is, Integer invoicingYear, Integer invoicingMonth, String invoiceRef, Patient patient, String insuranceCode, InvoiceItem icd) throws IOException {
		WriterSession ws = new WriterSession(writer);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert (formattedCreationDate.length() == 8);

        NumberFormat nf11 = new DecimalFormat("00000000000");
        NumberFormat nf9 = new DecimalFormat("000000000");
        NumberFormat nf4 = new DecimalFormat("0000");

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, invoicingYear);
        c.set(Calendar.MONTH, invoicingMonth - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);

        String noSIS = patient.getSsin() != null ? patient.getSsin() : "";
        noSIS = noSIS.replaceAll("[^0-9]", "");

        ws.registerField("EnregistrementDeType50", 50, "N", 2);
        ws.registerField("NumeroDordreDeLenregistrement", recordNumber, "N", 6);
        ws.registerField("NormePrestationPourcentage", icd.getPercentNorm() != null ? icd.getPercentNorm().getCode() : InvoicingPercentNorm.None.getCode(), "N", 1);
        ws.registerField("CodeNomenclatureOuPseudoCodeNomenclature", icd.getCodeNomenclature(), "N", 7);
        ws.registerField("DatePremierePrestationEffectuee", df.format(icd.getDateCode()), "N", 8);
        ws.registerField("DateDernierePrestationEffectueePartie1et2", df.format(icd.getDateCode()), "N", 8);
        ws.registerField("NumeroMutualiteDaffiliation", insuranceCode, "N", 3);
        ws.registerField("IdentificationBeneficiairePartie1et2", noSIS, "N", 13);
        ws.registerField("SexeBeneficiaire", patient.getGender() == null || patient.getGender().equals(Gender.male) ? 1 : 2, "N", 1);
        ws.registerField("Accouchement", 0, "N", 1);
        ws.registerField("ReferenceNumeroDeCompteFinancier", 0, "N", 1);
        ws.registerField("NuitWeekEndJourFerie", icd.getTimeOfDay() != null ? icd.getTimeOfDay().getCode() : InvoicingTimeOfDay.Other.getCode(), "N", 1);
        ws.registerField("CodeService", 990, "N", 3);
        ws.registerField("LieuDePrestation", 0, "N", 12);
        ws.registerField("IdentificationDuDispensateur", icd.getDoctorIdentificationNumber(), "N", 12);
        ws.registerField("NormeDispensateur", icd.getGnotionNihii() == null ? 1 : 4, "N", 1);
        ws.registerField("PrestationRelativePartie1et2", icd.getRelatedCode() , "N", 7);

        ws.registerField("SigneMontantInterventionDeLassurance", (icd.getReimbursedAmount()>=0?"+":"-") + nf11.format(Math.abs(icd.getReimbursedAmount())), "A", 12);
        ws.registerField("DatePrescriptionPartie1et2", 0, "N", 8);
        ws.registerField("SigneNombreDunites", "+" + nf4.format(icd.getUnits()), "A", 5);
        ws.registerField("NombreDeCoupes", 0, "N", 2);
        ws.registerField("IdentificationPrescripteurPartie1et2", icd.getPrescriberNihii(), "N", 12); //!!!
        ws.registerField("NormePrescripteur", icd.getPrescriberNorm() != null ? icd.getPrescriberNorm().getCode() : InvoicingPrescriberCode.None.getCode() , "N", 1);
        ws.registerField("Z27SigneInterventionPersonnellePatient", (icd.getPatientFee()>=0?"+":"-") + nf9.format(Math.abs(icd.getPatientFee())), "A", 10);
        ws.registerField("ReferenceDeLetablissement", invoiceRef, "A", 25);
        ws.registerField("DentTraitee", 0, "N", 2);
        ws.registerField("SigneMontantSupplementPartie1et2", (icd.getDoctorSupplement()>=0?"+":"-") + nf9.format(Math.abs(icd.getDoctorSupplement())), "A", 10);
        ws.registerField("ExceptionTiersPayant", icd.getOverride3rdPayerCode()!=null && icd.getOverride3rdPayerCode()>=0?icd.getOverride3rdPayerCode():0, "N", 1);
        ws.registerField("CodeFacturationInterventionPersonnelleOuSupplement", icd.getPersonalInterventionCoveredByThirdPartyCode()!=null && icd.getPersonalInterventionCoveredByThirdPartyCode()>=0? icd.getPersonalInterventionCoveredByThirdPartyCode():0, "N", 1); //MAF Zone 33 todo //Mettre 1 si a charge du medecin
        ws.registerField("MembreTraite", icd.getSideCode() != null ? icd.getSideCode().getCode() : InvoicingSideCode.None.getCode(), "N", 1);
        ws.registerField("PrestataireConventionne", is.getConventionCode(), "N", 1);
        ws.registerField("HeureDePrestationPartie1et2", 0, "N", 4);
        ws.registerField("IdentificationAdministrateurDuSang", 0, "N", 12);
        ws.registerField("NumeroDeLattestationDadministrationPartie1et2", 0, "N", 12);
        ws.registerField("NumeroBonDeDelivranceOuSacPartie1et2", 0, "N", 12);  //Forced to N so that it is padded with 0s
        ws.registerField("CodeImplantPartie1", 0, "N", 12);
        ws.registerField("LibelleDuProduitPartie1et2", null, "A", 30);
        ws.registerField("NormePlafond", 0, "N", 1);
        ws.registerField("DateAccordPrestation", 0, "N", 8);
        ws.registerField("Transplantation", 0, "N", 1);
        ws.registerField("IdentificationPrescripeur", icd.getGnotionNihii(), "N", 12);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("SiteHospitalier", 0, "N", 6);
        ws.registerField("IdentificationAssociationBassinDeSoins", 0, "N", 12);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("CodeNotificationImplantPartie1et2", 0, "N", 12);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 6);
        ws.registerField("Reserve", 0, "N", 2);

        ws.writeFieldsWithCheckSum();
    }

    public void writeInvolvementRecordContent(Integer invoicingYear, Integer invoicingMonth, Patient patient, String insuranceCode, InvoiceItem icd) throws IOException {
	    WriterSession ws = new WriterSession(writer);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert (formattedCreationDate.length() == 8);

        NumberFormat nf3 = new DecimalFormat("000");
        NumberFormat nf11 = new DecimalFormat("00000000000");

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, invoicingYear);
        c.set(Calendar.MONTH, invoicingMonth - 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);

        String noSIS = patient.getSsin() != null ? patient.getSsin() : "";
        noSIS = noSIS.replaceAll("[^0-9]", "");

		String tc1String = getInsurabilityParameters(patient, InsuranceParameter.tc1);
		Integer ct1 = tc1String != null ? Integer.valueOf(tc1String) : 0;
		String tc2String = getInsurabilityParameters(patient, InsuranceParameter.tc2);
		Integer ct2 = tc2String != null? Integer.valueOf(tc2String) : 0;

		ws.registerField("EnregistrementDeType51", 51, "N", 2);
        ws.registerField("NumeroDordreDeLenregistrement", recordNumber, "N", 6);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("CodeNomenclatureOuPseudoCodeNomenclature", icd.getCodeNomenclature(), "N", 7);
        ws.registerField("DatePremierePrestationEffectuee", df.format(icd.getDateCode()), "N", 8);
        ws.registerField("Reserve", 0, "N", 11);
        ws.registerField("IdentificationBeneficiairePartie1et2", noSIS, "N", 13);
        ws.registerField("Reserve", 0, "N", 19);
        ws.registerField("IdentificationDuDispensateur", icd.getDoctorIdentificationNumber(), "N", 12);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("SigneMontantInterventionDeLassurance", (icd.getReimbursedAmount()>=0?"+":"-") + nf11.format(Math.abs(icd.getReimbursedAmount())), "A", 12);
        ws.registerField("Reserve", 0, "N", 28);
        ws.registerField("Cts", "0000" + nf3.format(ct1) + nf3.format(ct2), "A", 10);
        ws.registerField("Reserve", 0, "N", 75);
        ws.registerField("Ref", icd.getInsuranceRef(), "A", 48);
        ws.registerField("Reserve", 0, "N", 60);
        ws.registerField("RefDate", df.format(icd.getInsuranceRefDate()), "N", 8);
        ws.registerField("Reserve", 0, "N", 20);

        ws.writeFieldsWithCheckSum();
    }

	public void writeEid(InvoiceItem icd, Patient patient, InvoiceSender invoiceSender) throws IOException {
		WriterSession ws = new WriterSession(writer);

		recordNumber++;

		EIDItem eidItem = icd.getEidItem();

		NumberFormat nf4 = new DecimalFormat("0000");

		String noSIS = patient.getSsin() != null ? patient.getSsin() : "";
		noSIS = noSIS.replaceAll("[^0-9]", "");

		ws.registerField("Enregistrement de type 52", 52, "N", 2);
		ws.registerField("Numero d'ordre de l'enregistrement", recordNumber, "N", 6);
		ws.registerField("reserve", 0, "N", 1);
		ws.registerField("Code nomenclature", icd.getCodeNomenclature(), "N", 7);
		ws.registerField("Date de prestation",df.format(icd.getDateCode()), "N", 8);
		ws.registerField("Date de lecture document identite electronique", df.format(eidItem.getReadDate()) ,"N", 8);
		ws.registerField("reserve", 0, "N", 3);
		ws.registerField("Numero NISS du patient", noSIS, "N", 13);
		ws.registerField("reserve", 0, "N", 1);
		ws.registerField("Type de support document identite electronique", eidItem.getDeviceType(), "A", 1);
		ws.registerField("Type de lecture document identite electronique", eidItem.getReadType(), "A", 1);
		ws.registerField("Heure de lecture document identite electronique", nf4.format(eidItem.getReadHour()), "N", 4);
		ws.registerField("reserve", 0, "N", 12);
		ws.registerField("Numero INAMI", invoiceSender.getInamiNumber(), "N", 12);
        ws.registerField("EID number", eidItem.getReadvalue(), "A", 15);
        ws.registerField("Numero doc just", 0, "N", 25);
		ws.registerField("reserve", 0, "N", 229);

		ws.writeFieldsWithCheckSum();
	}

    public void writeRecordFooter(InvoiceSender is, Long invoiceNumber,
                                  String invoiceRef, Patient patient, String insuranceCode, List<Long> codesNomenclature, long amount, long fee, long sup) throws IOException {
		WriterSession ws = new WriterSession(writer);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);
        assert (formattedCreationDate.length() == 8);

        String noSIS = patient.getSsin() != null ? patient.getSsin() : "";
        noSIS = noSIS.replaceAll("[^0-9]", "");

        NumberFormat nf11 = new DecimalFormat("00000000000");
        NumberFormat nf9 = new DecimalFormat("000000000");

        ws.registerField("EnregistrementDeType80", 80, "N", 2);
        ws.registerField("NumeroDordreDeLenregistrement", recordNumber, "N", 6);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("HeureDadmission", 0, "N", 7);
        ws.registerField("DateDadmission", 0, "N", 8);
        ws.registerField("DateDeSortiePartie1et2", 0, "N", 8);
        ws.registerField("NumeroMutualiteDaffiliation", insuranceCode, "N", 3);
        ws.registerField("IdentificationBeneficiairePartie1", noSIS, "N", 13);
        ws.registerField("SexeBeneficiaire", patient.getGender().equals(Gender.male) ? 1 : 2, "N", 1);
        ws.registerField("TypeFacture", 3, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Service721Bis", 0, "N", 3);
        ws.registerField("NumeroDeLetablissementQuiFacture", is.getInamiNumber(), "N", 12);
        ws.registerField("SigneMontantDeCompteFinancierB", "+00000000000", "A", 12);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("CausesDuTraitement", 0, "N", 4);

		//Silly rules for this field
		String destCode = getDestCode(insuranceCode, is);

		ws.registerField("NumeroMutualiteDeDestination", destCode, "N", 3);        ws.registerField("SigneMontantDeCompteFinancierA", (amount>=0?"+":"-") + nf11.format(Math.abs(amount)), "A", 12);
        ws.registerField("DateDeLaFacturePartie1et2", formattedCreationDate, "N", 8);
        ws.registerField("HeureDeSortie", 0, "N", 5);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("NumeroDeLaFactureIndividuellePartie1et2", invoiceNumber, "N", 12);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("SigneInterventionPersonnellePatient", (fee>=0?"+":"-") + nf9.format(Math.abs(fee)), "A", 10);
        ws.registerField("ReferenceDeLetablissement", invoiceRef, "A", 25);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("SigneMontantSupplementPartie1et2", (sup>=0?"+":"-") + nf9.format(Math.abs(sup)), "A", 10);
        ws.registerField("FlagIdentificationDuBeneficiaire", 1, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 3);
        ws.registerField("SigneAcompteNumeroCompteFinancierA", "+00000000000", "A", 12);
        ws.registerField("Reserve", 0, "N", 10);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("Reserve", 0, "N", 6);
        ws.registerField("Reserve", 0, "N", 6);
        ws.registerField("Reserve", 0, "N", 11);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 26);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 12);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 6);
        ws.registerField("Reserve", 0, "N", 12);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 6);

        BigInteger cs = BigInteger.ZERO;
        for (long val : codesNomenclature) {
            cs = cs.add(BigInteger.valueOf(val));
        }
        long modulo = cs.mod(BigInteger.valueOf(97)).longValue();
        modulo = modulo == 0 ? 97 : modulo;

        ws.registerField("ChiffresDeControleDeLaFacture", modulo , "N", 2);

        ws.writeFieldsWithCheckSum();
    }

    public void writeFileFooter(InvoiceSender is, Long sendingNumber, Integer invoicingYear, Integer invoicingMonth, List<Long> codesNomenclature, Long amount) throws IOException {
        WriterSession ws = new WriterSession(writer);

        recordNumber++;

        Date creationDate = new Date();
        String formattedCreationDate = df.format(creationDate);

        assert (formattedCreationDate.length() == 8);
        NumberFormat nf = new DecimalFormat("00000000000");

        ws.registerField("EnregistrementDeType90", 90, "N", 2);
        ws.registerField("NumeroDordreDeLenregistrement", recordNumber, "N", 6);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 7);
        ws.registerField("NumeroCompteFinancierAPartie1et2", 0, "N", 12);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("NumeroDenvoi", sendingNumber, "N", 3);
        ws.registerField("NumeroCompteFinancierB", 0, "N", 12);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 3);
        ws.registerField("NumeroTiersPayant", is.getInamiNumber(), "N", 12);
        ws.registerField("SigneMontantTotalNumeroCompteFinancierB", "+00000000000", "A", 12);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 3);
        ws.registerField("SigneMontantTotalNumeroCompteFinancierA", (amount>=0?"+":"-") + nf.format(Math.abs(amount)), "A", 12);
        ws.registerField("Reserve", 0, "N", 7);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("AnneeDeFacturation", invoicingYear, "N", 5);
        ws.registerField("MoisDeFacturation", invoicingMonth, "N", 2);
        ws.registerField("Reserve", 0, "N", 5);
        ws.registerField("Reserve", 0, "N", 7);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("BCE", is.bce, "N", 10);
        ws.registerField("ReferenceDeLetablissement", invoicingYear * 100 + invoicingMonth, "A", 25);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("Reserve", 0, "N", 2);
        ws.registerField("BicCompteFinancierAPartie1_2_3et4", is.getBicNumber(), "A", 11);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("IbanCompteFinancierAPartie1_2_3_4_5et6", is.getIbanNumber(), "A", 34);
        ws.registerField("Reserve", 0, "N", 6);
        ws.registerField("BicCompteFinancierB", null, "A", 11);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 26);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 1);
        ws.registerField("IbanCompteFinancierBPartie1_2_3et4", null, "A", 34);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 8);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 4);
        ws.registerField("Reserve", 0, "N", 6);

        BigInteger cs = BigInteger.ZERO;
        for (long val : codesNomenclature) {
            cs = cs.add(BigInteger.valueOf(val));
        }
        long modulo = cs.mod(BigInteger.valueOf(97)).longValue();
        ws.registerField("ChiffresDeControleDeLenvoi", modulo == 0 ? 97 : modulo, "N", 2);

        ws.writeFieldsWithCheckSum();
    }
}
