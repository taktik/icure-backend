/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.efact.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import javax.security.auth.login.LoginException;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.soap.SOAPFaultException;

import be.cin.mycarenet.esb.common.v2.CommonInput;
import be.cin.nip.async.generic.GetResponse;
import be.cin.nip.async.generic.MsgQuery;
import be.cin.nip.async.generic.MsgResponse;
import be.cin.nip.async.generic.Post;
import be.cin.nip.async.generic.PostResponse;
import be.cin.nip.async.generic.Query;
import be.cin.nip.async.generic.TAck;
import be.cin.nip.async.generic.TAckResponse;
import be.ehealth.business.mycarenetcommons.builders.BlobBuilder;
import be.ehealth.business.mycarenetcommons.builders.RequestBuilderFactory;
import be.ehealth.business.mycarenetcommons.builders.util.BlobUtil;
import be.ehealth.business.mycarenetcommons.domain.Blob;
import be.ehealth.business.mycarenetcommons.mapper.SendRequestMapper;
import be.ehealth.business.mycarenetcommons.util.WsAddressingUtil;
import be.ehealth.businessconnector.genericasync.builders.BuilderFactory;
import be.ehealth.businessconnector.genericasync.builders.RequestObjectBuilder;
import be.ehealth.businessconnector.genericasync.exception.GenAsyncBusinessConnectorException;
import be.ehealth.businessconnector.genericasync.mappers.CommonInputMapper;
import be.ehealth.businessconnector.genericasync.session.GenAsyncService;
import be.ehealth.businessconnector.genericasync.session.GenAsyncSessionServiceFactory;
import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.Configuration;
import be.ehealth.technicalconnector.config.util.ConfigUtil;
import be.ehealth.technicalconnector.enumeration.Charset;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.handler.domain.WsAddressingHeader;
import be.ehealth.technicalconnector.idgenerator.IdGeneratorFactory;
import be.ehealth.technicalconnector.utils.ConnectorIOUtils;
import be.fgov.ehealth.mycarenet.commons.core.v2.BlobType;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.insurability.InsurabilityInfo;
import org.taktik.icure.be.ehealth.dto.insurability.InsurabilityItem;
import org.taktik.icure.be.ehealth.logic.efact.EfactLogic;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.BelgianInsuranceInvoicing;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.BelgianInsuranceInvoicingFormat;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.BelgianInsuranceInvoicingReader;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.EIDItem;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.Invoice;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoiceItem;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoiceSender;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicesBatch;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicingPercentNorm;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicingPrescriberCode;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicingSideCode;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicingTimeOfDay;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicingTreatmentReasonCode;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments.ErrorDetail;
import org.taktik.icure.be.ehealth.logic.generalinsurability.impl.GeneralInsurabilityLogicImpl;
import org.taktik.icure.dao.impl.idgenerators.IDGenerator;
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.entities.Message;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Address;
import org.taktik.icure.entities.embed.Delegation;
import org.taktik.icure.entities.embed.InvoicingCode;
import org.taktik.icure.entities.embed.Telecom;
import org.taktik.icure.entities.embed.TelecomType;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;
import org.taktik.icure.logic.DocumentLogic;
import org.taktik.icure.logic.HealthcarePartyLogic;
import org.taktik.icure.logic.InsuranceLogic;
import org.taktik.icure.logic.InvoiceLogic;
import org.taktik.icure.logic.MainLogic;
import org.taktik.icure.logic.MessageLogic;
import org.taktik.icure.logic.PatientLogic;
import org.taktik.icure.logic.SessionLogic;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
@org.springframework.stereotype.Service
public class EfactLogicImpl implements EfactLogic {
/* For support call:

	300: 02 515 17 00 - helpdesk.carenet@solidaris.be
	500: 02 778 93 27
	900: M. Moreels - 02 5254957

 */
	private Log log = LogFactory.getLog(this.getClass());
	private IDGenerator idg = new UUIDGenerator();

	private DocumentLogic documentLogic;
	private MessageLogic messageLogic;
	private InvoiceLogic invoiceLogic;
	private PatientLogic patientLogic;
	private InsuranceLogic insuranceLogic;
	private SessionLogic sessionLogic;
	private HealthcarePartyLogic healthcarePartyLogic;
	private UUIDGenerator uuidGenerator;
	private MainLogic mainLogic;

	private GeneralInsurabilityLogicImpl generalInsurabilityLogic;

	private TaskExecutor taskExecutor;

	private static final BigInteger LSB_MASK = new BigInteger("ffffffffffffffff", 16);

	{
		//For 3.11.1
		//WSSConfig.init();
	}


	private UUID decodeUuidFromRef(String val) {
		UUID uuid = null;
		if (val != null) {
			val = val.trim();
			if (val.length() > 0 && val.matches("[0-9a-zA-Z]+")) {
				BigInteger id = new BigInteger(val, 36);
				uuid = new UUID(id.shiftRight(64).longValue(), id.and(LSB_MASK).longValue());
			}
		}
		return uuid;
	}

	private String encodeRefFromUUID(UUID uuid) {
		java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		return new BigInteger(1, bb.array()).toString(36);
	}

	protected Long encodeNumberFromUUID(UUID uuid) {
		java.nio.ByteBuffer bb = java.nio.ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());

		return new BigInteger(1, Arrays.copyOfRange(bb.array(), 0, 4)).longValue();
	}


	private static List<String> expectedProps = new ArrayList<>();
	private static Configuration config = ConfigFactory.getConfigValidator(expectedProps);

	public List<org.taktik.icure.entities.Invoice> analyzeBelgianInsuranceInvoicing(BelgianInsuranceInvoicing belgianInsuranceInvoicing, Message msg) throws LoginException, MissingRequirementsException {
		if (belgianInsuranceInvoicing != null && belgianInsuranceInvoicing.getIdentificationFlux() != null) {
			Set<UUID> rejectedIcIds = new HashSet<>();
			boolean hasError = false;
			for (ErrorDetail ed : belgianInsuranceInvoicing.getErrorDetails()) {
				boolean error = (ed.getRejectionCode1() != null && !ed.getRejectionCode1().equals("000000") && !ed.getRejectionCode1().matches(" *0* *"))
						|| (ed.getRejectionCode2() != null && !ed.getRejectionCode2().equals("000000") && !ed.getRejectionCode2().matches(" *0* *"))
						|| (ed.getRejectionCode3() != null && !ed.getRejectionCode3().equals("000000") && !ed.getRejectionCode3().matches(" *0* *"));

				if (error && ed.getInvoiceRecord().getRecordNumber().equals("50")) {
					String val = ed.getInvoiceRecord().getZoneValue("28");
					UUID uuid = decodeUuidFromRef(val);
					if (uuid != null) {
						rejectedIcIds.add(uuid);
					}
				}
				hasError = hasError || error;
			}

			List<org.taktik.icure.entities.Invoice> invoices = new LinkedList<>();
			List<org.taktik.icure.entities.Invoice> reassignationInvoices = new LinkedList<>();

			String messageDescription = belgianInsuranceInvoicing.getIdentificationFlux().getMessageDescription() != null ? belgianInsuranceInvoicing.getIdentificationFlux().getMessageDescription() : (""+belgianInsuranceInvoicing.getIdentificationFlux().getMessageName());
			switch (belgianInsuranceInvoicing.getIdentificationFlux().getMessageName()) {
				case 920999:
					msg.setSubject(msg.getSubject() + " : " + messageDescription);
					reassignationInvoices = (invoices = rejectMessage(msg)).stream().map(i-> org.taktik.icure.entities.Invoice.reassignationInvoiceFromOtherInvoice(i, uuidGenerator)).collect(Collectors.toList());
					break;
				case 920099:
					msg.setSubject(msg.getSubject() + " : " + messageDescription);
					reassignationInvoices = (invoices = rejectMessage(msg)).stream().map(i-> org.taktik.icure.entities.Invoice.reassignationInvoiceFromOtherInvoice(i, uuidGenerator)).collect(Collectors.toList());
					break;
				case 920098:
					msg.setSubject(msg.getSubject() + " : " + messageDescription);
					acceptAndMaskMessage(msg, hasError);
					break;
				case 931000:
					msg.setSubject(msg.getSubject() + " : " + messageDescription);
					acceptAndMaskMessage(msg, false);
					break;
				case 920900:
					msg.setSubject(msg.getSubject() + " : " + messageDescription);
					if (hasError) {
						msg.setStatus(msg.getStatus() | Message.STATUS_WARNING);
					} else {
						msg.setStatus(msg.getStatus() | Message.STATUS_SUCCESS);
					}
					messageLogic.modifyMessage(msg);
					if (msg.getParentId() != null) {
						Message parent = messageLogic.get(msg.getParentId());
						parent.setStatus(parent.getStatus() | Message.STATUS_ACCEPTED);
						if (hasError) {
							parent.setStatus(parent.getStatus() | Message.STATUS_WARNING);
						} else {
							parent.setStatus(parent.getStatus() | Message.STATUS_SUCCESS);
						}
						messageLogic.modifyMessage(parent);
						if (parent.getParentId() != null) {
							Message parentParent = messageLogic.get(parent.getParentId());
							parentParent.setStatus(parentParent.getStatus() | Message.STATUS_ACCEPTED);
							if (hasError) {
								parentParent.setStatus(parentParent.getStatus() | Message.STATUS_WARNING);
							} else {
								parentParent.setStatus(parentParent.getStatus() | Message.STATUS_SUCCESS);
							}
							messageLogic.modifyMessage(parentParent);

							invoices = invoiceLogic.getInvoices(Optional.of(parentParent.getInvoiceIds()).orElse(new LinkedList<>()));
							for (org.taktik.icure.entities.Invoice iv : invoices) {
								List<InvoicingCode> reassignedCodes = new LinkedList<>();
								for (InvoicingCode ic : iv.getInvoicingCodes()) {
									ic.setPending(false);
									if (rejectedIcIds.contains(UUID.fromString(ic.getId()))) {
										ic.setCanceled(true);
										reassignedCodes.add(ic);
									} else {
										ic.setCanceled(false);
										ic.setPending(false);
										ic.setAccepted(true);
									}
								}
								if (reassignedCodes.size()>0) {
									reassignationInvoices.add(org.taktik.icure.entities.Invoice.reassignationInvoiceFromOtherInvoice(iv, reassignedCodes, uuidGenerator));
								}
							}
						}
					}
					break;
			}

			for (org.taktik.icure.entities.Invoice iv : invoices) {
				invoiceLogic.modifyInvoice(iv);
			}

			return reassignationInvoices;
		}
		return new ArrayList<>();
	}

	private void acceptAndMaskMessage(Message msg, boolean hasError) throws LoginException, MissingRequirementsException {
		msg.setStatus(msg.getStatus() | Message.STATUS_MASKED);
		if (hasError) {
			msg.setStatus(msg.getStatus() | Message.STATUS_WARNING);
		}
		messageLogic.modifyMessage(msg);
		if (msg.getParentId() != null) {
			Message parent = messageLogic.get(msg.getParentId());
			parent.setStatus(parent.getStatus() | Message.STATUS_ACCEPTED_FOR_TREATMENT);
			messageLogic.modifyMessage(parent);
			if (parent.getParentId() != null) {
				Message parentParent = messageLogic.get(parent.getParentId());
				parentParent.setStatus(parent.getStatus() | Message.STATUS_ACCEPTED_FOR_TREATMENT);
				messageLogic.modifyMessage(parentParent);
			}
		}
	}

	private List<org.taktik.icure.entities.Invoice> rejectMessage(Message msg) throws LoginException, MissingRequirementsException {
		msg.setStatus(msg.getStatus() | Message.STATUS_ERROR);
		messageLogic.modifyMessage(msg);
		if (msg.getParentId() != null) {
			Message parent = messageLogic.get(msg.getParentId());
			parent.setStatus(parent.getStatus() | Message.STATUS_REJECTED);
			parent.setStatus(parent.getStatus() | Message.STATUS_ERROR);
			messageLogic.modifyMessage(parent);
			if (parent.getParentId() != null) {
				Message parentParent = messageLogic.get(parent.getParentId());
				parentParent.setStatus(parentParent.getStatus() | Message.STATUS_REJECTED);
				parentParent.setStatus(parentParent.getStatus() | Message.STATUS_ERROR);
				messageLogic.modifyMessage(parentParent);
				List<org.taktik.icure.entities.Invoice> invoices = invoiceLogic.getInvoices(Optional.of(parentParent.getInvoiceIds()).orElse(new LinkedList<>()));
				for (org.taktik.icure.entities.Invoice iv : invoices) {
					for (InvoicingCode ic : iv.getInvoicingCodes()) {
						ic.setCanceled(true);
						ic.setPending(false);
					}
				}
				return invoices;
			}
		}
		return new LinkedList<>();
	}


	@Override
	public List<EfactMessage> loadPendingMessages(String token) throws ConnectorException, TokenNotAvailableException, LoginException, MissingRequirementsException, CreationException {
		synchronized (this) {
			if (token == null) {
				throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
			}

			String inputReference = "" + System.currentTimeMillis();
			RequestObjectBuilder requestObjectBuilder;
			try {
				requestObjectBuilder = BuilderFactory.getRequestObjectBuilder("invoicing");
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			Boolean isTest = config.getProperty("endpoint.mcn.tarification").contains("-acpt");

			CommonInput ci = CommonInputMapper.mapCommonInputType(RequestBuilderFactory.getCommonBuilder("invoicing").createCommonInput(ConfigUtil.retrievePackageInfo("genericasync." + "invoicing"), isTest, inputReference));
			WsAddressingHeader header;
			try {
				header = new WsAddressingHeader(new URI("urn:be:cin:nip:async:generic:get:query"));
				//header.setTo(new URI(oa != null ? "urn:nip:destination:io:" + oa : ""));
				header.setFaultTo("http://www.w3.org/2005/08/addressing/anonymous");
				header.setReplyTo("http://www.w3.org/2005/08/addressing/anonymous");
				header.setMessageID(new URI("" + UUID.randomUUID()));
			} catch (URISyntaxException e) {
				throw new IllegalStateException(e);
			}

			GenAsyncService service = GenAsyncSessionServiceFactory.getGenAsyncService("invoicing");

			int batchSize = 16;

			XStream xStream = new XStream();
			ArrayList<EfactMessage> efactMessages = new ArrayList<>();

			User loggedUser = sessionLogic.getCurrentSessionContext().getUser();
			HealthcareParty hcp = healthcarePartyLogic.getHealthcareParty(loggedUser.getHealthcarePartyId());

			while (true) {
				MsgQuery msgQuery = requestObjectBuilder.createMsgQuery(batchSize, true, "HCPFAC", "HCPAFD", "HCPVWR");
				Query query = requestObjectBuilder.createQuery(batchSize, true);

				GetResponse getResponse;
				try {
					getResponse = service.getRequest(requestObjectBuilder.buildGetRequest(ci.getOrigin(), msgQuery, query), header);
				} catch (TechnicalConnectorException e) {
					if (e.getMessage().contains("SocketTimeout") && batchSize > 1) {
						batchSize /= 2;
						continue;
					}

					throw new IllegalStateException(e);
				} catch (SOAPFaultException e) {
					if (e.getMessage().contains("Not enough time")) {
						break;
					}

					throw new IllegalStateException(e);
				}

				List<MsgResponse> msgResponses = getResponse.getReturn().getMsgResponses();
				List<TAckResponse> tAckResponses = getResponse.getReturn().getTAckResponses();

				List<MsgResponse> toConfirmResponses = new ArrayList<>();

				for (MsgResponse r : msgResponses) {
					EfactMessage message = null;

					String rawMessage = null;
					String structuredMessage = null;
					BelgianInsuranceInvoicing belgianInsuranceInvoicing = null;
					try {
						message = new EfactMessage();
						be.cin.types.v1.Blob detail = r.getDetail();
						message.setDetail(new String(ConnectorIOUtils.decompress(IOUtils.toByteArray(detail.getValue().getInputStream())), "UTF8")); //This starts with 92...
						message.setId(detail.getId());
						message.setName(detail.getMessageName());
						efactMessages.add(message);

						rawMessage = message.getDetail();

						belgianInsuranceInvoicing = new BelgianInsuranceInvoicingReader(hcp.getLanguages() != null && hcp.getLanguages().size() > 0 ? hcp.getLanguages().get(0) : "fr").read(rawMessage);

						structuredMessage = xStream.toXML(belgianInsuranceInvoicing);
					} catch (IOException e) {
						System.err.println(e.getMessage());
						e.printStackTrace(System.err);
					} finally {
						if (message != null && rawMessage != null) {
							Document document = new Document();
							document.setId(idg.newGUID().toString());

							document.setAttachment(("<message><raw>"
								+ rawMessage.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
								+ "</raw><xades>" + Base64.encodeBase64String(r.getXadesT().getValue()) + "</xades><analysis>"
								+ structuredMessage
								+ "</analysis></message>").getBytes());

							document.setName("Reponse d'envoi " + ((belgianInsuranceInvoicing != null && belgianInsuranceInvoicing.getAcknowledgment() != null && belgianInsuranceInvoicing.getAcknowledgment().getMessageName() != null) ? (belgianInsuranceInvoicing.getAcknowledgment().getMessageName() + (belgianInsuranceInvoicing.getErrorCount() != null ? belgianInsuranceInvoicing.getErrorCount().getErrorsPercentage() + " % errors" : "")) : ""));
							document.setMainUti("public.xml");


							String ref = r.getCommonOutput().getInputReference();

							org.taktik.icure.entities.Message msg = new Message();
							msg.setId(idg.newGUID().toString());

							msg.setTransportGuid("EFACT:IN:" + ref);
							msg.setFromAddress("EFACT");
							msg.setRecipients(Collections.singleton(loggedUser.getHealthcarePartyId()));
							msg.setRecipientsType(HealthcareParty.class.getName());

							msg.setReceived(System.currentTimeMillis());
							msg.setStatus(Message.STATUS_UNREAD);
							msg.setSubject("Reponse d'envoi");
							msg.setToAddresses(Collections.singleton("EFACTBOX"));

							Collection<Message> messagesWithTransportGuids = messageLogic.findByTransportGuid(loggedUser.getHealthcarePartyId(), "EFACT:OUT:" + ref, new PaginationOffset<>(1000)).getRows();
							if (messagesWithTransportGuids.size() > 0) {
								Message parent = messagesWithTransportGuids.iterator().next();
								msg.setParentId(parent.getId());
							}

							message.setDocument(documentLogic.createDocument(document, loggedUser.getHealthcarePartyId()));
							message.setMessage(messageLogic.createMessage(msg));

							toConfirmResponses.add(r);

							List<org.taktik.icure.entities.Invoice> reassignedInvoices = analyzeBelgianInsuranceInvoicing(belgianInsuranceInvoicing, msg);
							message.setReassignedInvoices(reassignedInvoices);
						}
					}
				}

				for (TAckResponse r : tAckResponses) {
					try {
						EfactMessage message = new EfactMessage();
						efactMessages.add(message);

						String descr = "Recipice d'envoi";// ("+oa+")";
						Document document = new Document();
						document.setId(idg.newGUID().toString());
						document.setAttachment(xStream.toXML(r).getBytes("UTF8"));
						document.setName(descr);
						document.setMainUti("public.xml");

						String ref = r.getTAck().getAppliesTo().replaceAll("urn:nip:reference:input:", "");

						org.taktik.icure.entities.Message msg = new Message();
						msg.setId(idg.newGUID().toString());
						message.setMessage(msg);
						msg.setTransportGuid("EFACT:IN:" + ref);
						msg.setFromAddress("EFACT");
						msg.setRecipientsType(HealthcareParty.class.getName());
						msg.setRecipients(Collections.singleton(loggedUser.getHealthcarePartyId()));
						msg.setToAddresses(Collections.singleton("EFACTBOX"));
						msg.setStatus(Message.STATUS_MASKED | Message.STATUS_UNREAD);
						Collection<Message> messagesWithTransportGuids = messageLogic.findByTransportGuid(loggedUser.getHealthcarePartyId(), "EFACT:OUT:" + ref, new PaginationOffset<>(1000)).getRows();
						if (messagesWithTransportGuids.size() > 0) {
							Message parent = messagesWithTransportGuids.iterator().next();
							msg.setParentId(parent.getId());
							parent.setStatus(parent.getStatus() | Message.STATUS_RECEIVED);
							messageLogic.modifyMessage(parent);

							Message parentParent = messageLogic.get(parent.getParentId());

							if (parentParent != null) {
								parentParent.setStatus(parent.getStatus() | Message.STATUS_RECEIVED);
								messageLogic.modifyMessage(parentParent);
							}
						}
						msg.setReceived(System.currentTimeMillis());
						msg.setSubject(descr);

						message.setDocument(documentLogic.createDocument(document, loggedUser.getHealthcarePartyId()));
						message.setMessage(messageLogic.createMessage(msg));
					} catch (IOException e) {
						throw new IllegalStateException(e);
					} catch (LoginException | CreationException | MissingRequirementsException e) {
						e.printStackTrace();
					}
				}

				if (toConfirmResponses.size() > 0 || tAckResponses.size() > 0) {
					taskExecutor.execute(() -> {
						WsAddressingHeader confirmheader;
						be.cin.nip.async.generic.Confirm confirm;
						try {
							String confirmessageID = IdGeneratorFactory.getIdGenerator("uuid").generateId();
							confirmheader = WsAddressingUtil.createHeader(null, "urn:be:cin:nip:async:generic:confirm:hash", confirmessageID);
							confirm = requestObjectBuilder.buildConfirmRequest(ci.getOrigin(), toConfirmResponses, tAckResponses);
						} catch (DataFormatException | TechnicalConnectorException e) {
							throw new IllegalStateException(e);
						}
						// The output is empty, which indicates that the confirm was processed correctly.
						// In case there is an error, a fault is returned instead of the empty response
						try {
							service.confirmRequest(confirm, confirmheader);
						} catch (SOAPFaultException e) {
							String xml = getSoapFaultAsString(e);
							log.error("SOAP Fault:\n" + xml, e);

							try {
								//Try one by one
								for (MsgResponse m : toConfirmResponses) {
									String id = IdGeneratorFactory.getIdGenerator("uuid").generateId();
									WsAddressingHeader h = WsAddressingUtil.createHeader(null, "urn:be:cin:nip:async:generic:confirm:hash", id);
									try {
										service.confirmRequest(requestObjectBuilder.buildConfirmRequest(ci.getOrigin(), Collections.singletonList(m), new ArrayList<>()), h);
									} catch (DataFormatException e1) {
										log.error("Data format exception: ", e1);
									} catch (SOAPFaultException e1) {
										log.error("SOAP Fault:\n" + getSoapFaultAsString(e1), e1);
									} catch (GenAsyncBusinessConnectorException e1) {
										log.error("Gen async exception: ", e1);
									}
								}

								for (TAckResponse m : tAckResponses) {
									String id = IdGeneratorFactory.getIdGenerator("uuid").generateId();
									WsAddressingHeader h = WsAddressingUtil.createHeader(null, "urn:be:cin:nip:async:generic:confirm:hash", id);
									try {
										service.confirmRequest(requestObjectBuilder.buildConfirmRequest(ci.getOrigin(), new ArrayList<>(), Collections.singletonList(m)), h);
									} catch (DataFormatException e1) {
										log.error("Data format exception: ", e1);
									} catch (SOAPFaultException e1) {
										log.error("SOAP Fault:\n" + getSoapFaultAsString(e1), e1);
									} catch (GenAsyncBusinessConnectorException e1) {
										log.error("Gen async exception: ", e1);
									}
								}
							} catch (TechnicalConnectorException e1) {
								throw new IllegalStateException(e1);
							}
						} catch (TechnicalConnectorException | GenAsyncBusinessConnectorException e) {
							throw new IllegalStateException(e);
						}
					});
				}

				if (getResponse.getReturn().getMsgCount() < batchSize && getResponse.getReturn().getTAckCount() < batchSize) {
					break;
				} else {
					try {
						Thread.sleep(7000);
					} catch (InterruptedException ignored) {
					}
				}
			}
			return efactMessages;
		}
	}

	private String getSoapFaultAsString(SOAPFaultException e) {
		SOAPFault node = e.getFault();
		StringWriter writer = new StringWriter();
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(node), new StreamResult(writer));
		} catch (TransformerException ex) {
			ex.printStackTrace();
		}
		return writer.toString();
	}

	@Override
	public EfactInvoiceResponse sendInvoicesBatch(String token, InvoicesBatch invoicesBatch, long uniqueSendNumber, InvoiceSender sender, String parentMessageId, Long numericalRef, boolean ignorePrescriptionDate) throws ConnectorException, TokenNotAvailableException, IOException, LoginException, CreationException {
		Boolean isTest = config.getProperty("endpoint.mcn.tarification").contains("-acpt");
		User loggedUser = sessionLogic.getCurrentSessionContext().getUser();

		if (numericalRef>9999999999L) {
			throw new IllegalArgumentException("numericalRef is too long (10 positions max)"); //Can only be 10 positions long as it is going to be multiplied by 100 for record 950000 which doesn't allow more than 12 positions
		}

		if (token == null) {
			throw new TokenNotAvailableException("Cannot obtain token for Ehealth Box operations");
		}

		String oa = invoicesBatch.getOaCode();

		if (invoicesBatch.getInvoices().size() == 0) {
			return null;
		}

		StringWriter stringWriter = new StringWriter();
		BelgianInsuranceInvoicingFormat iv = new BelgianInsuranceInvoicingFormat(stringWriter, insuranceLogic);
		String inputReference = "" + new DecimalFormat("00000000000000").format(numericalRef);

		try {
			iv.write920000(sender, numericalRef, invoicesBatch.getBatchRef(), isTest ? 92 : 12, uniqueSendNumber, invoicesBatch.getInvoicingYear(), invoicesBatch.getInvoicingMonth(), isTest);

			List<Long> codes = new ArrayList<>();
			long amount = 0L;
			long recordsCount = 0L;

			Map<String, List<Long>> codesPerOAMap = new HashMap<>();
			Map<String, Long[]> amountPerOAMap = new HashMap<>();
			Map<String, Long[]> recordsCountPerOAMap = new HashMap<>();

			iv.writeFileHeader(sender, isTest ? 9991999L : 1999L, uniqueSendNumber, invoicesBatch.getInvoicingYear(), invoicesBatch.getInvoicingMonth(), invoicesBatch.getBatchRef());
			recordsCount++;

			for (Invoice invoice : invoicesBatch.getInvoices().stream().sorted(
					(i1,i2)->iv.getDestCode(i1.getInsurance().getCode(), sender).compareToIgnoreCase(iv.getDestCode(i2.getInsurance().getCode(), sender))
			).collect(Collectors.toList())) {
				if (invoice.getItems() != null && invoice.getItems().size()>0) {
					Insurance insurance = invoice.getInsurance();
					String insuranceCode = insurance.getCode().substring(0, 3).replaceAll("[^0-9]", "");
					String destCode = iv.getDestCode(insurance.getCode().substring(0, 3).replaceAll("[^0-9]", ""), sender);

					List<Long> codesPerOA = codesPerOAMap.get(destCode);
					Long[] amountPerOA = amountPerOAMap.get(destCode);
					Long[] recordsCountPerOA = recordsCountPerOAMap.get(destCode);

					if (codesPerOA == null) {
						codesPerOAMap.put(destCode, codesPerOA = new ArrayList<>());
					}
					if (amountPerOA == null) {
						amountPerOAMap.put(destCode, amountPerOA = new Long[]{0L});
					}
					if (recordsCountPerOA == null) {
						recordsCountPerOAMap.put(destCode, recordsCountPerOA = new Long[]{0L});
					}

					List<Long> recordCodes = new ArrayList<>();
					long recordAmount = 0L;
					long recordFee = 0L;
					long recordSup = 0L;
					iv.writeRecordHeader(sender, invoice.getInvoiceNumber(), invoice.getReason(), invoice.getInvoiceRef(), invoice.getPatient(), insuranceCode, ignorePrescriptionDate);
					recordsCountPerOA[0]++;
					recordsCount++;
					for (InvoiceItem it : invoice.getItems()) {
						iv.writeRecordContent(sender, invoicesBatch.getInvoicingYear(), invoicesBatch.getInvoicingMonth(), it.getInvoiceRef(), invoice.getPatient(), insuranceCode, it);

						recordsCountPerOA[0]++;
						recordsCount++;

						if (it.getInsuranceRef() != null) {
							iv.writeInvolvementRecordContent(invoicesBatch.getInvoicingYear(), invoicesBatch.getInvoicingMonth(), invoice.getPatient(), insuranceCode, it);
							recordCodes.add(it.getCodeNomenclature());
							recordsCountPerOA[0]++;
							recordsCount++;
						}

						if (it.getEidItem() != null) {
							iv.writeEid(it, invoice.getPatient(), sender);
							recordCodes.add(it.getCodeNomenclature());
							recordsCountPerOA[0]++;
							recordsCount++;
						}

						codesPerOA.add(it.getCodeNomenclature());
						amountPerOA[0] += it.getReimbursedAmount();

						recordCodes.add(it.getCodeNomenclature());
						recordAmount += it.getReimbursedAmount();
						recordFee += it.getPatientFee();
						recordSup += it.getDoctorSupplement();

					}
					iv.writeRecordFooter(sender, invoice.getInvoiceNumber(), invoice.getInvoiceRef(), invoice.getPatient(), insuranceCode, recordCodes, recordAmount, recordFee, recordSup);
					recordsCountPerOA[0]++;
					recordsCount++;

					codes.addAll(recordCodes);
					amount += recordAmount;
				}
			}
			iv.writeFileFooter(sender, uniqueSendNumber, invoicesBatch.getInvoicingYear(), invoicesBatch.getInvoicingMonth(), codes, amount);
			recordsCount++;

			for (String k : codesPerOAMap.keySet()) {
				iv.write950000(k, numericalRef, recordsCountPerOAMap.get(k)[0], codesPerOAMap.get(k), amountPerOAMap.get(k)[0]);
			}
			iv.write960000(invoicesBatch.getOaCode().replaceAll("00", "99"), recordsCount, codes, amount);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

		String content = stringWriter.toString();

		BlobBuilder bbuilder = RequestBuilderFactory.getBlobBuilder("invoicing");
		RequestObjectBuilder requestObjectBuilder;
		try {
			requestObjectBuilder = BuilderFactory.getRequestObjectBuilder("invoicing");
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		Blob blob = bbuilder.build(ConnectorIOUtils.toBytes(content, Charset.UTF_8)); // will build the Blob as configured with properties

		String messageName = "HCPFAC"; // depends on content of message HCPFAC HCPAFD or HCPVWR
		blob.setMessageName(messageName);

		// Creation of the request
		CommonInput ci = CommonInputMapper.mapCommonInputType(RequestBuilderFactory.getCommonBuilder("invoicing").createCommonInput(ConfigUtil.retrievePackageInfo("genericasync." + "invoicing"), isTest, inputReference));
		BlobType blobForXades = SendRequestMapper.mapBlobToBlobType(blob);
		be.cin.types.v1.Blob det = SendRequestMapper.mapBlobToCinBlob(blob);
		byte[] xades = BlobUtil.generateXades(blobForXades, "invoicing").getValue();
		Post post = requestObjectBuilder.buildPostRequest(ci, det, xades);

		GenAsyncService service = GenAsyncSessionServiceFactory.getGenAsyncService("invoicing");
		WsAddressingHeader header;
		try {
			header = new WsAddressingHeader(new URI("urn:be:cin:nip:async:generic:post:msg"));
			header.setTo(new URI(oa != null ? "urn:nip:destination:io:" + oa : ""));
			header.setFaultTo("http://www.w3.org/2005/08/addressing/anonymous");
			header.setReplyTo("http://www.w3.org/2005/08/addressing/anonymous");
			header.setMessageID(new URI("" + UUID.randomUUID()));
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		PostResponse postResponse = service.postRequest(post, header);

		TAck tack = postResponse.getReturn();

		boolean success = tack.getResultMajor() != null && tack.getResultMajor().equals("urn:nip:tack:result:major:success");
		org.taktik.icure.entities.Message msg = null;
		Document document = null;
		if (success) {
			String descr = "Envoi Efact (" + oa + ")";

			document = new Document();
			document.setId(idg.newGUID().toString());

			document.setAttachment(content.getBytes("UTF8"));

			document.setName(descr);
			document.setMainUti("public.plain-text");

			msg = new Message();
			msg.setId(idg.newGUID().toString());

			msg.setTransportGuid("EFACT:OUT:" + inputReference);
			msg.setToAddresses(Collections.singleton("EFACT"));
			msg.setFromHealthcarePartyId(loggedUser.getHealthcarePartyId());
			msg.setExternalRef(""+uniqueSendNumber);

			msg.setSent(new Date().getTime());
			msg.setStatus(Message.STATUS_UNREAD);
			msg.setSubject(descr);

			msg.setParentId(parentMessageId);

			document = documentLogic.createDocument(document, loggedUser.getHealthcarePartyId());
			msg = messageLogic.createMessage(msg);
		}

		return new EfactInvoiceResponse(success, inputReference, msg, document);
	}

	private InvoicesBatch createBatch(String token, String batchRef, Insurance is, Map<String, List<org.taktik.icure.entities.Invoice>> ivs, HealthcareParty hcp) {
		InvoicesBatch invBatch = new InvoicesBatch();

		Calendar calendar = Calendar.getInstance();

		invBatch.setInvoicingYear(calendar.get(Calendar.YEAR));
		invBatch.setInvoicingMonth(calendar.get(Calendar.MONTH) + 1);

		invBatch.setBatchRef("" + batchRef);

		invBatch.setOaCode(is.getCode());

		List<Invoice> invoices = new ArrayList<>();

		for (Map.Entry<String, List<org.taktik.icure.entities.Invoice>> e : ivs.entrySet()) {
			Patient patient = patientLogic.getPatient(e.getKey());

			for (org.taktik.icure.entities.Invoice iv : e.getValue()) {
				List<InvoicingCode> ivcs = iv.getInvoicingCodes();

				Invoice invoice = new Invoice();

				invoice.setPatient(patient);
				if (patient.getInsurabilities().size()==0 || patient.getInsurabilities().get(0).getInsuranceId() == null) {
					try {
						InsurabilityInfo generalInsurabity = generalInsurabilityLogic.getGeneralInsurabity(token, patient.getSsin(), null, null, new Date(), false);
						if (generalInsurabity != null && generalInsurabity.getInsurabilities().size()>0) {
							InsurabilityItem insurabilityItem = generalInsurabity.getInsurabilities().get(0);
							List<Insurance> insurances = insuranceLogic.listInsurancesByCode(insurabilityItem.getMutuality());
							if (insurances.size()>0) {
								invoice.setInsurance(insurances.get(0));
							}
						}
					} catch (TokenNotAvailableException | ConnectorException e1) {
						log.error(e1);
					}
				} else {
					invoice.setInsurance(insuranceLogic.getInsurance(patient.getInsurabilities().get(0).getInsuranceId()));
				}
				long invoiceNumber = (iv.getInvoiceReference()!=null && iv.getInvoiceReference().matches("^[0-9]{4,12}$"))?Long.valueOf(iv.getInvoiceReference()):(this.encodeNumberFromUUID(UUID.fromString(iv.getId())));
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setInvoiceRef(encodeRefFromUUID(UUID.fromString(iv.getId())));
				invoice.setReason(InvoicingTreatmentReasonCode.Other);

				List<InvoiceItem> items = new ArrayList<>();

				for (InvoicingCode ivc : ivcs) {
					Double patientIntervention = ivc.getPatientIntervention();
					Double doctorSupplement = ivc.getDoctorSupplement();
					Double reimbursement = ivc.getReimbursement();

					if (patientIntervention == null) {
						patientIntervention = 0d;
					}
					if (doctorSupplement == null) {
						doctorSupplement = 0d;
					}
					if (reimbursement == null) {
						reimbursement = 0d;
					}

					items.add(createInvoiceItem(hcp, encodeRefFromUUID(UUID.fromString(ivc.getId())), Long.valueOf(ivc.getCode() != null ? ivc.getCode() : ivc.getTarificationId().split("\\|")[1]), Math.round(reimbursement * 100), Math.round(patientIntervention * 100), Math.round(doctorSupplement * 100),
							ivc.getContract(), ivc.getDateCode(), ivc.getEidReadingHour(), ivc.getEidReadingValue(), ivc.getSide() == null ? -1 : ivc.getSide(), ivc.getOverride3rdPayerCode(), ivc.getTimeOfDay() == null ? -1 : ivc.getTimeOfDay(), ivc.getCancelPatientInterventionReason(), ivc.getRelatedCode() == null ? 0 : Long.valueOf(ivc.getRelatedCode()), ivc.getGnotionNihii(), ivc.getPrescriberNihii(), ivc.getUnits() == null ? 1 : ivc.getUnits(), ivc.getPrescriberNorm() == null ? -1 : ivc.getPrescriberNorm(), ivc.getPercentNorm() == null ? -1 : ivc.getPercentNorm()
					));

					ivc.setStatus(InvoicingCode.STATUS_PENDING);
				}
				invoice.setItems(items);
				invoices.add(invoice);
			}
		}

		invBatch.setInvoices(invoices);
		return invBatch;
	}

	private InvoiceItem createInvoiceItem(HealthcareParty hcp, String ref, long codeNomenclature, long reimbursedAmount, long patientFee, long doctorSupplement, String contract, Long date, Integer eidReading, String eidValue, Integer side, Integer thirdPayerExceptionCode, int timeOfDay, Integer personalInterventionCoveredByThirdPartyCode, Long prestationRelative, String dmgReference, String prescriberIdentificationNumber, int units, Integer prescriberCode, Integer percentNorm) {
		InvoiceItem invoiceItem = new InvoiceItem();

		invoiceItem.setInsuranceRef(contract);
		invoiceItem.setInsuranceRefDate(date);

		invoiceItem.setDateCode(date);
		invoiceItem.setCodeNomenclature(codeNomenclature);
		invoiceItem.setRelatedCode(prestationRelative);
		invoiceItem.setGnotionNihii(dmgReference);
		invoiceItem.setDoctorIdentificationNumber(hcp.getNihii());
		invoiceItem.setDoctorSupplement(doctorSupplement);
		invoiceItem.setInvoiceRef(ref);
		invoiceItem.setPatientFee(patientFee);
		invoiceItem.setPersonalInterventionCoveredByThirdPartyCode(personalInterventionCoveredByThirdPartyCode);
		invoiceItem.setPrescriberNorm(InvoicingPrescriberCode.withCode(prescriberCode));
		invoiceItem.setPercentNorm(InvoicingPercentNorm.withCode(percentNorm));
		invoiceItem.setPrescriberNihii(prescriberIdentificationNumber);
		invoiceItem.setReimbursedAmount(reimbursedAmount);
		invoiceItem.setSideCode(InvoicingSideCode.withSide(side));
		invoiceItem.setOverride3rdPayerCode(thirdPayerExceptionCode);
		invoiceItem.setTimeOfDay(InvoicingTimeOfDay.withCode(timeOfDay));
		invoiceItem.setUnits(units);

		if (eidReading != null && eidReading != 0 && eidValue != null && eidValue.length()>6) {
			invoiceItem.setEidItem(new EIDItem(date, eidReading, eidValue));
		}

		return invoiceItem;
	}

	@Override
	public SentMessageBatch createBatchAndSend(String token, String batchRef, Long numericalRef, HealthcareParty hcp, Insurance insurance, boolean ignorePrescriptionDate, Map<String, List<org.taktik.icure.entities.Invoice>> invoices) throws TokenNotAvailableException, ConnectorException, LoginException, IOException, CreationException {
		synchronized (this) {
			InvoicesBatch invBatch = createBatch(token, encodeRefFromUUID(UUID.fromString(batchRef)).substring(0, 13), insurance, invoices, hcp);

			assert hcp.getCbe() != null;
			assert hcp.getNihii() != null;
			String bic = hcp.getFinancialInstitutionInformation().stream().filter(fi -> insurance.getCode().equals(fi.getKey())).findFirst().map((financialInstitutionInformation) -> financialInstitutionInformation.getProxyBic() != null ? financialInstitutionInformation.getProxyBic() : financialInstitutionInformation.getBic()).orElse(hcp.getProxyBic() != null ? hcp.getProxyBic() : hcp.getBic());
			String iban = hcp.getFinancialInstitutionInformation().stream().filter(fi -> insurance.getCode().equals(fi.getKey())).findFirst().map((financialInstitutionInformation) -> financialInstitutionInformation.getProxyBankAccount() != null ? financialInstitutionInformation.getProxyBankAccount() : financialInstitutionInformation.getBankAccount()).orElse(hcp.getProxyBankAccount() != null ? hcp.getProxyBankAccount() : hcp.getBankAccount());

			assert bic != null;
			assert iban != null;

			InvoiceSender sender = new InvoiceSender(Long.valueOf(hcp.getNihii().replaceAll("[^0-9]", "")), bic, iban);
			sender.setFirstName(hcp.getFirstName());
			sender.setLastName(hcp.getLastName());

			Optional<Address> address = hcp.getAddresses().stream().findFirst();

			sender.setPhoneNumber(Long.valueOf(address.map(a -> a.getTelecoms().stream().filter(t -> t.getTelecomType() == TelecomType.phone).findFirst()).map(t -> t.map(Telecom::getTelecomNumber).map(s -> s.replaceAll("\\+", "00").replaceAll("[^0-9]", "")).orElse("0")).orElse("0")));
			sender.setBce(Long.valueOf(hcp.getCbe().replaceAll("[^0-9]", "")));

			sender.setConventionCode(hcp.getConvention() != null ? hcp.getConvention() : 0);

			ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(1);
			for (org.taktik.icure.entities.Invoice invoice : invoices.values().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
				ZonedDateTime invoiceDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(invoice.getInvoiceDate()), ZoneId.systemDefault());
				if (invoiceDateTime.isAfter(zonedDateTime)) {
					zonedDateTime = invoiceDateTime;
				}
			}

			int maxSendNumber = ZonedDateTime.now().get(ChronoField.DAY_OF_YEAR) * 2 + (insurance.getCode().equals("306") ? 1 : 0);
			int sendNumber = zonedDateTime.get(ChronoField.DAY_OF_YEAR) * 2 + (insurance.getCode().equals("306") ? 1 : 0);
			//Check unicity
			while (messageLogic.listMessagesByExternalRefs(hcp.getId(), Collections.singletonList("" + sendNumber)).stream().filter(m -> m.getRecipients().contains(insurance.getId())).count() > 0) {
				sendNumber += 1;
				if (sendNumber > maxSendNumber) {
					throw new IllegalArgumentException("A message has already eben sent for this reference");
				}
			}

			ArrayList<String> invoiceIds = new ArrayList<>();
			org.taktik.icure.entities.Message mm = new org.taktik.icure.entities.Message();

			for (List<org.taktik.icure.entities.Invoice> ivs : invoices.values()) {
				invoiceIds.addAll(ivs.stream().map(StoredDocument::getId).collect(Collectors.toList()));
			}

			mm.setId(batchRef);
			mm.setInvoiceIds(invoiceIds);
			mm.setSubject("Facture tiers payant");
			mm.setStatus(Message.STATUS_UNREAD | Message.STATUS_EFACT);
			mm.setTransportGuid("EFACT:BATCH:" + batchRef);
			mm.setAuthor(sessionLogic.getCurrentSessionContext().getUser().getId());
			mm.setResponsible(hcp.getId());
			mm.setFromHealthcarePartyId(hcp.getId());
			mm.setRecipients(Collections.singleton(insurance.getId()));
			mm.setExternalRef("" + sendNumber);

			Map<String,List<Delegation>> delegations = new HashMap<>();
			delegations.put(hcp.getId(),new ArrayList<>());

			mm.setDelegations(delegations);
			if (insurance.getAddress() != null && insurance.getAddress().getTelecoms() != null) {
				mm.setToAddresses(Collections.singleton(insurance.getAddress().getTelecoms().stream()
					.filter((Telecom t) -> t.getTelecomType() == TelecomType.email && t.getTelecomNumber() != null && t.getTelecomNumber().length() > 0).findAny().map(Telecom::getTelecomNumber).orElse(insurance.getCode())));
			}
			mm.setSent(System.currentTimeMillis());

			EfactInvoiceResponse response = sendInvoicesBatch(token, invBatch, sendNumber, sender, mm.getId(), numericalRef, ignorePrescriptionDate);

			messageLogic.createMessage(mm);

			for (List<org.taktik.icure.entities.Invoice> ivs : invoices.values()) {
				invoiceLogic.getInvoices(ivs.stream().map(org.taktik.icure.entities.Invoice::getId).collect(Collectors.toList())).forEach(i -> {
					i.setSentDate(mm.getSent());
					invoiceLogic.modifyInvoice(i);
				});
			}

			return new SentMessageBatch(mm, response);
		}
	}

	@Autowired
	public void setDocumentLogic(DocumentLogic documentLogic) {
		this.documentLogic = documentLogic;
	}

	@Autowired
	public void setMessageLogic(MessageLogic messageLogic) {
		this.messageLogic = messageLogic;
	}

	@Autowired
	public void setMainLogic(MainLogic mainLogic) {
		this.mainLogic = mainLogic;
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Autowired
	public void setHealthcarePartyLogic(HealthcarePartyLogic healthcarePartyLogic) {
		this.healthcarePartyLogic = healthcarePartyLogic;
	}

	@Autowired
	public void setGeneralInsurabilityLogic(GeneralInsurabilityLogicImpl generalInsurabilityLogic) {
		this.generalInsurabilityLogic = generalInsurabilityLogic;
	}

	@Autowired
	public void setInvoiceLogic(InvoiceLogic invoiceLogic) {
		this.invoiceLogic = invoiceLogic;
	}

	@Autowired
	public void setInsuranceLogic(InsuranceLogic insuranceLogic) {
		this.insuranceLogic = insuranceLogic;
	}

	@Autowired
	public void setPatientLogic(PatientLogic patientLogic) {
		this.patientLogic = patientLogic;
	}

	@Autowired
	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	@Autowired
	public void setUuidGenerator(UUIDGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}
}
