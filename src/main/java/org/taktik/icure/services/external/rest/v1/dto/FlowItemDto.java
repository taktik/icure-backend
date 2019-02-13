package org.taktik.icure.services.external.rest.v1.dto;

public class FlowItemDto  {

    protected String id;

    protected String title;

    protected String comment;

    protected Long receptionDate;

    protected Long processingDate;

    protected String processer;

    protected Long cancellationDate;

    protected String canceller;

    protected String cancellationReason;

    protected String cancellationNote;

    protected String status;

    protected Boolean homeVisit;

    protected String municipality;

    protected String town;

    protected String zipCode;

    protected String street;

    protected String building;

    protected String buildingNumber;

    protected String doorbellName;

    protected String floor;

    protected String letterBox;

    protected String notesOps;

    protected String notesContact;

    protected String latitude;

    protected String longitude;

    protected String type;

    protected Boolean emergency;

    protected String phoneNumber;

    protected String patientId;

    protected String patientLastName;

    protected String patientFirstName;

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

    public Boolean getHomeVisit() {
        return homeVisit;
    }

    public void setHomeVisit(Boolean homeVisit) {
        this.homeVisit = homeVisit;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getDoorbellName() {
        return doorbellName;
    }

    public void setDoorbellName(String doorbellName) {
        this.doorbellName = doorbellName;
    }

    public String getLetterBox() {
        return letterBox;
    }

    public void setLetterBox(String letterBox) {
        this.letterBox = letterBox;
    }

    public String getNotesOps() {
        return notesOps;
    }

    public void setNotesOps(String notesOps) {
        this.notesOps = notesOps;
    }

    public String getNotesContact() {
        return notesContact;
    }

    public void setNotesContact(String notesContact) {
        this.notesContact = notesContact;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

}
