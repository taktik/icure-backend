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

package org.taktik.icure.dao.migration;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.taktik.icure.dao.FormTemplateDAO;
import org.taktik.icure.entities.FormTemplate;
import org.taktik.icure.services.external.rest.v1.dto.gui.Code;
import org.taktik.icure.services.external.rest.v1.dto.gui.CodeType;
import org.taktik.icure.services.external.rest.v1.dto.gui.Editor;
import org.taktik.icure.services.external.rest.v1.dto.gui.FormDataOption;
import org.taktik.icure.services.external.rest.v1.dto.gui.FormPlanning;
import org.taktik.icure.services.external.rest.v1.dto.gui.Formula;
import org.taktik.icure.services.external.rest.v1.dto.gui.Suggest;
import org.taktik.icure.services.external.rest.v1.dto.gui.Tag;
import org.taktik.icure.services.external.rest.v1.dto.gui.type.Data;

@Repository("formLayoutMigration")
public class FormLayoutMigration extends CouchDbRepositorySupport<MigrationStub> implements DbMigration {

	FormTemplateDAO formTemplateDAO;
	Gson gsonMapper;

	@Autowired
	protected FormLayoutMigration(@SuppressWarnings("SpringJavaAutowiringInspection") @Qualifier("couchdbBase") CouchDbConnector couchdb) {
		super(MigrationStub.class, couchdb, false);
	}

	@Override
	public boolean hasBeenApplied() {
		try {
			this.get(this.getClass().getCanonicalName());
			return true;
		} catch (DocumentNotFoundException ignored) {}
		return false;
	}

	@Override
	public void apply() {
		List<FormTemplate> templates = formTemplateDAO.getAll();
		Set<FormTemplate> modified = new HashSet<>();
		templates.forEach(t-> {
			try {
				if (t.getLayout() != null) {
					FormLayout from = gsonMapper.fromJson(new String(t.getLayout(), Charsets.UTF_8), FormLayout.class);
					if (from.getSections() != null) {
						from.getSections().forEach(s -> {
							if (s.getFormColumns() != null) {
								s.getFormColumns().forEach(c -> {
									if (c.getFormDataList() != null) {
										c.getFormDataList().forEach(fld -> {
											if (fld.getDefaultValue() != null) {
												fld.setDefaultValue(null);
												modified.add(t);
											}
										});
									}
								});
							}
						});

					}
					t.setLayout(gsonMapper.toJson(from).getBytes(Charsets.UTF_8));
				}
			} catch (JsonSyntaxException ignored) {}
		});
		formTemplateDAO.save(modified);
		this.update(new MigrationStub(this.getClass().getCanonicalName()));
	}

	@Autowired
	public void setFormTemplateDAO(FormTemplateDAO formTemplateDAO) {
		this.formTemplateDAO = formTemplateDAO;
	}

	@Autowired
	public void setGsonMapper(Gson gsonMapper) {
		this.gsonMapper = gsonMapper;
	}

	class FormLayout {
		private String name;
		private Double width;
		private Double height;
		private String descr;
		private Tag tag;
		private String guid;
		private String group;
		private List<FormSection> sections = new ArrayList<>();
		List<String> importedServiceXPaths;

		public String getDescr() {
			return descr;
		}

		public void setDescr(String descr) {
			this.descr = descr;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public Double getHeight() {
			return height;
		}

		public void setHeight(Double height) {
			this.height = height;
		}

		public List<String> getImportedServiceXPaths() {
			return importedServiceXPaths;
		}

		public void setImportedServiceXPaths(List<String> importedServiceXPaths) {
			this.importedServiceXPaths = importedServiceXPaths;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<FormSection> getSections() {
			return sections;
		}

		public void setSections(List<FormSection> sections) {
			this.sections = sections;
		}

		public Tag getTag() {
			return tag;
		}

		public void setTag(Tag tag) {
			this.tag = tag;
		}

		public Double getWidth() {
			return width;
		}

		public void setWidth(Double width) {
			this.width = width;
		}
	}

	class FormSection {
		private Integer columns;
		private List<FormColumn> formColumns = new ArrayList<FormColumn>();

		public Integer getColumns() {
			return columns;
		}

		public void setColumns(Integer columns) {
			this.columns = columns;
		}

		public List<FormColumn> getFormColumns() {
			return formColumns;
		}

		public void setFormColumns(List<FormColumn> formColumns) {
			this.formColumns = formColumns;
		}
	}

	class FormColumn {
		private List<FormLayoutData> formDataList = new ArrayList<FormLayoutData>();
		String columns;

		public String getColumns() {
			return columns;
		}

		public void setColumns(String columns) {
			this.columns = columns;
		}

		public List<FormLayoutData> getFormDataList() {
			return formDataList;
		}

		public void setFormDataList(List<FormLayoutData> formDataList) {
			this.formDataList = formDataList;
		}
	}
	public class FormLayoutData implements Serializable {
		Boolean subForm;

		Boolean irrelevant;
		String type;
		String name;
		Double sortOrder;

		Map<String, FormDataOption> options;
		String descr;
		String label;

		Editor editor;
		Data defaultValue;
		List<Suggest> suggest;
		List<FormPlanning> plannings;
		List<Code> tags;
		List<Code> codes;
		List<CodeType> codeTypes;
		List<Formula> formulas;

		public List<Code> getCodes() {
			return codes;
		}

		public void setCodes(List<Code> codes) {
			this.codes = codes;
		}

		public List<CodeType> getCodeTypes() {
			return codeTypes;
		}

		public void setCodeTypes(List<CodeType> codeTypes) {
			this.codeTypes = codeTypes;
		}

		public Data getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(Data defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String getDescr() {
			return descr;
		}

		public void setDescr(String descr) {
			this.descr = descr;
		}

		public Editor getEditor() {
			return editor;
		}

		public void setEditor(Editor editor) {
			this.editor = editor;
		}

		public List<Formula> getFormulas() {
			return formulas;
		}

		public void setFormulas(List<Formula> formulas) {
			this.formulas = formulas;
		}

		public Boolean getIrrelevant() {
			return irrelevant;
		}

		public void setIrrelevant(Boolean irrelevant) {
			this.irrelevant = irrelevant;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, FormDataOption> getOptions() {
			return options;
		}

		public void setOptions(Map<String, FormDataOption> options) {
			this.options = options;
		}

		public List<FormPlanning> getPlannings() {
			return plannings;
		}

		public void setPlannings(List<FormPlanning> plannings) {
			this.plannings = plannings;
		}

		public Double getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(Double sortOrder) {
			this.sortOrder = sortOrder;
		}

		public Boolean getSubForm() {
			return subForm;
		}

		public void setSubForm(Boolean subForm) {
			this.subForm = subForm;
		}

		public List<Suggest> getSuggest() {
			return suggest;
		}

		public void setSuggest(List<Suggest> suggest) {
			this.suggest = suggest;
		}

		public List<Code> getTags() {
			return tags;
		}

		public void setTags(List<Code> tags) {
			this.tags = tags;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

}
