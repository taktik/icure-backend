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

package org.taktik.icure.be.ehealth.logic.ehealthbox.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import javax.security.auth.login.LoginException;

import be.ehealth.businessconnector.ehbox.api.domain.Addressee;
import be.ehealth.businessconnector.ehbox.api.domain.Document;
import be.ehealth.businessconnector.ehbox.api.domain.DocumentMessage;
import be.ehealth.businessconnector.ehbox.api.domain.Message;
import be.ehealth.businessconnector.ehbox.api.domain.NewsMessage;
import be.ehealth.businessconnector.ehbox.api.domain.exception.EhboxBusinessConnectorException;
import be.ehealth.businessconnector.ehbox.v3.session.EhealthBoxServiceV3;
import be.ehealth.businessconnector.ehboxV2.builders.BuilderFactory;
import be.ehealth.businessconnector.ehboxV2.builders.SendMessageBuilder;
import be.ehealth.businessconnector.ehboxV2.session.EhealthBoxServiceV2;
import be.ehealth.businessconnector.ehboxV2.session.ServiceFactory;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.exception.UnsealConnectorException;
import be.fgov.ehealth.commons.core.v1.StatusType;
import be.fgov.ehealth.commons.protocol.v1.ResponseType;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.DeleteMessageRequest;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.GetBoxInfoRequest;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.GetBoxInfoResponse;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.GetFullMessageResponse;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.GetMessageListResponseType;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.GetMessagesListRequest;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.MessageRequestType;
import be.fgov.ehealth.ehbox.consultation.protocol.v2.MoveMessageRequest;
import be.fgov.ehealth.ehbox.publication.protocol.v2.SendMessageRequest;
import be.fgov.ehealth.ehbox.publication.protocol.v2.SendMessageResponse;
import com.google.common.collect.Lists;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.common.BoxInfo;
import org.taktik.icure.be.ehealth.logic.ehealthbox.EhealthBoxLogic;
import org.taktik.icure.be.ehealth.logic.sts.STSLogic;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.MainLogic;
import org.taktik.icure.logic.MessageLogic;

import static org.taktik.icure.be.ehealth.dto.common.IdentifierType.NIHII;
import static org.taktik.icure.be.ehealth.dto.common.IdentifierType.NIHII11;
import static org.taktik.icure.be.ehealth.dto.common.IdentifierType.SSIN;

/**
 * Created by aduchate on 01/06/13, 12:14
 */
@org.springframework.stereotype.Service
public class EhealthBoxLogicImpl implements EhealthBoxLogic {
	public static final int PUBLICATION_RECEIPT_MASK = 1 << 0;
	public static final int RECEIVED_RECEIPT_MASK = 1 << 1;
	public static final int READ_RECEIPT_MASK = 1 << 2;

	Log log = LogFactory.getLog(this.getClass());

	protected STSLogic stsLogic;
	protected MapperFacade mapper;
	protected MessageLogic messageLogic;
	protected HealthcarePartyLogic healthcarePartyLogic;
	protected MainLogic mainLogic;
	private UUIDGenerator uuidGenerator;

	@Autowired
	public void setMainLogic(MainLogic mainLogic) {
		this.mainLogic = mainLogic;
	}

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Autowired
	public void setMessageLogic(MessageLogic messageLogic) {
		this.messageLogic = messageLogic;
	}

	@Autowired
	public void setMapper(MapperFacade mapper) {
		this.mapper = mapper;
	}

	@Autowired
	public void setStsLogic(STSLogic stsLogic) {
		this.stsLogic = stsLogic;
	}

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	@Override
	public BoxInfo getInfos(String token) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException {
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		EhealthBoxServiceV2 serviceV2 = ServiceFactory.getEhealthBoxServiceV2();
		GetBoxInfoRequest infoRequest = new GetBoxInfoRequest();
		GetBoxInfoResponse response = serviceV2.getBoxInfo(infoRequest);

		BoxInfo boxInfo = new BoxInfo();

		boxInfo.setBoxId(response.getBoxId().getId());
		boxInfo.setQuality(response.getBoxId().getQuality());
		boxInfo.setCurrentSize(response.getCurrentSize());
		boxInfo.setNbrMessagesInStandBy(response.getNbrMessagesInStandBy());
		boxInfo.setMaxSize(response.getMaxSize());

		return boxInfo;
	}

	@Override
	public List<org.taktik.icure.be.ehealth.dto.common.Message> getMessagesList(String token, String box) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException {
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		EhealthBoxServiceV2 serviceV2 = ServiceFactory.getEhealthBoxServiceV2();

		GetMessagesListRequest messagesListRequest = new GetMessagesListRequest();
		messagesListRequest.setSource(box);
		messagesListRequest.setStartIndex(1);
		messagesListRequest.setEndIndex(100);

		List<org.taktik.icure.be.ehealth.dto.common.Message> result = new ArrayList<>();

		while(true) {
			GetMessageListResponseType response = serviceV2.getMessageList(messagesListRequest);
			List<be.fgov.ehealth.ehbox.consultation.protocol.v2.Message> messages = response.getMessages();
			for (be.fgov.ehealth.ehbox.consultation.protocol.v2.Message m : messages) {
				try {
					Message msg = BuilderFactory.getConsultationMessageBuilder().buildMessage(m);
					org.taktik.icure.be.ehealth.dto.common.Message messageDto = getGenericMessage(msg);
					result.add(messageDto);
				} catch (UnsealConnectorException e) {
					//Skip
				}
			}
			if (messages.size()<100) { break; }

			messagesListRequest.setStartIndex(messagesListRequest.getStartIndex()+100);
			messagesListRequest.setEndIndex(messagesListRequest.getEndIndex()+100);
		}
		return result;
	}

	@Override
	public org.taktik.icure.be.ehealth.dto.common.Message getFullMessage(String token, String source, String messageId) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException, MessageDeletedException {
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		EhealthBoxServiceV2 serviceV2 = ServiceFactory.getEhealthBoxServiceV2();

		MessageRequestType messageRequest = new MessageRequestType();
		messageRequest.setMessageId(messageId);
		messageRequest.setSource(source);
		GetFullMessageResponse fullMessage;
		try {
			fullMessage = serviceV2.getFullMessage(messageRequest);
		} catch (be.ehealth.businessconnector.ehbox.api.domain.exception.EhboxBusinessConnectorException e) {
			if ("806".equals(e.getErrorCode())) {
				throw new MessageDeletedException(e.getMessage());
			}
			throw new IllegalStateException(e);
		}

		Message message = BuilderFactory.getConsultationMessageBuilder().buildFullMessage(fullMessage);

		org.taktik.icure.be.ehealth.dto.common.Message msg = getGenericMessage(message);

		if (msg instanceof org.taktik.icure.be.ehealth.dto.common.NewsMessage) {
			org.taktik.icure.be.ehealth.dto.common.Document newsMsgDoc = ((org.taktik.icure.be.ehealth.dto.common.NewsMessage) msg).getNews();

			List<org.taktik.icure.be.ehealth.dto.common.Message> messagesList = getMessagesList(token, source);
			if (newsMsgDoc != null && (newsMsgDoc.getMimeType() == null || newsMsgDoc.getFilename() == null)) {
				for (org.taktik.icure.be.ehealth.dto.common.Message m : messagesList) {
					if (m instanceof org.taktik.icure.be.ehealth.dto.common.NewsMessage) {
						org.taktik.icure.be.ehealth.dto.common.Document news = ((org.taktik.icure.be.ehealth.dto.common.NewsMessage) m).getNews();
						if (news != null) {
							if (newsMsgDoc.getMimeType() == null) {
								newsMsgDoc.setMimeType(news.getMimeType());
							}
							if (newsMsgDoc.getFilename() == null) {
								newsMsgDoc.setFilename(news.getFilename());
							}
						}
					}
				}
			}
		}

		return msg;
	}

	private org.taktik.icure.be.ehealth.dto.common.Message getGenericMessage(Message message) throws UnsealConnectorException {
		if (message instanceof NewsMessage) {
			return getNewsMessage((NewsMessage) message);
		} else if (message instanceof DocumentMessage) {
			return getDocumentMessage((DocumentMessage) message);
		}

		org.taktik.icure.be.ehealth.dto.common.Message msg = new org.taktik.icure.be.ehealth.dto.common.Message();
		fillMessage(msg, message);

		return msg;
	}

	private org.taktik.icure.be.ehealth.dto.common.NewsMessage getNewsMessage(NewsMessage message) throws UnsealConnectorException {
		org.taktik.icure.be.ehealth.dto.common.NewsMessage result = new org.taktik.icure.be.ehealth.dto.common.NewsMessage();

		result.setNews(getDocument(message.getNews()));

		fillMessage(result, message);
		return result;
	}

	private org.taktik.icure.be.ehealth.dto.common.DocumentMessage getDocumentMessage(DocumentMessage message) throws UnsealConnectorException {
		org.taktik.icure.be.ehealth.dto.common.DocumentMessage result = new org.taktik.icure.be.ehealth.dto.common.DocumentMessage();

		result.setDocument(getDocument(message.getDocument()));
		result.setFreeText(message.getFreeText());
		result.setPatientInss(message.getPatientInss());

		List<org.taktik.icure.be.ehealth.dto.common.Document> ehbDocuments = new ArrayList<org.taktik.icure.be.ehealth.dto.common.Document>();
		for (Document al : (List<Document>) message.getAnnexList()) {
			ehbDocuments.add(getDocument(al));
		}
		result.setAnnex(ehbDocuments);
		fillMessage(result, message);

		return result;
	}

	private void fillMessage(org.taktik.icure.be.ehealth.dto.common.Message result, Message message) {
		result.setId(message.getId());

		result.setCustomMetas(message.getCustomMetas());
		result.setDestinations(((List<Addressee>) message.getDestinations()).stream().map(this::getAddressee).collect(Collectors.toList()));
		result.setEncrypted(message.isEncrypted());
		result.setExpirationDate(message.getExpirationDateTime()==null?null:Instant.ofEpochMilli(message.getExpirationDateTime().toInstant().getMillis()));
		result.setHasFreeInformations(message.isHasFreeInformations());
		result.setImportant(message.isImportant());
		result.setMandatee(getAddressee(message.getMandatee()));
		result.setHasAnnex(message.isHasAnnex());
		result.setPublicationDate(message.getPublicationDateTime()==null?null:Instant.ofEpochMilli(message.getPublicationDateTime().toInstant().getMillis()));
		result.setPublicationId(message.getPublicationId());
		result.setSender(getAddressee(message.getSender()));
	}

	private org.taktik.icure.be.ehealth.dto.common.Addressee getAddressee(Addressee addressee) {
		if (addressee == null) {
			return null;
		}
		try {
			if (addressee.getId() == null) {
				return null;
			}
		} catch (NullPointerException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}

		return mapper.map(addressee, org.taktik.icure.be.ehealth.dto.common.Addressee.class);
	}

	private Addressee getAddressee(org.taktik.icure.be.ehealth.dto.common.Addressee addressee) {
		if (addressee == null) {
			return null;
		}
		Addressee result = null;
		try {
			if (addressee.getId() == null) {
				return null;
			}
			if (addressee.getIdentifierType() == null) {
				return null;
			}

			result = new Addressee(addressee.getIdentifierType().toEhType());
			result.setId(addressee.getId());
			result.setQuality(addressee.getQuality());
			result.setFirstName(addressee.getFirstName());
			result.setLastName(addressee.getLastName());
			result.setOrganizationName(addressee.getOrganizationName());
			result.setApplicationId(addressee.getApplicationId() == null && addressee.getIdentifierType().equals(SSIN) ? "" : addressee.getApplicationId());
		} catch (NullPointerException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}

		return result;
	}


	private org.taktik.icure.be.ehealth.dto.common.Document getDocument(Document d) throws UnsealConnectorException {
		if (d == null) {
			return null;
		}

		org.taktik.icure.be.ehealth.dto.common.Document document = new org.taktik.icure.be.ehealth.dto.common.Document();


		byte[] bytes = null;
		try {
			bytes = d.getContent() == null ? null : new byte[d.getContent().length];
		} catch (NullPointerException e) {

		}

		if (bytes != null) {
			int i = 0;
			for (Byte b : d.getContent()) {
				bytes[i++] = b;
			}

			String textContent = null;
			if (d.getMimeType() != null && d.getMimeType().equals("text/plain")) {
				try {
					textContent = new String(bytes, "UTF8");
				} catch (UnsupportedEncodingException e) {
					log.info("Couldn't convert text/plain message to String due to UnsupportedEncodingException");
				}
			}
			if (textContent != null) {
				document.setTextContent(textContent);
			} else {
				document.setContent(bytes);
			}
		}

		document.setTitle(d.getTitle());
		document.setMimeType(d.getMimeType());
		document.setSigning(d.getSigning());
		document.setFilename(d.getFilename());
		return document;
	}


	private Document getDocument(org.taktik.icure.be.ehealth.dto.common.Document d) {
		if (d == null) {
			return null;
		}

		Document document = new Document();

		document.setFilename(StringUtils.isEmpty(d.getFilename()) ? UUID.randomUUID().toString() : FilenameUtils.getName(d.getFilename()));
		document.setTitle(StringUtils.isEmpty(d.getTitle()) ? document.getFilename() : d.getTitle());
		document.setMimeType(d.getMimeType());
		document.setSigning(d.getSigning());

		byte[] fromBytes = d.getContent();
		if (d.getTextContent() != null && fromBytes == null) {
			if (document.getMimeType() == null) {
				document.setMimeType("text/plain");
			}
			try {
				fromBytes = d.getTextContent().getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				log.info("Couldn't convert text/plain message to byte[] due to UnsupportedEncodingException");
			}
		}

		if (!StringUtils.isEmpty(d.getFilename()) && fromBytes == null) {
			File f = new File(d.getFilename());
			if (f.exists()) {
				InputStream is = null;
				try {
					is = new BufferedInputStream(new FileInputStream(f));
					fromBytes = IOUtils.toByteArray(is);
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Byte[] bytes = fromBytes == null ? new Byte[0] : new Byte[fromBytes.length];

		if (fromBytes != null) {
			int i = 0;
			for (byte b : fromBytes) {
				bytes[i++] = b;
			}
		}

		document.setContent(bytes);
		if (StringUtils.isEmpty(document.getMimeType())) {
			document.setMimeType("application/binary");
		}

		return document;
	}

	@Override
	public void sendMessage(String token, org.taktik.icure.be.ehealth.dto.common.DocumentMessage message, int notificationMask) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException, IOException, CMSException, BusinessConnectorException {
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		EhealthBoxServiceV2 serviceV2 = ServiceFactory.getEhealthBoxServiceV2();

		SendMessageBuilder builder = BuilderFactory.getSendMessageBuilder();

		List<Addressee> addresseeList = new ArrayList<Addressee>();
		for (org.taktik.icure.be.ehealth.dto.common.Addressee id : message.getDestinations()) {
			addresseeList.add(getAddressee(id));
		}
		DocumentMessage documentMsg = new DocumentMessage();

		documentMsg.setSender(getAddressee(message.getSender()));
		documentMsg.setDestinations(addresseeList);
		documentMsg.setImportant(message.isImportant());
		documentMsg.setEncrypted(message.isEncrypted());
		documentMsg.setFreeText(message.getFreeText());
		documentMsg.setPatientInss(message.getPatientInss());

		if (message.getCustomMetas() != null) {
			for (Map.Entry<String, String> e : message.getCustomMetas().entrySet()) {
				documentMsg.getCustomMetas().put(e.getKey(), e.getValue());
			}
		}

		documentMsg.setDocument(getDocument(message.getDocument()));

		if (message.getAnnex() != null) {
			for (org.taktik.icure.be.ehealth.dto.common.Document annex : message.getAnnex()) {
				documentMsg.getAnnexList().add(getDocument(annex));
			}
		}
		SendMessageRequest request = builder.buildMessage(documentMsg);

		request.getContentContext().getContentSpecification().setPublicationReceipt((notificationMask & PUBLICATION_RECEIPT_MASK) == PUBLICATION_RECEIPT_MASK);
		request.getContentContext().getContentSpecification().setReceivedReceipt((notificationMask & RECEIVED_RECEIPT_MASK) == RECEIVED_RECEIPT_MASK);
		request.getContentContext().getContentSpecification().setPublicationReceipt((notificationMask & READ_RECEIPT_MASK) == READ_RECEIPT_MASK);

		EhealthBoxServiceV2 service = ServiceFactory.getEhealthBoxServiceV2();
		SendMessageResponse response = service.sendMessage(request);

		StatusType status = response.getStatus();

		if (!status.getCode().equals("100")) {
			throw new BusinessConnectorException(status.getMessages().toString(), status.getCode());
		}
	}

	@Override
	public List<org.taktik.icure.entities.Message> loadMessages(String token, String userId, String hcpId, String boxId, Integer limit, List<String> spamFromAddresses) throws TechnicalConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, EhboxBusinessConnectorException, LoginException, CreationException, MissingRequirementsException {
		synchronized (this) {
			List<org.taktik.icure.be.ehealth.dto.common.Message> messages = getMessagesList(token, boxId);

			ArrayList<org.taktik.icure.entities.Message> result = new ArrayList<>();

			if (messages.size() == 0) {
				return result;
			}

			HashMap<String, org.taktik.icure.be.ehealth.dto.common.Message> messagesMap = new HashMap<>(messages.size());
			for (org.taktik.icure.be.ehealth.dto.common.Message m : messages) {
				messagesMap.put(boxId + ":" + m.getId(), m);
			}

			List<org.taktik.icure.entities.Message> messagesWithTransportGuids = messageLogic.getByTransportGuids(hcpId, messagesMap.keySet());
			List<String> skippedMessagesToDelete = new ArrayList<>();

			Instant now = Instant.now();

			for (org.taktik.icure.entities.Message m : messagesWithTransportGuids) {
				messagesMap.remove(m.getTransportGuid());
				if ((m.getStatus() & org.taktik.icure.entities.Message.STATUS_SHOULD_BE_DELETED_ON_SERVER) != 0 && ((m.getSent() != null && m.getSent() < (now.toEpochMilli() - 24 * 3_600_000L)) || (m.getReceived() != null && m.getReceived() < (now.toEpochMilli() - 24 * 3_600_000L)))) {
					String[] transportGuiParts = m.getTransportGuid().split(":");
					if (transportGuiParts.length > 1) {
						skippedMessagesToDelete.add(transportGuiParts[1]);
					}
				}
			}

			if (limit == null) {
				limit = Integer.MAX_VALUE;
			}


			List<org.taktik.icure.be.ehealth.dto.common.Message> oldestMessages = messagesMap.values().stream().sorted(Comparator.comparing(org.taktik.icure.be.ehealth.dto.common.Message::getPublicationDate)).limit(limit).collect(Collectors.toList());
			for (org.taktik.icure.be.ehealth.dto.common.Message m : oldestMessages) {
				if (spamFromAddresses.contains(m.getSender().toString())) {
					skippedMessagesToDelete.add(m.getId());
					continue;
				}
				org.taktik.icure.entities.Message mm = new org.taktik.icure.entities.Message();

				mm.setTransportGuid(boxId + ":" + m.getId());
				mm.setId(uuidGenerator.newGUID().toString());
				mm.setAuthor(userId);
				mm.setResponsible(hcpId);

				//Try to find a doctor for sender
				{
					HealthcareParty hcp = null;
					List<HealthcareParty> hcps = m.getSender().getIdentifierType() == SSIN ? healthcarePartyLogic.listBySsin(m.getSender().getId()) : healthcarePartyLogic.listByNihii(m.getSender().getId());

					if (hcps.size() == 0) {
						if ((m.getSender().getIdentifierType() == null || Arrays.asList(SSIN, NIHII, NIHII11).contains(m.getSender().getIdentifierType())) &&
							!StringUtils.isEmpty(m.getSender().getLastName())) {
							hcp = new HealthcareParty();

							hcp.setId(uuidGenerator.newGUID().toString());
							hcp.setFirstName(m.getSender().getFirstName());
							hcp.setLastName(m.getSender().getLastName());
							if (m.getSender().getIdentifierType() == SSIN) {
								hcp.setSsin(m.getSender().getId());
							} else {
								hcp.setNihii(m.getSender().getId());
							}
						}

						if (!StringUtils.isEmpty(m.getSender().getOrganizationName())) {
							hcp = new HealthcareParty();

							hcp.setId(uuidGenerator.newGUID().toString());
							hcp.setName(m.getSender().getLastName());
							if (m.getSender().getIdentifierType() == SSIN) {
								hcp.setSsin(m.getSender().getId());
							} else {
								hcp.setNihii(m.getSender().getId());
							}
						}
					} else {
						hcp = hcps.get(0);
					}
					if (hcp != null) {
						mm.setFromHealthcarePartyId(hcp.getId());
					}


					mm.setRecipients(Collections.singleton(hcpId));

					mm.setFromAddress(m.getSender().toString());
					mm.setToAddresses(Collections.singleton(boxId));

					mm.setReceived(System.currentTimeMillis());
					mm.setStatus(org.taktik.icure.entities.Message.STATUS_UNREAD);
					mm.setSent(m.getPublicationDate().toEpochMilli());
					if (m.isImportant()) {
						mm.setStatus(mm.getStatus() | org.taktik.icure.entities.Message.STATUS_IMPORTANT);
					}
					if (m.isHasAnnex()) {
						mm.setStatus(mm.getStatus() | org.taktik.icure.entities.Message.STATUS_HAS_ANNEX);
					}
					if (m.isEncrypted()) {
						mm.setStatus(mm.getStatus() | org.taktik.icure.entities.Message.STATUS_ENCRYPTED);
					}
					if (m.isHasFreeInformations()) {
						mm.setStatus(mm.getStatus() | org.taktik.icure.entities.Message.STATUS_HAS_FREE_INFORMATION);
					}

					if (m instanceof org.taktik.icure.be.ehealth.dto.common.DocumentMessage) {
						org.taktik.icure.be.ehealth.dto.common.DocumentMessage dm = (org.taktik.icure.be.ehealth.dto.common.DocumentMessage) m;
						mm.setSubject(dm.getDocument().getTitle());
						mm.setRemark(dm.getFreeText());
					} else if (m instanceof org.taktik.icure.be.ehealth.dto.common.NewsMessage) {
						org.taktik.icure.be.ehealth.dto.common.NewsMessage dm = (org.taktik.icure.be.ehealth.dto.common.NewsMessage) m;
						mm.setSubject(dm.getNews().getTitle());
						mm.setRemark(((org.taktik.icure.be.ehealth.dto.common.NewsMessage) m).getNews().getTextContent());
					}
					if (!m.getCustomMetas().isEmpty()) {
						mm.setMetas(m.getCustomMetas());
					}

					result.add(messageLogic.createMessage(mm));

				}
			}
			if (skippedMessagesToDelete.size() > 0) {
				try {
					deleteMessages(token, skippedMessagesToDelete, boxId);
				} catch (ConnectorException | BusinessConnectorException e) {
					log.warn("Some message could not be deleted: ", e);
				}
			}
			return result;
		}
	}

	@Override
	public void moveMessages(String token, List<String> messageIds, String source, String destination) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException, BusinessConnectorException {
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		EhealthBoxServiceV2 serviceV2 = ServiceFactory.getEhealthBoxServiceV2();

		MoveMessageRequest mmr = new MoveMessageRequest();
		mmr.setSource(source);
		mmr.setDestination(destination);

		mmr.getMessageIds().addAll(messageIds);

		ResponseType response = serviceV2.moveMessage(mmr);

		StatusType status = response.getStatus();
		if (!status.getCode().equals("100")) {
			throw new BusinessConnectorException(status.getMessages().toString(), status.getCode());
		}
	}

	@Override
	public void deleteMessages(String token, List<String> allMessageIds, String source) throws TokenNotAvailableException, ConnectorException, BusinessConnectorException {
		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		EhealthBoxServiceV3 serviceV3 = be.ehealth.businessconnector.ehbox.v3.session.ServiceFactory.getEhealthBoxServiceV3();

		be.fgov.ehealth.ehbox.consultation.protocol.v3.DeleteMessageRequest mmr = new be.fgov.ehealth.ehbox.consultation.protocol.v3.DeleteMessageRequest();
		mmr.setSource(source == null ? "INBOX" : source);

		for (List<String> messageIds : Lists.partition(allMessageIds, 100)) {
			mmr.getMessageIds().clear();
			mmr.getMessageIds().addAll(messageIds);

			ResponseType response;
			try {
				response = serviceV3.deleteMessage(mmr);
				StatusType status = response.getStatus();
				if (!status.getCode().equals("100")) {
					throw new BusinessConnectorException(status.getMessages().toString(), status.getCode());
				}
			} catch (Exception e) {
				String message = "Messages " + String.join(", ", messageIds) + " could not be deleted, trying ten by ten";
				log.warn(message);
				for (List<String> subMessageIds : Lists.partition(messageIds, 10)) {
					try {
						mmr.getMessageIds().clear();
						mmr.getMessageIds().addAll(messageIds);
						response = serviceV3.deleteMessage(mmr);
						StatusType status = response.getStatus();
						if (!status.getCode().equals("100")) {
							throw new BusinessConnectorException(status.getMessages().toString(), status.getCode());
						}
					} catch (Exception ee) {
						String subMessage = "Messages " + String.join(", ", subMessageIds) + " could not be deleted, trying one by one";
						log.warn(subMessage);
						for (String messageId : subMessageIds) {
							mmr.getMessageIds().clear();
							mmr.getMessageIds().add(messageId);
							try {
								response = serviceV3.deleteMessage(mmr);
								StatusType status = response.getStatus();
								if (!status.getCode().equals("100")) {
									throw new BusinessConnectorException(status.getMessages().toString(), status.getCode());
								}
							} catch (Exception eee) {
								String fatalMessage = "Fatal: Message " + messageId + " could not be deleted";
								log.error(fatalMessage);
							}
						}
					}
				}
			}
		}
	}
}
