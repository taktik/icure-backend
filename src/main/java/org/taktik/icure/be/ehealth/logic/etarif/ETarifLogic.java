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

package org.taktik.icure.be.ehealth.logic.etarif;

import java.util.Date;
import java.util.List;

import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.etarif.TarificationConsultationResult;

public interface ETarifLogic {
	TarificationConsultationResult consultTarif(String token, String niss, List<String> codes, String justification) throws TokenNotAvailableException;

	TarificationConsultationResult consultTarif(String token, String niss, List<String> codes, String justification, Date encounterDateTime) throws TokenNotAvailableException;

	TarificationConsultationResult consultTarif(String token, String niss, List<String> codes, String justification, Date encounterDateTime, String nihiiDmg) throws TokenNotAvailableException;
}
