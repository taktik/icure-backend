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

package org.taktik.icure.dao;

import org.ektorp.support.View;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.base.Code;

import java.util.List;
import java.util.Set;

public interface HealthElementDAO  extends GenericDAO<HealthElement>  {
    List<HealthElement> findByPatient(String patientId);

	List<HealthElement> findByPatientAndCodes(String patientId, Set<Code> codes);

	HealthElement findHealthElementByPlanOfActionId(String planOfActionId);

	HealthElement getHealthElement(String healthElementId);

	List<HealthElement> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys);

	List<HealthElement> listConflicts();
}
