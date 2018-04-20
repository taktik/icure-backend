/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.io.Serializable;

import org.taktik.icure.services.external.rest.v1.dto.CodeDto;

public class RegimenItemDto implements Serializable {
	//Day definition (One and only one of the three following should be not null)
	Long date; //yyyymmdd
	Integer dayNumber;
	Weekday weekday;

	//Time of day definition (One and only one of the two following should be not null)
	CodeDto dayPeriod;
	Long timeOfDay; //hhmmss 103010

	AdministrationQuantity administratedQuantity;

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Integer getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(Integer dayNumber) {
		this.dayNumber = dayNumber;
	}

	public Weekday getWeekday() {
		return weekday;
	}

	public void setWeekday(Weekday weekday) {
		this.weekday = weekday;
	}

	public CodeDto getDayPeriod() {
		return dayPeriod;
	}

	public void setDayPeriod(CodeDto dayPeriod) {
		this.dayPeriod = dayPeriod;
	}

	public Long getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(Long timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public AdministrationQuantity getAdministratedQuantity() {
		return administratedQuantity;
	}

	public void setAdministratedQuantity(AdministrationQuantity administratedQuantity) {
		this.administratedQuantity = administratedQuantity;
	}

	public static class Weekday  implements Serializable{
		CodeDto weekday;
		Integer weekNumber;

		public CodeDto getWeekday() {
			return weekday;
		}

		public void setWeekday(CodeDto weekday) {
			this.weekday = weekday;
		}

		public Integer getWeekNumber() {
			return weekNumber;
		}

		public void setWeekNumber(Integer weekNumber) {
			this.weekNumber = weekNumber;
		}
	}

	public static class AdministrationQuantity implements Serializable {
		Double quantity;
		CodeDto administrationUnit;
		String unit; //Should be null

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public CodeDto getAdministrationUnit() {
			return administrationUnit;
		}

		public void setAdministrationUnit(CodeDto administrationUnit) {
			this.administrationUnit = administrationUnit;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}
}
