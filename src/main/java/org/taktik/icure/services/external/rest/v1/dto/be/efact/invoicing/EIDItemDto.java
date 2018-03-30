/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class EIDItemDto {
	private String deviceType;
	private Date readDate;
	private int readHour;
	private String readType;

    public EIDItemDto() {
        deviceType = "1";
        readType = "1";
        readDate = new Date();

        Calendar cal = Calendar.getInstance();

        readHour = cal.get(Calendar.HOUR_OF_DAY)*100+cal.get(Calendar.MINUTE);
    }

    public EIDItemDto(Date readDate, Integer readHour) {
        deviceType = "1";
        readType = "1";

        this.readDate = readDate;
        this.readHour = readHour;
    }

    public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

	public int getReadHour() {
		return readHour;
	}

	public void setReadHour(int readHour) {
		this.readHour = readHour;
	}

	public String getReadType() {
		return readType;
	}

	public void setReadType(String readType) {
		this.readType = readType;
	}
}
