package org.taktik.icure.services.external.rest.v1.dto;

public class UserGroupDto {
	String groupId;
	String userId;
	String groupName;

	public UserGroupDto() {
	}

	public UserGroupDto(String groupId, String userId, String groupName) {

		this.groupId = groupId;
		this.userId = userId;
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
