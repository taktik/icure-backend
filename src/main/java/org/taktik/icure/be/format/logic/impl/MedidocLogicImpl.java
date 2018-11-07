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
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.format.logic.MedidocLogic;
import org.taktik.icure.dto.result.ResultInfo;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.embed.Address;
import org.taktik.icure.entities.embed.AddressType;
import org.taktik.icure.entities.embed.Content;
import org.taktik.icure.entities.embed.Service;
import org.taktik.icure.entities.embed.Telecom;
import org.taktik.icure.entities.embed.TelecomType;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.ContactLogic;
import org.taktik.icure.utils.FuzzyValues;

@org.springframework.stereotype.Service
public class MedidocLogicImpl extends GenericResultFormatLogicImpl implements MedidocLogic {
	private final Pattern p1 = Pattern.compile("^#A.*$");
	private final Pattern p2 = Pattern.compile("^#R[a-zA-Z]*\\s*$");
	private final Pattern p3 = Pattern.compile("^#A/\\s*$");
	private final Pattern p4 = Pattern.compile("^#R/\\s*$");
	private final Pattern p5 = Pattern.compile("^#/[0-9]*\\s*$");
	private final DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
	private final DateFormat idf = new SimpleDateFormat("ddMMyyyy");
	private final DateFormat sidf = new SimpleDateFormat("ddMMyy");
	private final Pattern onlyNumbersAndPercentSigns = Pattern.compile("^[0-9%]+$");

	protected ContactLogic contactLogic;

	@Autowired
	public void setContactLogic(ContactLogic contactLogic) {
		this.contactLogic = contactLogic;
	}

	@Override
	public boolean canHandle(Document doc) throws IOException {
		boolean hasAHash = false, hasAHashSlash = false, hasRHash = false, hasRHashSlash = false, hasFinalTag = false;

		String text = decodeRawData(doc.getAttachment());
		if (text != null) {
			BufferedReader reader = new BufferedReader(new StringReader(text));
			String line;
			while ((line = reader.readLine()) != null) {
				if (p1.matcher(line).matches()) {
					hasAHash = true;
				}
				if (p2.matcher(line).matches()) {
					hasRHash = true;
				}
				if (p3.matcher(line).matches() && hasAHash) {
					hasAHashSlash = true;
				}
				if (p4.matcher(line).matches() && hasRHash) {
					hasRHashSlash = true;
				}
				if (p5.matcher(line).matches()) {
					hasFinalTag = true;
				}
			}
		}
		return hasAHash && hasAHashSlash && hasRHash && hasRHashSlash && hasFinalTag;
	}

	@Override
	public List<ResultInfo> getInfos(Document doc) throws IOException {
		List<ResultInfo> l = new ArrayList<>();
		BufferedReader br = getBufferedReader(doc);
		List<String> lines = IOUtils.readLines(br);
		String labo = lines.get(1).replaceAll("  +", " ");

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (p1.matcher(line).matches() && !p3.matcher(line).matches() && (i + 9 < lines.size())) {
				ResultInfo ri = new ResultInfo();

				ri.getCodes().add(new Code("CD-TRANSACTION", "report", "1"));

				ri.setLabo(labo);
				ri.setDocumentId(doc.getId());

				ri.setLastName(lines.get(i + 1).substring(0, 24).trim());
				ri.setFirstName(lines.get(i + 1).substring(24).trim());

				String birthDateLine = lines.get(i + 2).trim();

				//The examples we got do NOT respect the format at all
				//There seems to be one common variant where address and NISS
				//come before the birth date.
				boolean isStandardFormat = onlyNumbersAndPercentSigns.matcher(lines.get(i + 2).trim()).matches();
				if (!isStandardFormat) {
					birthDateLine = lines.get(i + 5).trim();
				}

				try {
					//noinspection ConstantConditions
					ri.setDateOfBirth(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(parseDate(birthDateLine).getTime()), ZoneId.systemDefault()), ChronoUnit.DAYS));
				} catch (ParseException | NullPointerException ignored) {
				}

				String gender = lines.get(isStandardFormat?i + 3:i+6).trim().toUpperCase();
				if (gender.equals("X") || gender.equals("Y")) {
					ri.setSex(gender.equals("X") ? "F" : "M");
				}

				Date demandDate = getResultDate(lines, i, isStandardFormat);
				if (demandDate!=null) { ri.setDemandDate(demandDate.getTime()); }
				String code = getProtocolCode(lines, i, isStandardFormat, demandDate);

				ri.setProtocol(code);
				i += isStandardFormat?6:9;
				l.add(ri);
			}
		}
		return l;
	}

	private String getProtocolCode(List<String> lines, int i, boolean isStandardFormat, Date demandDate) {
		String code = null;
		if (isStandardFormat) {
				try {
				//noinspection ConstantConditions
					Date date = parseDate(lines.get(i + 2).trim());
					code = computeProtocolCode(lines.get(i + 1).substring(0, Math.min(24,lines.get(i + 1).length())).trim(),
						lines.get(i + 1).length()>24?lines.get(i + 1).substring(24).trim():"",
						date != null ? date.getTime() : System.currentTimeMillis(),
						demandDate.getTime(),
						lines.get(i + 5));
			} catch (ParseException | NullPointerException e) {
				e.printStackTrace();
			}
		} else {
			try {
				//noinspection ConstantConditions
				code = computeProtocolCode(lines.get(i + 1).substring(0, 24).trim(),
						lines.get(i + 1).substring(24).trim(),
						parseDate(lines.get(i + 5).trim()).getTime(),
						demandDate.getTime(),
						lines.get(i + 8));
			} catch (ParseException | NullPointerException e) {
				e.printStackTrace();
			}
		}
		return code;
	}

	private Date getResultDate(List<String> lines, int i, boolean isStandardFormat) {
		Date demandDate = null;
		if (isStandardFormat) {
			//noinspection Duplicates
			try {
				demandDate = parseDate(lines.get(i + 4).trim());
			} catch (ParseException ignored) {
			}
		} else {
			try {
				demandDate = parseDate(lines.get(i + 7).trim());
			} catch (ParseException ignored) {
			}
		}
		return demandDate;
	}

	@Override
	public Contact doImport(String language, Document doc, String hcpId, List<String> protocolIds, List<String> formIds, String planOfActionId, Contact ctc) throws IOException {
		BufferedReader br = getBufferedReader(doc);
		List<String> lines = IOUtils.readLines(br);
		List<LaboLine> lls = new ArrayList<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (p1.matcher(line).matches() && !p3.matcher(line).matches() && (i + 9 < lines.size())) {
				//The examples we got do NOT respect the format at all
				//There seems to be one common variant where address and NISS
				//come before the birth date.
				boolean isStandardFormat = onlyNumbersAndPercentSigns.matcher(lines.get(i + 2).trim()).matches();

				Date demandDate = getResultDate(lines, i, isStandardFormat);
				String code = getProtocolCode(lines, i, isStandardFormat, demandDate);
				i += isStandardFormat?6:9;

				if (protocolIds.contains(code) || (protocolIds.size() == 1 && protocolIds.get(0) != null && protocolIds.get(0).startsWith("***"))) {
					do {
						i++;
					} while (!p2.matcher(lines.get(i)).matches());
					//Skip p2 and first empty line
					i += 2;

					StringBuilder b = new StringBuilder();
					while (!p4.matcher(lines.get(i)).matches()) {
						b.append(lines.get(i)).append("\n");
						i++;
					}

					String labo = lines.get(1).replaceAll("  +", " ");

					LaboLine ll = new LaboLine();
					lls.add(ll);

					Service s = new Service();
					s.setId(uuidGen.newGUID().toString());
					s.getContent().put(language, new Content(b.toString()));
					s.setLabel("Protocol");
					s.setValueDate(FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(demandDate != null ? Instant.ofEpochMilli(demandDate.getTime()) : Instant.now(), ZoneId.systemDefault()), ChronoUnit.DAYS));

					ll.setServices(Collections.singletonList(s));
					ll.setResultReference(code);
					ll.setLabo(labo);

				}
			}
		}
		fillContactWithLines(ctc, lls, planOfActionId, hcpId, protocolIds, formIds);

		try {
			return contactLogic.modifyContact(ctc);
		} catch (MissingRequirementsException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void doExport(HealthcareParty sender, HealthcareParty recipient, Patient patient, LocalDateTime date, String ref, String text, OutputStream output) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(output, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}

		if (sender.getNihii() != null && recipient.getNihii() != null) {
			//1
			pw.print((sender.getNihii().replaceAll("([0-9])([0-9]{5})([0-9][0-9])([0-9][0-9][0-9])",
					"$1/$2/$3/$4")).replaceAll("[\\r\\n]", "") + "\r\n");
			//2
			pw.print((StringUtils.rightPad(StringUtils.substring(sender.getLastName(), 0, 24), 24) +
					StringUtils.rightPad(StringUtils.substring(sender.getFirstName() == null ? "" : sender.getFirstName(), 0, 16), 16)).replaceAll("[\\r\\n]", "") + "\r\n");

			Optional<Address> senderAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.work).findFirst();
			if (!senderAddress.isPresent()) {
				senderAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.clinic).findFirst();
			}
			if (!senderAddress.isPresent()) {
				senderAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.hospital).findFirst();
			}
			if (!senderAddress.isPresent()) {
				senderAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.hq).findFirst();
			}
			if (!senderAddress.isPresent()) {
				senderAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.other).findFirst();
			}
			if (!senderAddress.isPresent()) {
				senderAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.home).findFirst();
			}

			//3
			pw.print((StringUtils.rightPad(StringUtils.substring(senderAddress.map(Address::getStreet).orElse(""), 0, 35), 35) +
					StringUtils.rightPad(StringUtils.substring(senderAddress.map(Address::getHouseNumber).orElse(""), 0, 10), 10)).replaceAll("[\\r\\n]", "") + "\r\n");

			//4
			pw.print((StringUtils.rightPad(StringUtils.substring(senderAddress.map(Address::getPostalCode).orElse(""), 0, 10), 10) +
					StringUtils.rightPad(StringUtils.substring(senderAddress.map(Address::getCity).orElse(""), 0, 35), 35)).replaceAll("[\\r\\n]", "") + "\r\n");

			Set<Telecom> senderTelecoms = senderAddress.map(Address::getTelecoms).orElse(new HashSet<>());
			Optional<Telecom> senderPhone = senderTelecoms.stream().filter(t -> t.getTelecomType() == TelecomType.phone).findFirst();
			if (!senderPhone.isPresent()) {
				senderPhone = senderTelecoms.stream().filter(t -> t.getTelecomType() == TelecomType.mobile).findFirst();
			}
			Optional<Telecom> senderFax = senderTelecoms.stream().filter(t -> t.getTelecomType() == TelecomType.fax).findFirst();
			//5
			pw.print((StringUtils.rightPad(StringUtils.substring(senderPhone.map(Telecom::getTelecomNumber).orElse(""), 0, 25), 25) +
					StringUtils.rightPad(StringUtils.substring(senderPhone.map(Telecom::getTelecomNumber).orElse(""), 0, 25), 25)).replaceAll("[\\r\\n]", "") + "\r\n");

			//6
			pw.print("\r\n");
			//7
			pw.print(df.format(new Date()) + "\r\n");

			//8
			pw.print((recipient.getNihii().replaceAll("([0-9])([0-9]{5})([0-9][0-9])([0-9][0-9][0-9])",
					"$1/$2/$3/$4")).replaceAll("[\\r\\n]", "") + "\r\n");
			//9
			pw.print((StringUtils.rightPad(StringUtils.substring(recipient.getLastName(), 0, 24), 24) +
					StringUtils.rightPad(StringUtils.substring(recipient.getFirstName() == null ? "" : recipient.getFirstName(), 0, 16), 16)).replaceAll("[\\r\\n]", "") + "\r\n");


			pw.print("#A" + (Strings.isNullOrEmpty(patient.getSsin()) ? "" : patient.getSsin()) + "\r\n");
			//2
			pw.print((StringUtils.rightPad(StringUtils.substring(patient.getLastName(), 0, 24), 24) +
					StringUtils.rightPad(StringUtils.substring(patient.getFirstName() == null ? "" : patient.getFirstName(), 0, 16), 16)).replaceAll("[\\r\\n]", "") + "\r\n");

			//3
			pw.print(patient.getDateOfBirth().toString() + "\r\n");
			//4
			pw.print((patient.getGender() == null ? "Z" : (patient.getGender().getCode().equals("F") ? "X" : "Y")) + "\r\n");

			pw.print(date.format(dtf) + "\r\n");
			pw.print(ref.replaceAll("-", "").substring(0, 14) + "\r\n");
			pw.print("C\r\n");

			Optional<Address> patientAddress = patient.getAddresses().stream().filter(ad -> ad.getAddressType() == AddressType.home).findFirst();

			pw.print((StringUtils.rightPad(StringUtils.substring(patientAddress.map(Address::getStreet).orElse(""), 0, 24), 24) +
					StringUtils.rightPad(StringUtils.substring(patientAddress.map(Address::getHouseNumber).orElse(""), 0, 7), 7)).replaceAll("[\\r\\n]", "") + "\r\n");

			pw.print((StringUtils.rightPad(StringUtils.substring(patientAddress.map(Address::getPostalCode).orElse(""), 0, 7), 7)).replaceAll("[\\r\\n]", ""));
			pw.print((StringUtils.rightPad(StringUtils.substring(patientAddress.map(Address::getCity).orElse(""), 0, 24), 24)).replaceAll("[\\r\\n]", "") + "\r\n");

			pw.print("#Rb\r\n");
			pw.print("!Protocole\r\n");
			pw.print("\r\n");

			pw.print(text.replaceAll("\u2028", "\n").replaceAll("\n", "\r\n") + "\r\n");

			pw.print("#R/\r\n");
			pw.print("#A/\r\n");
			pw.print("#/\r\n");
		}
		pw.flush();
	}

	private String computeProtocolCode(String name, String first, Long birth, Long req, String code) {
		return "" + StringUtils.substring(name.replaceAll(" ", ""), 0, 16)
				+ StringUtils.substring(first.replaceAll(" ", ""), 0, 8)
				+ ((int) (birth / (1000 * 3600 * 24)))
				+ ((int) (req / (1000 * 3600 * 24)))
				+ StringUtils.substring(code, 0, 20);
	}

	private Date parseDate(String dateString) throws ParseException {
		if (dateString.contains("%")) {
			return null;
		}
		if (dateString.startsWith("0000")) {
			return null;
		}
		if (dateString.length() < 6) {
			return null;
		}
		if (dateString.length() < 8) {
			if (Integer.parseInt(dateString.substring(4, 6)) > 31) {
				return sidf.parse(dateString);
			} else {
				if (Integer.parseInt(dateString.substring(0, 2)) < 18) {
					return df.parse("20" + dateString);
				} else {
					return df.parse("19" + dateString);
				}
			}
		}

		if (Integer.parseInt(dateString.substring(4, 8)) > 1300) {
			//Last digits are a year. Let's guess a ddMMyyyy
			return idf.parse(dateString);
		} else {
			//You won't believe it... It follows the doc
			return df.parse(dateString);
		}
	}
}
