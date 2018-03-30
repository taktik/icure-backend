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

package org.taktik.icure.logic;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.taktik.icure.entities.Form;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.MissingRequirementsException;

/**
 * Created by emad7105 on 24/06/2014.
 */
public interface FormLogic extends EntityPersister<Form, String> {
	Form getForm(String id);

	List<Form> getForms(Collection<String> selectedIds);

    List<Form> findByHCPartyPatient(String hcPartyId, List<String> secretPatientKeys, String healthElementId, String planOfActionId, String formTemplateId);

	Form addDelegation(String formId, Delegation delegation);

	Form createForm(Form form) throws MissingRequirementsException;

	Set<String> deleteForms(Set<String> ids);

	void modifyForm(Form form) throws MissingRequirementsException;

	List<Form> findByHcPartyParentId(String hcPartyId, String formId);

	Form addDelegations(String formId, List<Delegation> delegations);

	void solveConflicts();
}
