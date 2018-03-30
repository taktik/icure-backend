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

package org.taktik.icure.logic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.ektorp.UpdateConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.FormDAO;
import org.taktik.icure.dao.Option;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Form;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.FormLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.validation.aspect.Check;


@org.springframework.stereotype.Service
public class FormLogicImpl extends VersionableLogicImpl<Form, FormDAO> implements FormLogic {
	private static final Logger logger = LoggerFactory.getLogger(FormLogicImpl.class);

	private FormDAO formDAO;
	private ICureSessionLogic sessionLogic;
	private UUIDGenerator uuidGenerator;

	@Autowired
	public void setFormDAO(FormDAO formDAO) {
		this.formDAO = formDAO;
	}

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	public Form getForm(String id) {
		return formDAO.get(id);
	}

	@Override
	public List<Form> getForms(Collection<String> selectedIds) {
		return formDAO.getList(selectedIds);
	}

	@Override
	public List<Form> findByHCPartyPatient(String hcPartyId, List<String> secretPatientKeys, String healthElementId, String planOfActionId, String formTemplateId) {
		List<Form> forms = formDAO.findByHcPartyPatient(hcPartyId, secretPatientKeys);

		return (healthElementId==null && planOfActionId==null && formTemplateId==null) ? forms : forms.stream().filter((f)->
						(healthElementId == null || healthElementId.equals(f.getHealthElementId())) &&
						(planOfActionId == null || planOfActionId.equals(f.getPlanOfActionId())) &&
						(formTemplateId == null || formTemplateId.equals(f.getFormTemplateId()))).collect(Collectors.toList());
	}

	@Override
	public Form addDelegation(String formId, Delegation delegation) {
		Form form = getForm(formId);
		form.addDelegation(delegation.getDelegatedTo(),delegation);

		return formDAO.save(form);
	}

	@Override
	public Form createForm(@Check @NotNull Form form) throws MissingRequirementsException {

		List<Form> createdForms = new ArrayList<>(1);
		try {
			// Fetching the hcParty
			String healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();

			// Setting contact attributes
			if (form.getId()==null) { form.setId(uuidGenerator.newGUID().toString()); }

			form.setAuthor(sessionLogic.getCurrentUserId());
			if (form.getResponsible() == null) { form.setResponsible(healthcarePartyId); }

			createEntities(Collections.singleton(form), createdForms);
		} catch (Exception e) {
			logger.error("createContact: " + e.getMessage());
			throw new IllegalArgumentException("Invalid contact", e);
		}
		return createdForms.size() == 0 ? null:createdForms.get(0);
	}

	@Override
	public Set<String> deleteForms(Set<String> ids) {
		try {
			deleteEntities(ids);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return ids;	}

	@Override
	public void modifyForm(@Check @NotNull Form form) throws MissingRequirementsException {

		try {
			String healthcarePartyId = sessionLogic.getCurrentHealthcarePartyId();

			Form previousForm = getForm(form.getId());

			if (form.getCreated() == null) { form.setCreated(previousForm.getCreated()); }
			form.setAuthor(healthcarePartyId);

			formDAO.save(form);
		} catch (UpdateConflictException e) {
			//resolveConflict(form, e);
			logger.warn("Documents of class {} with id {} and rev {} could not be merged",form.getClass().getSimpleName(),form.getId(),form.getRev());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid contact", e);
		}
	}

	@Override
	public List<Form> findByHcPartyParentId(String hcPartyId, String formId) {
		return formDAO.findByHcPartyParentId(hcPartyId, formId);
	}

	@Override
	public Form addDelegations(String formId, List<Delegation> delegations) {
		Form form = getForm(formId);
		delegations.forEach(d->form.addDelegation(d.getDelegatedTo(),d));

		return formDAO.save(form);
	}

	@Override
	protected FormDAO getGenericDAO() {
		return formDAO;
	}

	@Override
	public void solveConflicts() {
		List<Form> formsInConflict = formDAO.listConflicts().stream().map(it -> formDAO.get(it.getId(), Option.CONFLICTS)).collect(Collectors.toList());

		formsInConflict.forEach(form -> {
			Arrays.stream(form.getConflicts()).map(c -> formDAO.get(form.getId(), c)).forEach(cp -> {
				form.solveConflictWith(cp);
				formDAO.purge(cp);
			});
			formDAO.save(form);
		});
	}

}
