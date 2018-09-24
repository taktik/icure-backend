package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;

public class RightDto implements Serializable{

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
