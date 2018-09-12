package org.taktik.icure.services.external.rest.v1.dto;

public class FlowItemDto  {

    protected String id;

    protected String title;

    protected Long receptionDate;

    protected Long cancellationDate;

    protected String canceller;

    protected Long processingDate;

    protected String processer;

    protected String phoneNumber;

    protected String patientId;

    protected String patientFirstName;

    protected String patientLastName;

    protected String status;

    protected String type;

    protected Boolean emergency;

    protected String cancellationReason;

    protected String cancellationNote;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getReceptionDate() {
        return receptionDate;
    }

    public void setReceptionDate(Long receptionDate) {
        this.receptionDate = receptionDate;
    }

    public Long getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(Long cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getCanceller() {
        return canceller;
    }

    public void setCanceller(String canceller) {
        this.canceller = canceller;
    }

    public Long getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Long processingDate) {
        this.processingDate = processingDate;
    }

    public String getProcesser() {
        return processer;
    }

    public void setProcesser(String processer) {
        this.processer = processer;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(Boolean emergency) {
        this.emergency = emergency;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getCancellationNote() {
        return cancellationNote;
    }

    public void setCancellationNote(String cancellationNote) {
        this.cancellationNote = cancellationNote;
    }
}
