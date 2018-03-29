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

import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.Acknowledgment;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.Bordereau95;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.ErrorCount;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.ErrorDetail;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.IdentificationFlux;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceBordereau;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecord;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType10;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType20;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType30;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType50;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType51;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType52;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType80;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.InvoiceRecordType90;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.ZoneDescription;
import org.taktik.icure.utils.UTF8Control;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BelgianInsuranceInvoicingReader {

    private ResourceBundle errorCodesBundle;

    private ResourceBundle messagesBundle;

    private String language;

    public BelgianInsuranceInvoicingReader(String language) {
        this.language = language;
    }

    public String getMessageDescription(String errorCode, String message) {
        try {
            if (messagesBundle == null) {
                messagesBundle = ResourceBundle.getBundle(this.getClass().getPackage().getName()+".messages", new Locale(language), new UTF8Control());
            }
            return messagesBundle.getString(errorCode);
        } catch (Exception e) {
            return message;
        }
    }

    public String getErrorCodeDescription(String errorCode, String message) {
        try {
            if (errorCodesBundle == null) {
                errorCodesBundle = ResourceBundle.getBundle(this.getClass().getPackage().getName()+"efacterrorcodes", new Locale(language), new UTF8Control());
            }
            return errorCodesBundle.getString(errorCode);
        } catch (Exception e) {
            return message;
        }
    }

    public BelgianInsuranceInvoicing read920000(String message) throws IOException {
        ReaderSession reader = new ReaderSession(new StringReader(message));

        BelgianInsuranceInvoicing invoicing = new BelgianInsuranceInvoicing();
        readIdentificationFlux(reader, invoicing);
        readMessageHeader(reader, invoicing);
        readMessageDetails(reader, invoicing);

        return invoicing;
    }

    public BelgianInsuranceInvoicing read920098(String message) throws IOException {
		return read92Generic(message);
    }

	private BelgianInsuranceInvoicing read92Generic(String message) throws IOException {
		ReaderSession reader = new ReaderSession(new StringReader(message));

		BelgianInsuranceInvoicing invoicing = new BelgianInsuranceInvoicing();
		readIdentificationFlux(reader, invoicing);
		readErrorCount(reader, invoicing);
		readErrorDetails(reader, invoicing);

		return invoicing;
	}

	public BelgianInsuranceInvoicing read920099(String message) throws IOException {
		return read92Generic(message);
    }

    public BelgianInsuranceInvoicing read920900(String message) throws IOException {
		return read92Generic(message);
    }

    public BelgianInsuranceInvoicing read920999(String message) throws IOException {
        ReaderSession reader = new ReaderSession(new StringReader(message));

        BelgianInsuranceInvoicing invoicing = new BelgianInsuranceInvoicing();
        readIdentificationFlux(reader, invoicing);
        readInvoiceBordereau(reader, invoicing);

        boolean bordereau95HasBeenRead = readBordereau95(reader, invoicing);
        while (bordereau95HasBeenRead) {
            bordereau95HasBeenRead = readBordereau95(reader, invoicing);
        }

        return invoicing;
    }

    public BelgianInsuranceInvoicing read931000(String message) throws IOException {
        ReaderSession reader = new ReaderSession(new StringReader(message));

        BelgianInsuranceInvoicing invoicing = new BelgianInsuranceInvoicing();
        readIdentificationFlux(reader, invoicing);
        readAcknowledgment(reader, invoicing);

        return invoicing;
    }

    // TODO move to InvoiceRecordReader class
    public InvoiceRecord readInvoiceRecord(String message) throws IOException {
        return readInvoiceRecord(new ReaderSession(new StringReader(message)));
    }

    // TODO move to InvoiceRecordReader class
    private InvoiceRecord readInvoiceRecord(ReaderSession reader) throws IOException {
        InvoiceRecord invoiceRecord;

        try {
            int invoiceRecordType = reader.readInt("InvoiceRecordType", 2);
            switch (invoiceRecordType) {
                case 10:
                    invoiceRecord = new InvoiceRecordType10();
                    break;
                case 20:
                    invoiceRecord = new InvoiceRecordType20();
                    break;
                case 30:
                    invoiceRecord = new InvoiceRecordType30();
                    break;
                case 50:
                    invoiceRecord = new InvoiceRecordType50();
                    break;
                case 51:
                    invoiceRecord = new InvoiceRecordType51();
                    break;
                case 52:
                    invoiceRecord = new InvoiceRecordType52();
                    break;
                case 80:
                    invoiceRecord = new InvoiceRecordType80();
                    break;
                case 90:
                    invoiceRecord = new InvoiceRecordType90();
                    break;
                default:
                    // TODO this should return a "GenericInvoiceRecord" that simply tries to read the 800 characters...
                    return null;
            }
        } catch (IOException e) {
            return null;
        }

        Collection<ZoneDescription> zoneDescriptions = invoiceRecord.getZoneDescriptionsByZone().values();
        for (ZoneDescription zoneDescription : zoneDescriptions) {

            if (!invoiceRecord.contains(zoneDescription.getZones()[0])) {
                String value = reader.read(zoneDescription.getLabel(), zoneDescription.getLength());
                invoiceRecord.append(zoneDescription, value);
            }
        }

        return invoiceRecord;
    }

    private void readAcknowledgment(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        Acknowledgment acknowledgment = new Acknowledgment();
        acknowledgment.setMessageName(reader.read("Nom du message vise par cette communication", 6));
        reader.readInt("Code erreur", 2);
        acknowledgment.setReserve(reader.read("Reserve", 152));
        invoicing.setAcknowledgment(acknowledgment);
    }

    private boolean readBordereau95(ReaderSession reader, BelgianInsuranceInvoicing invoicing) {
        try {
            Bordereau95 bordereau = new Bordereau95();
            bordereau.setType(reader.readInt("Type de record", 2));
            reader.readInt("Code erreur", 2);
            bordereau.setMutualityCode(reader.readInt("Numero de mutualite", 3));
            reader.readInt("Code erreur", 2);
            bordereau.setInvoiceRecapNumber(reader.readLong("Numero de facture recapitulative", 12));
            reader.readInt("Code erreur", 2);
            bordereau.setAccountARequestedAmountSign(reader.read("Signe montant demande compte A", 1));
            bordereau.setAccountARequestedAmount(reader.readLong("Montant demande compte A", 11));
            reader.readInt("Code erreur", 2);
            bordereau.setAccountBRequestedAmountSign(reader.read("Signe montant demande code B", 1));
            bordereau.setAccountBRequestedAmount(reader.readLong("Montant demande compte B", 11));
            reader.readInt("Code erreur", 2);
            bordereau.setAccountsABTotalRequestedAmountSign(reader.read("Signe montant demande compte A + compte B", 1));
            bordereau.setAccountsABTotalRequestedAmount(reader.readLong("Total montants demandes compte A + compte B", 11));
            reader.readInt("Code erreur", 2);
            bordereau.setRecordsAmount(reader.readInt("Nombre d'enregistrements", 8));
            reader.readInt("Code erreur", 2);
            bordereau.setMutualityControlNumber(reader.readInt("N de controle par mutualite", 2));
            reader.readInt("Code erreur", 2);
            bordereau.setReserve(reader.read("Reserve", 271));
            invoicing.getBordereaus95().add(bordereau);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private void readMessageHeader(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        ErrorCount errorCount = new ErrorCount();
        errorCount.setInvoicingYearMonth(reader.readInt("Annee et mois de facturation", 6));
        reader.readInt("Code erreur", 2);
        errorCount.setSendNumber(reader.readInt("Numero d'envoi", 3));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceCreationDate(reader.readInt("Date de creation de la facture", 8));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceReference(reader.read("Reference facture", 13));
        reader.readInt("Code erreur", 2);
        errorCount.setInstructionsVersion(reader.readInt("Numero de version des instructions", 7));
        reader.readInt("Code erreur", 2);
        errorCount.setContactPersonLastName(reader.read("Nom de la personne de contact OA", 45));
        reader.readInt("Code erreur", 2);
        errorCount.setContactPersonFirstName(reader.read("Prenom de la personne de contact OA", 24));
        reader.readInt("Code erreur", 2);
        errorCount.setContactPersonPhone(reader.read("Numero de telephone de la personne de contact OA", 10));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceType(reader.readInt("Type de facture", 2));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoicingMode(reader.readInt("Mode facturation", 2));
        reader.readInt("Code erreur", 2);
        errorCount.setReserve(reader.read("Reserve", 20));
        invoicing.setErrorCount(errorCount);
    }

    private void readErrorCount(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        ErrorCount errorCount = new ErrorCount();
        errorCount.setInvoicingYearMonth(reader.readInt("Annee et mois de facturation", 6));
        reader.readInt("Code erreur", 2);
        errorCount.setSendNumber(reader.readInt("Numero d'envoi", 3));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceCreationDate(reader.readInt("Date de creation de la facture", 8));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceReference(reader.read("Reference facture", 13));
        reader.readInt("Code erreur", 2);
        errorCount.setInstructionsVersion(reader.readInt("Numero de version des instructions", 7));
        reader.readInt("Code erreur", 2);
        errorCount.setContactPersonLastName(reader.read("Nom de la personne de contact OA", 45));
        reader.readInt("Code erreur", 2);
        errorCount.setContactPersonFirstName(reader.read("Prenom de la personne de contact OA", 24));
        reader.readInt("Code erreur", 2);
        errorCount.setContactPersonPhone(reader.read("Numero de telephone de la personne de contact OA", 10));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceType(reader.readInt("Type de facture", 2));
        reader.readInt("Code erreur", 2);
        errorCount.setInvoicingMode(reader.readInt("Mode facturation", 2));
        reader.readInt("Code erreur", 2);
        errorCount.setErrorsPercentage(((double) reader.readInt("Pourcentage erreurs", 5)) / 100.0);
        reader.readInt("Code erreur", 2);
        errorCount.setInvoiceRejectionType(reader.readInt("Type refus facturation", 2));
        reader.readInt("Code erreur", 2);
        errorCount.setReserve(reader.read("Reserve", 459));
        invoicing.setErrorCount(errorCount);
    }

    private void readMessageDetails(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        List<ErrorDetail> errorDetails = invoicing.getErrorDetails();

        InvoiceRecord invoiceRecord = readInvoiceRecord(reader);
        while (invoiceRecord != null) {
            errorDetails.add(new ErrorDetail(invoiceRecord));
            invoiceRecord = readInvoiceRecord(reader);
        }
    }

    private void readErrorDetails(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        List<ErrorDetail> errorDetails = invoicing.getErrorDetails();

        InvoiceRecord invoiceRecord = readInvoiceRecord(reader);
        while (invoiceRecord != null) {
            ErrorDetail errorDetail = new ErrorDetail(invoiceRecord);
            errorDetail.setSendingId(reader.readInt("Identification envoi", 3));
            errorDetail.setCreationDate(reader.readInt("Date creation envoi", 8));
            errorDetail.setInvoicingYearMonth(reader.readInt("Mois et annee de facturation", 6));
            try {
                errorDetail.setMutualityCode(reader.readInt("Mutualite", 3));
            } catch (NumberFormatException ignored) {
            }
            reader.read("", 86);
            errorDetail.setRejectionLetter1(reader.read("Lettre rejet 1", 1));
            errorDetail.setRejectionCode1(reader.read("Code rejet 1", 6));
            errorDetail.setRejectionLetter2(reader.read("Lettre rejet 2", 1));
            errorDetail.setRejectionCode2(reader.read("Code rejet 2", 6));
            errorDetail.setRejectionLetter3(reader.read("Lettre rejet 3", 1));
            errorDetail.setRejectionCode3(reader.read("Code rejet 3", 6));
            reader.read("", 44);
            errorDetail.setOaResult(reader.read("Resultat OA", 12));
            errorDetail.setErrorCodeComment(reader.read("Commentaire du code erreur", 200));
            errorDetail.setReserve(reader.read("Reserve", 61));
            errorDetail.setIndex(reader.readInt("Index", 6));

            if (!errorDetail.getRejectionCode1().equals("000000") && "BREFS".contains(errorDetail.getRejectionLetter1())) {
                errorDetail.setRejectionDescr1(getErrorCodeDescription(errorDetail.getRejectionLetter1() + errorDetail.getRejectionCode1(), null));
                ZoneDescription zoneDescription = invoiceRecord.getZoneDescriptionsByZone().get(errorDetail.getRejectionCode1().substring(2, 4));
                errorDetail.setRejectionZoneDescr1(zoneDescription == null ? "" : zoneDescription.getLabel());
            }
            if (!errorDetail.getRejectionCode2().equals("000000") && "BREFS".contains(errorDetail.getRejectionLetter2())) {
                errorDetail.setRejectionDescr2(getErrorCodeDescription(errorDetail.getRejectionLetter2() + errorDetail.getRejectionCode2(), null));
                ZoneDescription zoneDescription = invoiceRecord.getZoneDescriptionsByZone().get(errorDetail.getRejectionCode2().substring(2, 4));
                errorDetail.setRejectionZoneDescr2(zoneDescription == null ? "" : zoneDescription.getLabel());
            }
            if (!errorDetail.getRejectionCode3().equals("000000") && "BREFS".contains(errorDetail.getRejectionLetter3())) {
                errorDetail.setRejectionDescr3(getErrorCodeDescription(errorDetail.getRejectionLetter3() + errorDetail.getRejectionCode3(), null));
                ZoneDescription zoneDescription = invoiceRecord.getZoneDescriptionsByZone().get(errorDetail.getRejectionCode3().substring(2, 4));
                errorDetail.setRejectionZoneDescr3(zoneDescription == null ? "" : zoneDescription.getLabel());
            }

            errorDetails.add(errorDetail);

            invoiceRecord = readInvoiceRecord(reader);
        }
    }

    private void readIdentificationFlux(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        IdentificationFlux flux = new IdentificationFlux();
        flux.setMessageName(reader.readInt("Nom du message", 6));
        reader.readInt("Code erreur", 2);
        flux.setMessageFormatVersion(reader.readInt("N version du format message", 2));
        reader.readInt("Code erreur", 2);
        flux.setMessageType(reader.readInt("Type message", 2));
        reader.readInt("Code erreur", 2);
        flux.setMessageStatus(reader.readInt("Statut du message", 2));
        reader.readInt("Code erreur", 2);
        flux.setMessageReference(reader.readLong("Reference du message (prestataire ou institution)", 14));
        reader.readInt("Code erreur", 2);
        flux.setMessageReferenceOA(reader.readLong("Reference message OA", 14));
        reader.readInt("Code erreur", 2);
        flux.setReserve(reader.read("Reserve", 15));
        flux.setMessageDescription(getMessageDescription("M" + flux.getMessageName(), null));
        invoicing.setIdentificationFlux(flux);
    }

    private void readInvoiceBordereau(ReaderSession reader, BelgianInsuranceInvoicing invoicing) throws IOException {
        InvoiceBordereau bordereau = new InvoiceBordereau();
        bordereau.setInvoicingYearMonth(reader.readInt("Annee et mois de facturation", 6));
        reader.readInt("Code erreur", 2);
        bordereau.setSendNumber(reader.readInt("Numero d'envoi", 3));
        reader.readInt("Code erreur", 2);
        bordereau.setInvoiceCreationDate(reader.readInt("Date de creation de la facture", 8));
        reader.readInt("Code erreur", 2);
        bordereau.setInvoiceReference(reader.read("Reference de la facture", 13));
        reader.readInt("Code erreur", 2);
        bordereau.setInstructionsVersion(reader.readInt("Numero de version des instructions", 7));
        reader.readInt("Code erreur", 2);
        bordereau.setContactPersonLastName(reader.read("Nom de la personne de contact", 45));
        reader.readInt("Code erreur", 2);
        bordereau.setContactPersonFirstName(reader.read("Prenom de la personne de contact", 24));
        reader.readInt("Code erreur", 2);
        bordereau.setContactPersonPhone(reader.read("Numero de telephone de la personne de contact", 10));
        reader.readInt("Code erreur", 2);
        bordereau.setInvoiceType(reader.readInt("Type de facture", 2));
        reader.readInt("Code erreur", 2);
        bordereau.setInvoicingType(reader.readInt("Type de facturation", 2));
        reader.readInt("Code erreur", 2);
        bordereau.setReserve(reader.read("Reserve", 20));
        invoicing.setInvoiceBordereau(bordereau);
    }

    public BelgianInsuranceInvoicing read(String message) throws IOException {
        ReaderSession reader = new ReaderSession(new StringReader(message));
        String messageType = reader.getMessageType();
        if ("920000".equals(messageType)) {
            return read920000(message);
        } else if ("920098".equals(messageType)) {
            return read920098(message);
        } else if ("920099".equals(messageType)) {
            return read920099(message);
        } else if ("920900".equals(messageType)) {
            return read920900(message);
        } else if ("920999".equals(messageType)) {
            return read920999(message);
        } else if ("931000".equals(messageType)) {
            return read931000(message);
        } else {
            throw new RuntimeException("Unknown message type for message: " + message);
        }
    }
}
