package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.FrontEndMigrationStatus;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontEndMigration extends StoredDocument implements Identifiable<String>, Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected Long startDate;
    protected Long endDate;
    protected FrontEndMigrationStatus status;
    protected String logs;
    protected String userId;
    protected String startKey;
    protected String startKeyDocId;
    protected Long processCount;


    public FrontEndMigration() {
    }

    public FrontEndMigration(String name, String userId, Long startDate, Long endDate, FrontEndMigrationStatus status, String logs) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.logs = logs;
        this.userId = userId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public FrontEndMigrationStatus getStatus() {
        return status;
    }

    public void setStatus(FrontEndMigrationStatus status) {
        this.status = status;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getStartKey() {
        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    public String getStartKeyDocId() {
        return startKeyDocId;
    }

    public void setStartKeyDocId(String startKeyDocId) {
        this.startKeyDocId = startKeyDocId;
    }


    public Long getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Long processCount) {
        this.processCount = processCount;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrontEndMigration frontEndMigration = (FrontEndMigration) o;

        if (id != null ? !id.equals(frontEndMigration.id) : frontEndMigration.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
