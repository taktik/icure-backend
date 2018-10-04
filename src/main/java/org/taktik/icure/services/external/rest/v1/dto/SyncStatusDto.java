package org.taktik.icure.services.external.rest.v1.dto;

import java.util.LinkedList;
import java.util.List;

public class SyncStatusDto {
	private String groupId;
	private Long timestamp;
	private LinkedList<String> syncedUsers = new LinkedList<>();

	public SyncStatusDto() {
	}

	public SyncStatusDto(String groupId, Long timestamp, List<String> updates) {
		this.groupId = groupId;
		this.timestamp = timestamp;
		this.syncedUsers.addAll(updates);
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

	public LinkedList<String> getSyncedUsers() {
		return syncedUsers;
	}

	public void setSyncedUsers(LinkedList<String> syncedUsers) {
		this.syncedUsers = syncedUsers;
	}
}
