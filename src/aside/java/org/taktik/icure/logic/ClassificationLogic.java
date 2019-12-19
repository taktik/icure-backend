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

import org.taktik.icure.entities.Classification;
import org.taktik.icure.entities.embed.Delegation;

import java.util.List;
import java.util.Set;

/**
 * Created by dlm on 16-07-18
 */
public interface ClassificationLogic extends EntityPersister<Classification, String> {

	Classification createClassification(Classification classification);

	Classification getClassification(String classificationId);

	List<Classification> findByHCPartySecretPatientKeys(String hcPartyId, List<String> secretPatientKeys);

	Set<String> deleteClassifications(Set<String> ids);

	Classification modifyClassification(Classification classification);

	Classification addDelegation(String classificationId, String healthcarePartyId, Delegation delegation);

	Classification addDelegations(String classificationId, List<Delegation> delegations);

    List<Classification> getClassificationByIds(List<String> ids);
}
