package org.taktik.icure.services.external.rest.v1.dto;

public class IdentityDocumentReaderDto {

    protected String justificatifDocumentNumber;
    protected String SupportSerialNumber;
    protected Long timeReadingEIdDocument;
    protected int eIdDocumentSupportType;
    protected int reasonManualEncoding;
    protected int reasonUsingVignette;

    public String getJustificatifDocumentNumber() {
        return justificatifDocumentNumber;
    }

    public void setJustificatifDocumentNumber(String justificatifDocumentNumber) {
        this.justificatifDocumentNumber = justificatifDocumentNumber;
    }

    public String getSupportSerialNumber() {
        return SupportSerialNumber;
    }

    public void setSupportSerialNumber(String supportSerialNumber) {
        SupportSerialNumber = supportSerialNumber;
    }

    public Long getTimeReadingEIdDocument() {
        return timeReadingEIdDocument;
    }

    public void setTimeReadingEIdDocument(Long timeReadingEIdDocument) {
        this.timeReadingEIdDocument = timeReadingEIdDocument;
    }

    public int geteIdDocumentSupportType() {
        return eIdDocumentSupportType;
    }

    public void seteIdDocumentSupportType(int eIdDocumentSupportType) {
        this.eIdDocumentSupportType = eIdDocumentSupportType;
    }

    public int getReasonManualEncoding() {
        return reasonManualEncoding;
    }

    public void setReasonManualEncoding(int reasonManualEncoding) {
        this.reasonManualEncoding = reasonManualEncoding;
    }

    public int getReasonUsingVignette() {
        return reasonUsingVignette;
    }

    public void setReasonUsingVignette(int reasonUsingVignette) {
        this.reasonUsingVignette = reasonUsingVignette;
    }
}
