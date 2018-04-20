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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class EIDItem {
	private String deviceType;
	private Long readDate;
	private int readHour;
	private String readType;
	private String readvalue;

	public EIDItem() {
        deviceType = "1";
        readType = "1";
        readDate = new Date().getTime();

        Calendar cal = Calendar.getInstance();

        readHour = cal.get(Calendar.HOUR_OF_DAY)*100+cal.get(Calendar.MINUTE);
    }

    public EIDItem(Long readDate, Integer readHour, String readvalue) {
	    deviceType = "1";
        readType = "1";

	    this.readvalue = readvalue;
	    this.readDate = readDate;
        this.readHour = readHour;
    }

    public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Long getReadDate() {
		return readDate;
	}

	public void setReadDate(Long readDate) {
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

	public String getReadvalue() {
		return readvalue;
	}

	public void setReadvalue(String readvalue) {
		this.readvalue = readvalue;
	}
}
