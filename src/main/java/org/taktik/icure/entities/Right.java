package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredDocument;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Right implements Serializable {

    private String userId;
    private boolean read;
    private boolean write;
    private boolean administration;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isAdministration() {
        return administration;
    }

    public void setAdministration(boolean administration) {
        this.administration = administration;
    }
}
