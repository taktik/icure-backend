package org.taktik.icure.services.external.rest.v1.dto;


import java.util.List;

public class TimeTableDto extends IcureDto {

	protected String name;
	protected String userId;
	protected Long startTime;
	protected Long endTime;
	protected List<TimeTableItemDto> items;

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

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public List<TimeTableItemDto> getItems() {
		return items;
	}

	public void setItems(List<TimeTableItemDto> items) {
		this.items = items;
	}
}
