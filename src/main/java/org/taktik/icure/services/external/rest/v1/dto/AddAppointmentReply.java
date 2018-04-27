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

package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;

public class AddAppointmentReply implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String sessionId;
	String mikronoId;
	String mikronoUrl;
	

	public AddAppointmentReply() {
	}
	public AddAppointmentReply(String mikronoUrl) {
		super();
		this.mikronoUrl=mikronoUrl;
	}
		
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getMikronoId() {
		return mikronoId;
	}
	public void setMikronoId(String mikronoId) {
		this.mikronoId = mikronoId;
	}
	public String getMikronoUrl() {
		return mikronoUrl;
	}
	public void setMikronoUrl(String mikronoUrl) {
		this.mikronoUrl = mikronoUrl;
	}

}
