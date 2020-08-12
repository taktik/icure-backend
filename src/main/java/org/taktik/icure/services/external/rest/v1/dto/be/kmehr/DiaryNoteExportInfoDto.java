package org.taktik.icure.services.external.rest.v1.dto.be.kmehr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;

public class DiaryNoteExportInfoDto extends ExportInfoDto implements Serializable {
    private List<String> tags;
    private List<String> contexts;
    private Boolean psy;
    private String documentId;
    private String attachmentId;
    private String note;

    public List<String> getSecretForeignKeys() {
        return secretForeignKeys;
    }

    public void setSecretForeignKeys(List<String> secretForeignKeys) {
        this.secretForeignKeys = secretForeignKeys;
    }

    public List<String> getTags() {
        if(tags != null)
            return tags;
        else
            return new ArrayList<String>();
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getContexts() {
        if(contexts != null)
            return contexts;
        else
            return new ArrayList<String>();
    }

    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public HealthcarePartyDto getRecipient() {
        return recipient;
    }

    public void setRecipient(HealthcarePartyDto recipient) {
        this.recipient = recipient;
    }

    public Boolean isPsy() {
        return psy;
    }

    public void setPsy(Boolean isPsy) {
        psy = isPsy;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }
}
