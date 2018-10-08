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

package org.taktik.icure.logic.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.FormTemplateDAO;
import org.taktik.icure.dto.gui.layout.FormColumn;
import org.taktik.icure.dto.gui.layout.FormLayout;
import org.taktik.icure.dto.gui.layout.FormLayoutData;
import org.taktik.icure.dto.gui.layout.FormSection;
import org.taktik.icure.entities.FormTemplate;
import org.taktik.icure.logic.FormTemplateLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.utils.FormUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Charsets.*;

@Service
public class FormTemplateLogicImpl extends GenericLogicImpl<FormTemplate, FormTemplateDAO> implements FormTemplateLogic {
	private static Logger logger = LoggerFactory.getLogger(FormTemplateLogicImpl.class);

	private FormTemplateDAO formTemplateDAO;
	private ICureSessionLogic sessionLogic;
	private Gson gsonMapper;

	@Autowired
	public void setGsonMapper(Gson gsonMapper) {
		this.gsonMapper = gsonMapper;
	}

	@Autowired
	public void setFormTemplateDAO(FormTemplateDAO formTemplateDAO) {
		this.formTemplateDAO = formTemplateDAO;
	}

	@Override
	public boolean createEntities(Collection<FormTemplate> entities, Collection<FormTemplate> createdEntities) throws Exception {
		entities.stream().forEach((e) -> {
			if (e.getAuthor() == null) {
				e.setAuthor(sessionLogic.getCurrentUserId());
			}
		});
		return super.createEntities(entities, createdEntities);
	}


	@Override
	public FormTemplate createFormTemplate(FormTemplate entity) {
		if (entity.getAuthor() == null) {
			entity.setAuthor(sessionLogic.getCurrentUserId());
		}

		return formTemplateDAO.createFormTemplate(entity);
	}

	@Override
	public FormTemplate getFormTemplateById(String formTemplateId) {
		return formTemplateDAO.get(formTemplateId);
	}

	@Override
	public List<FormTemplate> getFormTemplatesByGuid(String userId, String specialityCode, String formTemplateGuid) {
		List<FormTemplate> byUserGuid = formTemplateDAO.findByUserGuid(userId, formTemplateGuid, true);
		return byUserGuid.size()>0 ? byUserGuid : formTemplateDAO.findBySpecialtyGuid(specialityCode, formTemplateGuid, true);
	}

	@Override
	public FormLayout extractLayout(FormTemplate formTemplate) {
		try {
			return new FormUtils().parseXml(new InputStreamReader(new ByteArrayInputStream(formTemplate.getLayout()), "UTF8"));

		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public FormTemplate get(String formId) {
		Preconditions.checkNotNull(formId, "Form ID is not allowed to be null.");

		FormTemplate formTemplate = null;
		InputStreamReader templateStreamReader = null;

		try {
			// TODO: at the moment, we load the layout from the file system. Later, a DAO will get it from DB.
			InputStream templateStream = this.getClass().getResourceAsStream("/forms/" + formId + ".xml");
			if (templateStream == null) {
				throw new FileNotFoundException("Could not find template file '" + "/forms/" + formId + ".xml'");
			}
			templateStreamReader = new InputStreamReader(templateStream, UTF_8);
			FormLayout formLayout = new FormUtils().parseXml(templateStreamReader);

			// to UTF8 byte[]
			String formLayoutJsonString = gsonMapper.toJson(formLayout, FormLayout.class);
			byte[] formLayoutBytes = formLayoutJsonString.getBytes(UTF_8);

			formTemplate = new FormTemplate();
			formTemplate.setLayout(formLayoutBytes);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("An unexpected error was encountered while retrieving a FormTemplate. ", e);

		} finally {
			if (templateStreamReader != null) {
				try {
					templateStreamReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		return formTemplate;
	}

	@Override
	public List<FormTemplate> getFormTemplatesBySpecialty(String specialityCode, boolean loadLayout) {
		return formTemplateDAO.findBySpecialtyGuid(specialityCode, null, loadLayout);
	}

	@Override
	public List<FormTemplate> getFormTemplatesByUser(String userId, boolean loadLayout) {
		return formTemplateDAO.findByUserGuid(userId, null, loadLayout);
	}

	@Override
	public FormTemplate modifyFormTemplate(FormTemplate formTemplate) {
		if (formTemplate.getAuthor() == null) {
			formTemplate.setAuthor(sessionLogic.getCurrentUserId());
		}

		return formTemplateDAO.save(formTemplate);
	}

	@Override
	public FormLayout build(byte[] data) {
		return gsonMapper.fromJson(new String(data, UTF_8), FormLayout.class);
	}


	@Override
	protected FormTemplateDAO getGenericDAO() {
		return formTemplateDAO;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	public List<String> getFieldsNames(FormLayout formLayout) {
		List<String> fieldNames = new ArrayList<>();

		List<FormSection> sections = formLayout.getSections();
		sections.forEach(section -> {

			List<FormColumn> formColumns = section.getFormColumns();
			formColumns.forEach(column -> {

				List<FormLayoutData> formDataList = column.getFormDataList();
				formDataList.forEach(formData -> fieldNames.add(formData.getName()));
			});
		});

		return fieldNames;
	}
}
