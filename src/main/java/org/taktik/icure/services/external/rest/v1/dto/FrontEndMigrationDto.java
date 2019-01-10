package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.embed.FrontEndMigrationStatus;

public class FrontEndMigrationDto extends StoredDto {
    protected String name;
    protected String userId;
    protected Long startDate;
    protected Long endDate;
    protected FrontEndMigrationStatus status;
    protected String logs;
    protected String startKey;
    protected String startKeyDocId;
    protected Long processCount;

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

    public String getStartKey() { return startKey; }

    public void setStartKey(String startKey) { this.startKey = startKey; }

    public String getStartKeyDocId() { return startKeyDocId; }

    public void setStartKeyDocId(String startKeyDocId) { this.startKeyDocId = startKeyDocId; }

    public Long getProcessCount() { return processCount; }

    public void setProcessCount(Long processCount) { this.processCount = processCount; }
}
