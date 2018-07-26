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

package org.taktik.icure.logic;

import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.embed.Delegation;

import java.util.List;
import java.util.Set;

/**
 * Created by emad7105 on 24/06/2014.
 */
public interface HealthElementLogic extends EntityPersister<HealthElement, String> {

	HealthElement createHealthElement(HealthElement healthElement);

	HealthElement getHealthElement(String healthElementId);

	List<HealthElement> getHealthElements(List<String> healthElementIds);

	List<HealthElement> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys);

	Set<String> deleteHealthElements(Set<String> ids);

	HealthElement modifyHealthElement(HealthElement healthElement);

	HealthElement addDelegation(String healthElementId, String healthcarePartyId, Delegation delegation);

	HealthElement addDelegations(String healthElementId, List<Delegation> delegations);

	void solveConflicts();
}
