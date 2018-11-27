package org.taktik.icure.entities;

public class IdentityDocumentReader {

    private String justificatifDocumentNumber;
    private String SupportSerialNumber;
    private Long timeReadingEIdDocument;
    private int eIdDocumentSupportType;
    private int reasonManualEncoding;
    private int reasonUsingVignette;

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
