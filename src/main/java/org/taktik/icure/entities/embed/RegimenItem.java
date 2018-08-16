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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.taktik.icure.entities.base.Code;

import java.io.Serializable;
import java.text.SimpleDateFormat;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegimenItem  implements Serializable {
	//Day definition (One and only one of the three following should be not null)
	//The three are null if it applies to every day
	Long date; //yyyymmdd at this date
	Integer dayNumber; //day 1 of treatment. 1 based numeration
	Weekday weekday; //on monday

	//Time of day definition (One and only one of the three following should be not null)
	//Both are null if not specified
	Code dayPeriod; //CD-DAYPERIOD
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

	public Code getDayPeriod() {
		return dayPeriod;
	}

	public void setDayPeriod(Code dayPeriod) {
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
		Code weekday; //CD-WEEKDAY
		Integer weekNumber; //Can be null

		public Code getWeekday() {
			return weekday;
		}

		public void setWeekday(Code weekday) {
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
		Code administrationUnit; //CD-ADMINISTRATIONUNIT
		String unit; //Should be null

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public Code getAdministrationUnit() {
			return administrationUnit;
		}

		public void setAdministrationUnit(Code administrationUnit) {
			this.administrationUnit = administrationUnit;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		@Override
		public String toString() {
			return String.format("%f %s", getQuantity(), getAdministrationUnit() != null ? getAdministrationUnit().getCode() : getUnit());
		}
	}

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String result = this.date != null ? String.format("the %s", df.format(this.date)) : this.dayNumber != null ? String.format("on day %d", this.dayNumber) : this.weekday != null && this.weekday.weekday != null && this.weekday.weekday.getCode() != null ? String.format("on %s", this.weekday.weekday.getCode()) : null;

		if (this.dayPeriod != null && !StringUtils.isEmpty(this.dayPeriod.getCode())) {
			result = result != null ? String.format("%s %s", result, this.dayPeriod.getCode()) : this.dayPeriod.getCode();
		}
		if (this.timeOfDay != null ) {
			String timeOfDayDescr = this.timeOfDay == 120000 ? "noon" : String.format("%d:%d",this.timeOfDay/10000,(this.timeOfDay/100)%100);
			result = result != null ? String.format("%s at %s", result, timeOfDayDescr) :  String.format("at %s", timeOfDayDescr);
		}

		return String.format("%s, %s",this.administratedQuantity,result);
	}
}
