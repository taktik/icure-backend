package org.taktik.icure.services.external.rest.v1.dto;

public class RightDto {
    private String user;
    private boolean read;
    private boolean write;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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
}
