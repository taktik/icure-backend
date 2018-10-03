package org.taktik.icure.services.external.rest.v1.dto;

public class SyncStatusDto {
	private String groupId;
	private Long timestamp;

	public SyncStatusDto() {
	}

	public SyncStatusDto(String groupId, Long timestamp) {
		this.groupId = groupId;
		this.timestamp = timestamp;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
