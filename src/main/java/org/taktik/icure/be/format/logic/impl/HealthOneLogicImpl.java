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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.format.logic.HealthOneLogic;
import org.taktik.icure.dto.result.ResultInfo;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.embed.Address;
import org.taktik.icure.entities.embed.AddressType;
import org.taktik.icure.entities.embed.Content;
import org.taktik.icure.entities.embed.Measure;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.*;
import org.taktik.icure.utils.FuzzyValues;

import java.io.*;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class HealthOneLogicImpl extends GenericResultFormatLogicImpl implements HealthOneLogic {
	private static Logger log = LoggerFactory.getLogger(HealthOneLogicImpl.class);

	static SimpleDateFormat shortDateFormat = new SimpleDateFormat("ddMMyyyy");
	private final DateTimeFormatter shortDateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
	static SimpleDateFormat extraDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	//\s*>\s*((?:-|\+)?[0-9]*(?:\.|,)?[0-9]*) matches __>__-01.29 and >+2,245 and >1  into $1
	//(?:(?:\s*([^0-9\s]\S*))|(?:\s+(\S+)))?\s* matches a0eraa and __a5656 (first part) or (_898989) in other words: any garbage that is separed by a space or
	//an alphanumerical character
	//We also allow for an open parenthesis, an open [ or both
	static Pattern greaterThanReference = Pattern.compile("\\s*(?:[\\(\\[]+\\s*)?>\\s*((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)(?:(?:\\s*([^0-9\\s]\\S*))|(?:\\s+(\\S+)))?\\s*");
	//The same with <
	static Pattern lessThanReference = Pattern.compile("\\s*(?:[\\(\\[]+\\s*)?<\\s*((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)(?:(?:\\s*([^0-9\\s]\\S*))|(?:\\s+(\\S+)))?\\s*");
	//GROUPA = ((?:-|\+)?[0-9]*(?:\.|,)?[0-9]*)\s* matches -01.29 and +2,245 and 1  into $1
	//We match _GROUPA__-__GROUPA[GARBAGE]
	//We also allow for an open parenthesis
	static Pattern betweenReference = Pattern.compile("\\s*(?:[\\(\\[]+\\s*)?((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)\\s*[-:]\\s*((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)(?:(?:\\s*([^0-9\\s]\\S*))|(?:\\s+(\\S+)))?\\s*");
	static Pattern address = Pattern.compile("^(?:\\s*(\\d+)(?:\\s*,\\s*|\\s+)(\\S.*?\\S)\\s*)|(?:\\s*(\\S.*?\\S)(?:\\s*,\\s*|\\s+)(\\d+)\\s*)$");
	static Pattern zipCode = Pattern.compile("^\\s*(\\d+)\\s*$");

	static String headerPattern = "^\\s*(\\d+)\\s+";
	static Pattern headerCompiledPrefix = Pattern.compile("^\\s*[0-9][0-9][0-9][0-9](\\d+)\\s+([A-Z][0-9])(.*)$");

	protected PatientLogic patientLogic;
	protected DocumentLogic documentLogic;
	protected ContactLogic contactLogic;

	@Autowired
	public void setContactLogic(ContactLogic contactLogic) {
		this.contactLogic = contactLogic;
	}

	@Autowired
	public void setPatientLogic(PatientLogic patientLogic) {
		this.patientLogic = patientLogic;
	}

	@Autowired
	public void setDocumentLogic(DocumentLogic documentLogic) {
		this.documentLogic = documentLogic;
	}

	/* Import a series of protocols from a document into a contact

	*/
	@Override
	public Contact doImport(String language, Document doc, String hcpId, List<String> protocolIds, List<String> formIds, String planOfActionId, Contact ctc) throws IOException {
		String text = decodeRawData(doc.getAttachment());
		if (text != null) {
			Reader r = new StringReader(text);

			List<LaboLine> lls = parseReportsAndLabs(language, protocolIds, r);
			fillContactWithLines(ctc, lls, planOfActionId, hcpId, protocolIds, formIds);

			try {
				return contactLogic.modifyContact(ctc);
			} catch (MissingRequirementsException e) {
				throw new IllegalStateException(e);
			}
		} else {
			throw new UnsupportedCharsetException("Charset could not be detected");
		}
	}

	public List<LaboLine> parseReportsAndLabs(String language, List<String> protocols, Reader r) throws IOException {
		List<LaboLine> result = new LinkedList<>();
		String line;
		BufferedReader reader = new BufferedReader(r);
		LaboLine ll = null;
		int position = 0;
		while ((line = reader.readLine()) != null) {
			position++;
			if (isLaboLine(line)) {
				if (ll != null) {
					createServices(ll, language, position);
				}

				ll = getLaboLine(line);
				if (protocols.contains(ll.resultReference)||(protocols.size()==1&&protocols.get(0)!=null&&protocols.get(0).startsWith("***"))) {
					result.add(ll);
				} else {
					ll = null;
				}
			} else if (ll != null && isLaboResultLine(line)) {
				LaboResultLine lrl = getLaboResultLine(line, ll);
				if (lrl != null) {
					ll.setResultLabResult(true);
					if (ll.labosList.size() > 0 && !lrl.analysisCode.equals(ll.labosList.get(0).analysisCode)) {
						createServices(ll, language, position);
					}
					ll.labosList.add(lrl);
				}
			} else if (ll != null && isProtocolLine(line)) {
				ProtocolLine pl = getProtocolLine(line);
				if (pl != null) {
					// Less than 20 lines ... If the codes are different,
					// We probably have a bad header... Just concatenate
					if (ll.protoList.size() > 20 && !pl.code.equals((ll.protoList.get(ll.protoList.size() - 1)).code)) {
						createServices(ll, language, position);
					}
					ll.protoList.add(pl);
				}
			} else if (ll != null && isResultsInfosLine(line)) {
				ll.ril = getResultsInfosLine(line);
			} else if (ll != null && isPatientAddressLine(line)) {
				ll.pal = getPatientAddressLine(line);
			}
		}
		if (ll != null) {
			createServices(ll, language, position);
		}
		return result;
	}

	private void createServices(LaboLine ll, String language, int position) {
		if (ll.labosList.size() > 0) {
			ll.services.addAll(importLaboResult(language, ll.labosList, position, ll.ril));
			ll.labosList.clear();
		}
		if (ll.protoList.size() > 0) {
			ll.services.add(importProtocol(language, ll.protoList, position, ll.ril));
			ll.protoList.clear();
		}
	}


	private Service importProtocol(String language, List protoList, int position, ResultsInfosLine ril) {
		String text = ((ProtocolLine) protoList.get(0)).text;
		for (int i = 1; i < protoList.size(); i++) {
			text += "\n" + ((ProtocolLine) protoList.get(i)).text;
		}

		Service s = new Service();
		s.setId(uuidGen.newGUID().toString());
		s.getContent().put(language, new Content(text));
		s.setLabel("Protocol");
		s.setIndex((long) position);
		s.setValueDate(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS));

		return s;
	}

	private List<Service> importLaboResult(String language, List labResults, int position, ResultsInfosLine ril) {
		List<Service> result = new ArrayList<>();
		if (labResults.size() > 1) {
			LaboResultLine lrl = (LaboResultLine) labResults.get(0);
			String comment = null;
			if (tryToGetValueAsNumber(lrl.value) != null) {
				LaboResultLine lrl2 = (LaboResultLine) labResults.get(1);
				comment = lrl2.value;
				for (int i = 2; i < labResults.size(); i++) {
					lrl = (LaboResultLine) labResults.get(i);
					if (StringUtils.isNotEmpty(lrl.value)) {
						comment += "\n" + lrl.value;
					}
				}

				result = addLaboResult((LaboResultLine) labResults.get(0), language, position, ril, comment);
			} else {
				String label = lrl.analysisType;
				if ((lrl.analysisType == null) || (lrl.analysisType.trim().length() == 0)) {
					label = "untitled";
				}

				String value = lrl.value;
				for (int i = 1; i < labResults.size(); i++) {
					lrl = (LaboResultLine) labResults.get(i);
					if (StringUtils.isNotEmpty(lrl.value)) {
						value += "\n" + lrl.value;
					}
				}

				Service s = new Service();
				s.setId(uuidGen.newGUID().toString());
				s.getContent().put(language, new Content(value));
				s.setLabel(label);
				s.setIndex((long) position);
				s.setValueDate(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS));

				result.add(s);
			}
		} else {
			LaboResultLine lrl = (LaboResultLine) labResults.get(0);
			result = addLaboResult((LaboResultLine) labResults.get(0), language, position, ril, null);
		}
		return result;
	}

	private List<Service> addLaboResult(LaboResultLine lrl, String language, int position, ResultsInfosLine ril, String comment) {
		List<Service> result = new ArrayList<>();
		Double d = tryToGetValueAsNumber(lrl.value);
		if (d != null) {
			//We import as a Measure
			result.add(importNumericLaboResult(language, d, lrl, position, ril, comment));
		} else {
			result.add(importPlainStringLaboResult(language, lrl, position, ril));
		}
		return result;
	}

	private Service importPlainStringLaboResult(String language, LaboResultLine lrl, int position, ResultsInfosLine ril) {
		String value = lrl.value + " " + lrl.unit;
		if (lrl.referenceValues.trim().length() > 0) {
			value += " (" + lrl.referenceValues + " )";
		}
		if (lrl.severity.trim().length() > 0) {
			value += " (" + lrl.severity.trim() + " )";
		}
		Service s = new Service();
		s.setId(uuidGen.newGUID().toString());
		s.getContent().put(language, new Content(value));
		s.setLabel(lrl.analysisType);
		s.setIndex((long) position);
		s.setValueDate(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS));
		return s;
	}

	private Service importNumericLaboResult(String language, Double d, LaboResultLine lrl, int position, ResultsInfosLine ril, String comment) {
		Measure m = new Measure();

		m.setValue(d);
		if (comment != null) {
			m.setComment(comment);
		}
		m.setUnit(lrl.unit);
		Reference r = tryToGetReferenceValues(lrl.referenceValues);
		if (r != null) {
			m.setMin(r.minValue);
			m.setMax(r.maxValue);
			//Handle the case where the labo has put the unit into the reference values
			if ((r.unit != null) && ((lrl.unit == null) || (lrl.unit.trim().length() == 0))) {
				m.setUnit(r.unit);
			}
		}

		if (lrl.severity.trim().length() > 0) {
			if (lrl.severity.equals("+")
					|| lrl.severity.equals("++")
					|| lrl.severity.equals("-")
					|| lrl.severity.equals("--")) {
				m.setSeverity(1);
			}
		}

		Service s = new Service();
		s.setId(uuidGen.newGUID().toString());
		s.getContent().put(language, new Content(m));
		s.setLabel(lrl.analysisType);
		s.setIndex((long) position);
		s.setValueDate(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS));

		return s;
	}

	private Double tryToGetValueAsNumber(String value) {
		String numberS = value.replaceAll(",", ".");
		try {
			return Double.parseDouble(numberS);
		} catch (Exception e) {
			System.out.println("--------- Failed to parse '" + numberS + "'");
			return null;
		}
	}

	private Reference tryToGetReferenceValues(String refValues) {
		try {
			Matcher m = betweenReference.matcher(refValues);
			if (m.matches()) {
				Reference r = new Reference();
				r.minValue = new Double(m.group(1).replaceAll(",", "."));
				r.maxValue = new Double(m.group(2).replaceAll(",", "."));
				if (m.group(3) != null) {
					r.unit = m.group(3);
				}
				if (m.group(4) != null) {
					r.unit = m.group(4);
				}
				return r;
			}
			m = lessThanReference.matcher(refValues);
			if (m.matches()) {
				Reference r = new Reference();
				r.maxValue = new Double(m.group(1).replaceAll(",", "."));
				if (m.group(2) != null) {
					r.unit = m.group(3);
				}
				if (m.group(3) != null) {
					r.unit = m.group(4);
				}
				return r;
			}
			m = greaterThanReference.matcher(refValues);
			if (m.matches()) {
				Reference r = new Reference();
				r.minValue = new Double(m.group(1).replaceAll(",", "."));
				if (m.group(2) != null) {
					r.unit = m.group(3);
				}
				if (m.group(3) != null) {
					r.unit = m.group(4);
				}
				return r;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	public List<ResultInfo> getInfos(Document doc) throws IOException {
		List<ResultInfo> l = new ArrayList<>();
		BufferedReader br = getBufferedReader(doc);
		String line;

		while ((line = br.readLine()) != null) {
			if (isLaboLine(line)) {
				LaboLine ll = getLaboLine(line);

				ResultInfo ri = new ResultInfo();
				ri.setLabo(ll.getLabo());

				while ((line = br.readLine()) != null) {
					if (isPatientLine(line)) {
						PatientLine p = getPatientLine(line);

						ri.setLastName(p.lastName);
						ri.setFirstName(p.firstName);
						if (p.dn != null) {
							ri.setDateOfBirth(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.dn.getTime()), ZoneId.systemDefault()), ChronoUnit.DAYS));
						}
						ri.setProtocol(p.protocol);
						ri.setSex(p.sex);
						ri.setDocumentId(doc.getId());
					} else if (isExtraPatientLine(line)) {
						PatientLine p = getExtraPatientLine(line);
						if (p.dn != null) {
							ri.setDateOfBirth(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.dn.getTime()), ZoneId.systemDefault()), ChronoUnit.DAYS));
						}
						if (p.sex != null) {
							ri.setSex(p.sex);
						}
					} else if (isResultsInfosLine(line)) {
						ResultsInfosLine r = getResultsInfosLine(line);
						if (r != null) {
							ri.setComplete(r.complete);
							ri.setDemandDate(r.demandDate.toEpochMilli());
						}
					} else if (isPatientSSINLine(line)) {
						PatientSSINLine p = getPatientSSINLine(line);
						if (p != null) {
							ri.setSsin(p.ssin);
						}
					} else if (isProtocolLine(line)) {
						ri.getCodes().add(new Code("CD-TRANSACTION", "report", "1"));
						break;
					} else if (isLaboResultLine(line)) {
						ri.getCodes().add(new Code("CD-TRANSACTION", "labresult", "1"));
						break;
					}
				}
				if (ri.getProtocol()==null ||ri.getProtocol().length()==0) {
					ri.setProtocol("***"+ri.getDemandDate());
				}
				l.add(ri);
			}
		}
		br.close();

		return l;
	}

	private boolean isPatientLine(String line) {
		return line.startsWith("A2") || line.matches(headerPattern + "S2.*");
	}

	private boolean isExtraPatientLine(String line) {
		return line.matches(headerPattern + "S4.*");
	}

	private boolean isPatientAddressLine(String line) {
		return line.startsWith("A3") || line.matches(headerPattern + "S3.*");
	}

	private boolean isResultsInfosLine(String line) {
		return line.startsWith("A4") || line.matches(headerPattern + "S5.*");
	}

	private boolean isPatientSSINLine(String line) {
		return line.startsWith("A5");
	}

	private boolean isLaboLine(String line) {
		return line.startsWith("A1") || line.matches(headerPattern + "S1.*");
	}

	private boolean isLaboResultLine(String line) {
		return line.startsWith("L1") || line.matches(headerPattern + "R1.*");
	}

	private boolean isProtocolLine(String line) {
		return line.startsWith("L5") || line.startsWith("L2");
	}

	private LaboLine getLaboLine(String line) {
		String[] parts = splitLine(line);
		LaboLine ll = new LaboLine();
		if (parts.length > 1) {
			ll.resultReference = parts[1];
		}
		if (parts.length > 2) {
			ll.labo = parts[2];
		}
		ll.fullLine = line;
		return ll;
	}

	private PatientLine getPatientLine(String line) {
		String[] parts = splitLine(line);
		PatientLine pl = new PatientLine();
		if (parts.length > 1) {
			pl.protocol = parts[1];
		}
		if (parts.length > 3) {
			pl.firstName = parts[3].trim();
		}
		if (parts.length > 2) {
			pl.lastName = parts[2].trim();
		}
		try {
			if (parts.length > 4) {
				pl.sex = parts[4].trim().equals("V") ? "F" : parts[4].trim();
				if (parts.length > 5 && parts[5].trim().length() == 8) {
					pl.dn = new Timestamp(shortDateFormat.parse(parts[5].trim()).getTime());
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pl;
	}

	private PatientLine getExtraPatientLine(String line) {
		String[] parts = splitLine(line);
		PatientLine pl = new PatientLine();
		if (parts.length > 1) {
			pl.protocol = parts[1];
		}
		if (parts.length > 3) {
			pl.sex = parts[3].trim().equals("V") ? "F" : parts[3].trim();
		}
		try {
			if (parts.length > 2) {
				pl.dn = new Timestamp(shortDateFormat.parse(parts[2].trim()).getTime());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return pl;
	}

	private LaboResultLine getLaboResultLine(String line, LaboLine ll) {
		try {
			String[] parts = splitLine(line);
			LaboResultLine lrl = new LaboResultLine();

			if (parts.length > 1) {
				lrl.protocol = parts[1];
			}
			if (parts.length > 2) {
				lrl.analysisCode = parts[2];
			}
			if (parts.length > 3) {
				lrl.analysisType = parts[3].trim();
			}

			if (!line.startsWith("L1")) {
				if (parts.length > 5) {
					lrl.referenceValues = parts[4].trim() + " - " + parts[5].trim();
				}
				if (parts.length > 6) {
					lrl.unit = parts[6].trim();
				}
				lrl.severity = "";
			} else {
				if ((lrl.analysisType == null || lrl.analysisType.length() == 0) && ll.labosList.size() > 0 && ll.labosList.get(ll.labosList.size() - 1).analysisCode != null && ll.labosList.get(ll.labosList.size() - 1).analysisCode.equals(lrl.analysisCode)) {
					lrl.analysisType = ll.labosList.get(ll.labosList.size() - 1).analysisType;
					lrl.value = parts[4];
				} else {
					if (parts.length > 4) {
						lrl.referenceValues = parts[4];
					}
					if (parts.length > 5) {
						lrl.unit = parts[5];
					}
					if (parts.length > 6) {
						lrl.severity = parts[6];
					}
				}
			}
			if (lrl.value == null) {
				lrl.value = parts[7].trim();
			}
			if (lrl.analysisType.equals("")) {
				lrl.analysisType = "Note";
			}
			return lrl;
		} catch (Exception e) {
			System.out.println("------------Line = " + line);
			e.printStackTrace();
			return null;
		}
	}

	private ProtocolLine getProtocolLine(String line) {
		try {
			String[] parts = splitLine(line);
			ProtocolLine pl = new ProtocolLine();
			if (parts.length > 1) {
				pl.protocol = parts[1];
			}
			if (parts.length > 2) {
				pl.code = parts[2];
			}
			if (parts.length > 7) {
				pl.text = parts[7];
			} else if (parts.length > 3) {
				pl.text = parts[3];
			}
			return pl;
		} catch (Exception e) {
			System.out.println("------------Line = " + line);
			e.printStackTrace();
			return null;
		}
	}

	private ResultsInfosLine getResultsInfosLine(String line) {
		try {
			String[] parts = splitLine(line);
			ResultsInfosLine ril = new ResultsInfosLine();
			if (parts.length > 1) {
				ril.protocol = parts[1];
			}
			ril.complete = parts.length <= 5 || parts[5].toLowerCase().contains("c");
			if (parts.length > 3) {
				try {
					ril.demandDate = Instant.ofEpochMilli(shortDateFormat.parse(parts[3]).getTime());
				} catch (ParseException | NumberFormatException e) {
					log.error("Date {} could not be parsed", parts[3]);
					ril.demandDate = Instant.now();
				}
			}
			return ril;
		} catch (Exception e) {
			System.out.println("------------Line = " + line);
			e.printStackTrace();
			return null;
		}
	}

	private PatientSSINLine getPatientSSINLine(String line) {
		try {
			String[] parts = splitLine(line);
			PatientSSINLine psl = new PatientSSINLine();
			if (parts.length > 1) {
				psl.protocol = parts[1];
			}
			if (parts.length > 3) {
				psl.ssin = parts[3];
			}
			return psl;
		} catch (Exception e) {
			System.out.println("------------Line = " + line);
			e.printStackTrace();
			return null;
		}
	}

	private PatientAddressLine getPatientAddressLine(String line) {
		String[] parts = splitLine(line);
		PatientAddressLine pal = new PatientAddressLine();
		if (parts.length > 1) {
			pal.protocol = parts[1];
		}
		if (parts.length > 4) {
			pal.locality = parts[4];
		}
		if (parts.length > 3) {
			Matcher zipMatcher = zipCode.matcher(parts[3]);
			if (zipMatcher.matches()) {
				pal.zipCode = zipMatcher.group(1);
			}
		}
		if (parts.length > 2) {
			Matcher addressMatcher = address.matcher(parts[2]);
			if (addressMatcher.matches()) {
				pal.address = addressMatcher.group(1) == null ? addressMatcher.group(3) : addressMatcher.group(2);
				pal.number = addressMatcher.group(1) == null ? addressMatcher.group(4) : addressMatcher.group(1);
			} else {
				pal.address = parts[2];
			}
		}
		return pal;
	}

	@Override
	public void doExport(HealthcareParty sender, HealthcareParty recipient, Patient patient, LocalDateTime date, String ref, String text, OutputStream output) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(output, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}

		String namePat = patient.getLastName() != null ? patient.getLastName() : "";
		String firstPat = patient.getFirstName() != null ? patient.getFirstName() : "";
		String sexPat = patient.getGender() != null ? patient.getGender().getCode() : "";
		String birthPat = patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString().replaceAll("(....)(..)(..)","$3$2$1") : "";

		Optional<Address> a = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.home).findFirst();

		String addrPat3 = a.map(Address::getCity).orElse("");
		String addrPat2 = a.map(Address::getPostalCode).orElse("");
		String addrPat1 = a.map(Address::getStreet).orElse("");

		String inamiMed = sender.getNihii() != null ? sender.getNihii() : "";
		String nameMed = sender.getLastName() != null ? sender.getLastName() : "";
		String firstMed = sender.getFirstName() != null ? sender.getFirstName() : "";
		String dateAnal = date != null ? date.format(shortDateTimeFormatter) : "";
		String isFull = "C";

		namePat = namePat.replaceAll("\n", "").replaceAll("\r", "");
		firstPat = firstPat.replaceAll("\n", "").replaceAll("\r", "");
		sexPat = sexPat.replaceAll("\n", "").replaceAll("\r", "");
		birthPat = birthPat.replaceAll("\n", "").replaceAll("\r", "");
		addrPat3 = addrPat3.replaceAll("\n", "").replaceAll("\r", "");
		addrPat2 = addrPat2.replaceAll("\n", "").replaceAll("\r", "");
		addrPat1 = addrPat1.replaceAll("\n", "").replaceAll("\r", "");
		inamiMed = inamiMed.replaceAll("\n", "").replaceAll("\r", "");
		nameMed = nameMed.replaceAll("\n", "").replaceAll("\r", "");
		firstMed = firstMed.replaceAll("\n", "").replaceAll("\r", "");
		dateAnal = dateAnal.replaceAll("\n", "").replaceAll("\r", "");

		pw.print("A1\\" + ref + "\\" + inamiMed + " " + nameMed + " " + firstMed + "\\\r\n");
		pw.print("A2\\" + ref + "\\" + namePat + "\\" + firstPat + "\\" + sexPat + "\\" + birthPat + "\\\r\n");
		pw.print("A3\\" + ref + "\\" + addrPat1 + "\\" + addrPat2 + "\\" + addrPat3 + "\\\r\n");
		pw.print("A4\\" + ref + "\\" + inamiMed + " " + nameMed + " " + firstMed + "\\" + dateAnal + "\\" + isFull + "\\\r\n");

		for (String line : text.replaceAll("\u2028", "\n").split("\n")) {
			pw.print("L5\\" + ref + "\\DIVER\\\\\\\\\\" + line + "\\\r\n");
		}
		pw.flush();
	}


	public String[] splitLine(String line) {
		Matcher m = headerCompiledPrefix.matcher(line);

		if (m.matches()) {
			List<String> l = new ArrayList<String>();
			l.add(m.group(2));
			l.add(m.group(1));
			l.addAll(Arrays.asList(m.group(3).split("\\\\|\\|", -1)));

			return l.toArray(new String[l.size()]);
		} else {
			return line.split("\\\\|\\|", -1);
		}
	}

	@Override
	public boolean canHandle(Document doc) throws IOException {
		BufferedReader br = getBufferedReader(doc);

		String firstLine = br.readLine();
		br.close();

		return firstLine != null && this.isLaboLine(firstLine);
	}

}
