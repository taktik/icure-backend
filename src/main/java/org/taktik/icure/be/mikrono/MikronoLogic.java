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

package org.taktik.icure.be.mikrono;

import org.jetbrains.annotations.NotNull;
import org.taktik.icure.dto.message.EmailOrSmsMessage;
import org.taktik.icure.services.external.rest.v1.dto.AppointmentDto;

import java.io.IOException;
import java.util.List;

public interface MikronoLogic {
	@NotNull
	String getPassword(String licenseId);

	String register(String serverUrl, String userId, String token);

	String getMikronoServer(String serverUrl);

	void sendMessage(String serverUrl, String username, String userToken, EmailOrSmsMessage emailOrSmsMessage) throws IOException;

	List<AppointmentDto> getAppointmentsByDate(String serverUrl, String username, String userToken, String ownerId, Long calendarDate);

	List<AppointmentDto> getAppointmentsByPatient(String serverUrl, String username, String userToken, String ownerId, String patientId, Long startTime, Long EndTime);
}
