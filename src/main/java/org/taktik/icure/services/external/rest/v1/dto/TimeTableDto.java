package org.taktik.icure.services.external.rest.v1.dto;


import java.util.List;

public class TimeTableDto extends IcureDto {

	protected String name;
	protected Long startTime;
	protected Long endTime;
	protected List<TimeTableItemDto> items;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TimeTableItemDto> getItems() {
		return items;
	}

	public void setItems(List<TimeTableItemDto> items) {
		this.items = items;
	}
}
