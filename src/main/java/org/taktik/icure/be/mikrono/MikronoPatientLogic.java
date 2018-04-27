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

package org.taktik.icure.be.mikrono;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.taktik.icure.be.mikrono.dto.ChangeExternalIDReplyDto;
import org.taktik.icure.entities.Patient;

/**
 * Created by aduchate on 16/12/11, 12:59
 */
public interface MikronoPatientLogic {
    List<Long> createPatients(String url, Collection<Patient> patients, String mikronoUser, String mikronoPassword);

    Patient loadPatient(String url, String id, String mikronoUser, String mikronoPassword);

    List<String> listPatients(String url, Date fromDate, String mikronoUser, String mikronoPassword);

	ChangeExternalIDReplyDto updateExternalIds(String url, Map<String, String> ids, String mikronoUser, String mikronoPassword);

	void updatePatientId(String url, String id, String externalId, String mikronoUser, String mikronoPassword);

	Patient loadPatientWithIcureId(String url, String id, String mikronoUser, String mikronoPassword);

    void updatePatients(String url, Collection<Patient> patients, String mikronoUser, String mikronoPassword);

}
