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


package org.taktik.icure.be.format.logic.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.entities.embed.ServiceLink;
import org.taktik.icure.entities.embed.SubContact;
import org.taktik.icure.logic.FormLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public abstract class GenericResultFormatLogicImpl {
	protected HealthcarePartyLogic healthcarePartyLogic;
	protected UUIDGenerator uuidGen = new UUIDGenerator();
	protected FormLogic formLogic;

	@Autowired
	public void setFormLogic(FormLogic formLogic) {
		this.formLogic = formLogic;
	}

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	protected void fillContactWithLines(Contact ctc, List<LaboLine> lls, String planOfActionId, String hcpId, List<String> protocolIds, List<String> formIds) {
		lls.forEach((ll) -> {
			String formId = null;
			for (int i=0;i<protocolIds.size();i++) {
				if (protocolIds.get(i).equals(ll.getRil() != null ? ll.getRil().getProtocol() : ll.getResultReference()) || protocolIds.size()==1 && protocolIds.get(i)!=null && protocolIds.get(i).startsWith("***")) {
					formId = formIds.get(i);
				}
			}

			SubContact ssc = new SubContact();
			ssc.setResponsible(hcpId);
			ssc.setDescr(ll.labo);
			ssc.setProtocol(ll.resultReference);
			ssc.setPlanOfActionId(planOfActionId);

			ssc.setStatus((ll.isResultLabResult() ? SubContact.STATUS_LABO_RESULT : SubContact.STATUS_PROTOCOL_RESULT) | SubContact.STATUS_UNREAD | (ll.ril != null && ll.ril.complete ? SubContact.STATUS_COMPLETE : 0));
			ssc.setFormId(formId);
			ssc.setServices(ll.getServices().stream().map(s -> new ServiceLink(s.getId())).collect(Collectors.toList()));

			ctc.getServices().addAll(ll.getServices());
			ctc.getSubContacts().add(ssc);
		});
	}

	protected String decodeRawData(byte[] rawData) throws IOException {
		String text;
		String frenchCp850OrCp1252 = org.taktik.icure.db.StringUtils.detectFrenchCp850Cp1252(rawData);
		if ("cp850".equals(frenchCp850OrCp1252)) {
			text = new String(rawData, "cp850");
		} else if ("cp1252".equals(frenchCp850OrCp1252)) {
			text = new String(rawData, "cp1252");
		} else {
			text = new String(rawData, StandardCharsets.UTF_8);
		}
		return text;
	}

	protected org.w3c.dom.Document getXmlDocument(Document doc) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(new ByteArrayInputStream(doc.getAttachment()));
	}

	protected BufferedReader getBufferedReader(Document doc) throws IOException {
		return new BufferedReader(new StringReader(decodeRawData(doc.getAttachment())));
	}

	public static class LaboLine {
		protected String labo;
		protected String resultReference;
		protected String fullLine;

		protected List<LaboResultLine> labosList = new ArrayList<>();
		protected List<ProtocolLine> protoList = new ArrayList<>();

		protected ResultsInfosLine ril;
		protected PatientAddressLine pal;

		protected List<Service> services = new ArrayList<>();

		protected boolean resultLabResult;

		public String getLabo() {
			return labo;
		}

		public void setLabo(String labo) {
			this.labo = labo;
		}

		public String getResultReference() {
			return resultReference;
		}

		public void setResultReference(String resultReference) {
			this.resultReference = resultReference;
		}

		public String getFullLine() {
			return fullLine;
		}

		public void setFullLine(String fullLine) {
			this.fullLine = fullLine;
		}

		public List<LaboResultLine> getLabosList() {
			return labosList;
		}

		public void setLabosList(List<LaboResultLine> labosList) {
			this.labosList = labosList;
		}

		public List<ProtocolLine> getProtoList() {
			return protoList;
		}

		public void setProtoList(List<ProtocolLine> protoList) {
			this.protoList = protoList;
		}

		public ResultsInfosLine getRil() {
			return ril;
		}

		public void setRil(ResultsInfosLine ril) {
			this.ril = ril;
		}

		public PatientAddressLine getPal() {
			return pal;
		}

		public void setPal(PatientAddressLine pal) {
			this.pal = pal;
		}

		public List<Service> getServices() {
			return services;
		}

		public void setServices(List<Service> services) {
			this.services = services;
		}

		public boolean isResultLabResult() {
			return resultLabResult;
		}

		public void setResultLabResult(boolean resultLabResult) {
			this.resultLabResult = resultLabResult;
		}
	}

	class PatientLine {
		protected String lastName;
		protected String firstName;
		protected Timestamp dn;
		protected String sex;
		protected String protocol;

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public Timestamp getDn() {
			return dn;
		}

		public void setDn(Timestamp dn) {
			this.dn = dn;
		}

		public String getSex() {
			return sex;
		}

		public void setSex(String sex) {
			this.sex = sex;
		}

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
	}

	class PatientAddressLine {
		protected String protocol;
		protected String address;
		protected String number;
		protected String zipCode;
		protected String locality;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getZipCode() {
			return zipCode;
		}

		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}

		public String getLocality() {
			return locality;
		}

		public void setLocality(String locality) {
			this.locality = locality;
		}
	}

	class PatientSSINLine {
		protected String protocol;
		protected String ssin;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getSsin() {
			return ssin;
		}

		public void setSsin(String ssin) {
			this.ssin = ssin;
		}
	}

	class ProtocolLine {
		protected String protocol;
		protected String code;
		protected String text;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	class LaboResultLine {
		protected String protocol;
		protected String analysisCode;
		protected String analysisType;
		protected String referenceValues;
		protected String unit;
		protected String severity;
		protected String value;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public String getAnalysisCode() {
			return analysisCode;
		}

		public void setAnalysisCode(String analysisCode) {
			this.analysisCode = analysisCode;
		}

		public String getAnalysisType() {
			return analysisType;
		}

		public void setAnalysisType(String analysisType) {
			this.analysisType = analysisType;
		}

		public String getReferenceValues() {
			return referenceValues;
		}

		public void setReferenceValues(String referenceValues) {
			this.referenceValues = referenceValues;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getSeverity() {
			return severity;
		}

		public void setSeverity(String severity) {
			this.severity = severity;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	class ResultsInfosLine {
		protected String protocol;
		protected Instant demandDate;
		protected boolean complete;

		public String getProtocol() {
			return protocol;
		}

		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}

		public Instant getDemandDate() {
			return demandDate;
		}

		public void setDemandDate(Instant demandDate) {
			this.demandDate = demandDate;
		}

		public boolean isComplete() {
			return complete;
		}

		public void setComplete(boolean complete) {
			this.complete = complete;
		}
	}

	class Reference {
		protected Double minValue;
		protected Double maxValue;
		protected String unit;

		public Double getMinValue() {
			return minValue;
		}

		public void setMinValue(Double minValue) {
			this.minValue = minValue;
		}

		public Double getMaxValue() {
			return maxValue;
		}

		public void setMaxValue(Double maxValue) {
			this.maxValue = maxValue;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}
}
