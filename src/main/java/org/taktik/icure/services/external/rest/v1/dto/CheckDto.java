package org.taktik.icure.services.external.rest.v1.dto;


import java.io.Serializable;

public class CheckDto implements Serializable {
    private Boolean ok = true;
    private String app;
    private String version;

    @Override
    public String toString() {
        return "CheckDto{" +
                "ok=" + ok +
                ", app='" + app + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
