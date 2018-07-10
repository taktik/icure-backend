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

package org.taktik.icure.be.ehealth.logic.chapter4.impl


import be.ehealth.business.mycarenetcommons.builders.RequestBuilderFactory
import be.ehealth.businessconnector.chapterIV.builders.BuilderFactory
import be.ehealth.businessconnector.chapterIV.domain.ChapterIVBuilderResponse
import be.ehealth.businessconnector.chapterIV.domain.ChapterIVReferences
import be.ehealth.businessconnector.chapterIV.exception.ChapterIVBusinessConnectorException
import be.ehealth.businessconnector.chapterIV.exception.ChapterIVBusinessConnectorExceptionValues
import be.ehealth.businessconnector.chapterIV.mappers.CommonInputMapper
import be.ehealth.businessconnector.chapterIV.session.ChapterIVSessionServiceFactory
import be.ehealth.businessconnector.chapterIV.utils.ACLUtils
import be.ehealth.businessconnector.chapterIV.utils.FolderTypeUtils
import be.ehealth.businessconnector.chapterIV.utils.KeyDepotHelper
import be.ehealth.businessconnector.chapterIV.validators.Chapter4XmlValidator
import be.ehealth.businessconnector.chapterIV.validators.impl.Chapter4XmlValidatorImpl
import be.ehealth.businessconnector.chapterIV.wrapper.Chap4MedicalAdvisorAgreementRequestWrapper
import be.ehealth.businessconnector.chapterIV.wrapper.SealedRequestWrapper
import be.ehealth.businessconnector.chapterIV.wrapper.UnsealedRequestWrapper
import be.ehealth.businessconnector.chapterIV.wrapper.factory.XmlObjectFactory
import be.ehealth.businessconnector.chapterIV.wrapper.factory.impl.AskXmlObjectFactory
import be.ehealth.businessconnector.chapterIV.wrapper.factory.impl.ConsultationXmlObjectFactory
import be.ehealth.businessconnector.chapterIV.wrapper.impl.WrappedObjectMarshallerHelper
import be.ehealth.technicalconnector.config.ConfigFactory
import be.ehealth.technicalconnector.config.util.ConfigUtil
import be.ehealth.technicalconnector.exception.SoaErrorException
import be.ehealth.technicalconnector.exception.TechnicalConnectorException
import be.ehealth.technicalconnector.exception.TechnicalConnectorExceptionValues
import be.ehealth.technicalconnector.exception.UnsealConnectorException
import be.ehealth.technicalconnector.service.keydepot.KeyDepotManager
import be.ehealth.technicalconnector.service.keydepot.KeyDepotManagerFactory
import be.ehealth.technicalconnector.service.kgss.KgssManager
import be.ehealth.technicalconnector.service.kgss.domain.KeyResult
import be.ehealth.technicalconnector.utils.ConnectorExceptionUtils
import be.ehealth.technicalconnector.utils.MarshallerHelper
import be.ehealth.technicalconnector.utils.SessionUtil
import be.fgov.ehealth.chap4.core.v1.CareReceiverIdType
import be.fgov.ehealth.chap4.core.v1.CommonInputType
import be.fgov.ehealth.chap4.core.v1.RecordCommonInputType
import be.fgov.ehealth.chap4.core.v1.SecuredContentType
import be.fgov.ehealth.chap4.protocol.v1.AbstractChap4MedicalAdvisorAgreementResponseType
import be.fgov.ehealth.chap4.protocol.v1.AskChap4MedicalAdvisorAgreementResponse
import be.fgov.ehealth.medicalagreement.core.v1.Kmehrrequest
import be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR
import be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes
import be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
import org.apache.commons.collections4.CollectionUtils
import org.joda.time.DateTime
import org.springframework.stereotype.Service
import org.taktik.icure.be.drugs.civics.AddedDocumentPreview
import org.taktik.icure.be.drugs.civics.ParagraphPreview
import org.taktik.icure.be.ehealth.dto.chapter4.AgreementResponse
import org.taktik.icure.be.ehealth.dto.chapter4.AgreementTransaction
import org.taktik.icure.be.ehealth.dto.chapter4.Problem
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMMAAvalues.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes.CD_ITEM_MAA
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues.ISANAPPENDIXOF
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues.MULTIMEDIA
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDMAARESPONSETYPEvalues.AGREEMENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDMAARESPONSETYPEvalues.INTREATMENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes.CD_TRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes.CD_TRANSACTION_MAA
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes.ID_HCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes.ID_KMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.logic.chapter4.Appendix
import org.taktik.icure.be.ehealth.logic.chapter4.Chapter4Logic
import org.taktik.icure.be.ehealth.logic.chapter4.RequestType
import org.taktik.icure.be.ehealth.logic.chapter4.RequestType.cancellation
import org.taktik.icure.be.ehealth.logic.chapter4.RequestType.closure
import org.taktik.icure.be.ehealth.logic.kmehr.v20121001.KmehrExport
import org.taktik.icure.be.ehealth.logic.messages.AbstractMessage
import org.taktik.icure.be.ehealth.logic.messages.ErrorWarningMessages
import org.taktik.icure.be.ehealth.logic.messages.WarningMessage
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.Patient
import org.taktik.icure.logic.MessageLogic
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.MessageDto
import org.taktik.icure.utils.FuzzyValues
import org.w3c.dom.Element
import java.io.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.*
import javax.xml.bind.JAXBContext
import kotlin.collections.ArrayList

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 06/06/13
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
@Service
class Chapter4LogicImpl : KmehrExport(), Chapter4Logic {

    private val idg = UUIDGenerator()

    private var messageLogic: MessageLogic? = null

    private var consultMessages: List<AbstractMessage> = ArrayList()
    private var demandMessages: List<AbstractMessage> = ArrayList()
    private val chapter4XmlValidator: Chapter4XmlValidator = Chapter4XmlValidatorImpl()
    private val config = ConfigFactory.getConfigValidator(emptyList())

    init {
        var errors: InputStream? = Chapter4Logic::class.java.getResourceAsStream("ErrorCodesConsultation.xml")
        if (errors != null) {
            consultMessages = ErrorWarningMessages.parse(errors)
        }

        errors = Chapter4Logic::class.java.getResourceAsStream("ErrorCodesDemand.xml")
        if (errors != null) {
            demandMessages = ErrorWarningMessages.parse(errors)
        }
    }

    fun setMessageLogic(messageLogic: MessageLogic) {
        this.messageLogic = messageLogic
    }

    override fun findParagraphs(searchString: String, language: String): List<ParagraphPreview> {
        return drugsLogic!!.findParagraphs(searchString, language)
    }

    override fun findParagraphsWithCnk(cnk: Long?, language: String): List<ParagraphPreview> {
        return drugsLogic!!.findParagraphsWithCnk(cnk, language)
    }

    override fun getAddedDocuments(chapterName: String, paragraphName: String): List<AddedDocumentPreview> {
        return drugsLogic!!.getAddedDocuments(chapterName, paragraphName)
    }

    protected fun createCommonInput(isTest: Boolean, commonReference: String?, commonNIPReference: String?): CommonInputType {
        val packageInfo = ConfigUtil.retrievePackageInfo("chapterIV")
        val commonInput = RequestBuilderFactory.getCommonBuilder("chapterIV").createCommonInput(packageInfo, isTest, commonReference)
        return CommonInputMapper.mapCommonInputType(commonInput)
    }


    protected fun getUnknownKey(subTypeName: String): KeyResult {
        val acl = ACLUtils.createAclChapterIV(subTypeName)
        if (KeyDepotManagerFactory.getKeyDepotManager().getETK(KeyDepotManager.EncryptionTokenType.ENCRYPTION) == null) {
            log.debug("\t## EncryptionETK is null")
            throw TechnicalConnectorException(TechnicalConnectorExceptionValues.ERROR_ETK_NOTFOUND, *arrayOf<Any>("EncryptionETK is undefined"))
        } else {
            val systemETK = null//KeyDepotManagerFactory.getKeyDepotManager().getETK(KeyDepotManager.EncryptionTokenType.ENCRYPTION).etk.encoded
            val unknownKey = KgssManager.getInstance().getNewKeyFromKgss(acl, systemETK)
            return unknownKey
        }
    }

    @Throws(TechnicalConnectorException::class, ChapterIVBusinessConnectorException::class)
    private fun createAndValidateSealedRequest(message: Kmehrmessage, careReceiver: CareReceiverIdType, xmlObjectFactory: XmlObjectFactory, agreementStartDate: DateTime): SealedRequestWrapper<*> {
        try {
            val e = this.getUnknownKey(xmlObjectFactory.subtypeNameToRetrieveCredentialTypeProperties)
            val request = xmlObjectFactory.createSealedRequest()
            request.agreementStartDate = agreementStartDate
            request.careReceiver = this.mapToCinCareReceiverIdType(careReceiver)
            request.sealedContent = this.getSealedContent(message, e, xmlObjectFactory)
            request.unsealKeyId = e.getKeyId()
            this.chapter4XmlValidator.validate(request.xmlObject)

            return request
        } catch (var7: UnsupportedEncodingException) {
            log.debug("\t## The Character Encoding is not supported : throwing technical connector exception")
            throw TechnicalConnectorException(TechnicalConnectorExceptionValues.CHARACTER_ENCODING_NOTSUPPORTED, var7, *arrayOfNulls<Any>(0))
        }

    }

    private fun mapToCinCareReceiverIdType(careReceiver: CareReceiverIdType): be.cin.types.v1.CareReceiverIdType {
        val mappedCareReceiver = be.cin.types.v1.CareReceiverIdType()
        mappedCareReceiver.mutuality = careReceiver.mutuality
        mappedCareReceiver.regNrWithMut = careReceiver.regNrWithMut
        mappedCareReceiver.ssin = careReceiver.ssin
        return mappedCareReceiver
    }

    protected fun getSealedContent(message: Kmehrmessage, unknownKey: KeyResult, xmlObjectFactory: XmlObjectFactory): ByteArray {
        val request = this.createAndValidateUnsealedRequest(message, xmlObjectFactory)
        return SessionUtil.getEncryptionCrypto().seal(WrappedObjectMarshallerHelper.toXMLByteArray(request), unknownKey.secretKey, unknownKey.keyId)
    }

    private fun createAndValidateUnsealedRequest(message: Kmehrmessage, xmlObjectFactory: XmlObjectFactory): UnsealedRequestWrapper<*> {
        val request = xmlObjectFactory.createUnsealedRequest()
        request.etkHcp = null//KeyDepotManagerFactory.getKeyDepotManager().getETK(KeyDepotManager.EncryptionTokenType.ENCRYPTION).etk.encoded
        request.kmehrRequest = this.createAndValidateKmehrRequestXmlByteArray(message)
        this.chapter4XmlValidator.validate(request.xmlObject)
       return request
    }

    private fun createAndValidateKmehrRequestXmlByteArray(message: Kmehrmessage): ByteArray {
        val kmehrrequest = this.createKmehrRequest(message)
        this.chapter4XmlValidator.validate(kmehrrequest)
        val kmehrMarshallHelper = MarshallerHelper(Kmehrrequest::class.java, Kmehrrequest::class.java)
        return kmehrMarshallHelper.toXMLByteArray(kmehrrequest)
    }

    private fun createKmehrRequest(message: Kmehrmessage): Kmehrrequest {
        val kmehrrequest = Kmehrrequest()
        kmehrrequest.kmehrmessage = message
        return kmehrrequest
    }

    private fun marshallAndEncryptSealedRequest(request: SealedRequestWrapper<*>): SecuredContentType {
        val marshalledContent = WrappedObjectMarshallerHelper.toXMLByteArray(request)
        log.debug("securedContent : " + marshalledContent)
        val sealedKnown = SessionUtil.getEncryptionCrypto().seal(KeyDepotHelper.getChapterIVEncryptionToken(), marshalledContent)
        val securedContent = SecuredContentType()
        securedContent.securedContent = sealedKnown
        return securedContent
    }

    private fun buildAndValidateAgreementRequest(xmlObjectFactory: XmlObjectFactory, careReceiver: CareReceiverIdType, recordCommonInput: RecordCommonInputType, commonInput: CommonInputType, sealedRequest: SealedRequestWrapper<*>): Chap4MedicalAdvisorAgreementRequestWrapper<*> {
        val agreementRequest = xmlObjectFactory.createChap4MedicalAdvisorAgreementRequest()
        agreementRequest.careReceiver = careReceiver
        agreementRequest.recordCommonInput = recordCommonInput
        agreementRequest.commonInput = commonInput
        agreementRequest.request = this.marshallAndEncryptSealedRequest(sealedRequest)
        this.chapter4XmlValidator.validate(agreementRequest.xmlObject)
        return agreementRequest
    }

    fun createAgreementRequest(message: be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage, isTest: Boolean, references: ChapterIVReferences, xmlObjectFactory: XmlObjectFactory, agreementStartDate: DateTime?): ChapterIVBuilderResponse {
        if (agreementStartDate == null) {
            throw ChapterIVBusinessConnectorException(ChapterIVBusinessConnectorExceptionValues.INPUT_PARAM_NULL, "input parameter agreementStartDate was null")
        } else {
            val folder = message.folders[0]
            val careReceiver = CareReceiverIdType().apply {
                ssin = folder.patient.ids.filter { it.s == IDPATIENTschemes.ID_PATIENT && it.value != null }.firstOrNull()?.value
                mutuality = folder.patient.insurancymembership?.id?.value
                regNrWithMut = folder.patient.insurancymembership?.membership?.let { if (it is Element) it.textContent else null }
            }

            val recordCommonInput = RecordCommonInputType().apply { inputReference = BigDecimal(references.recordCommonInputId) }
            val commonInput = createCommonInput(isTest, references.commonReference, references.commonNIPReference)
            val sealedRequest = this.createAndValidateSealedRequest(message, careReceiver, xmlObjectFactory, agreementStartDate)
            val resultWrapper = this.buildAndValidateAgreementRequest(xmlObjectFactory, careReceiver, recordCommonInput, commonInput, sealedRequest)
            val result = hashMapOf(
                    "references" to references,
                    "folder" to folder,
                    "kmehrmessage" to message,
                    "carereceiver" to careReceiver,
                    "recordcommoninput" to recordCommonInput,
                    "commoninput" to (commonInput as Serializable),
                    "sealedrequest" to sealedRequest,
                    "result" to resultWrapper)
            return ChapterIVBuilderResponse(result)
        }
    }

    override fun requestAgreement(token: String, patient: Patient, requestType: RequestType, civicsVersion: String, paragraph: String, appendices: List<Appendix>, verses: List<String>?, incomplete: Boolean, start: Long, end: Long?, decisionReference: String?, ioRequestReference: String?): AgreementResponse {
        val isTest = config.getProperty("endpoint.ch4.admission.v1").contains("-acpt")

        val ref = "" + System.currentTimeMillis()
        val references = ChapterIVReferences(true)

        val hcp = healthcarePartyLogic!!.getHealthcareParty(sessionLogic!!.currentSessionContext.user.healthcarePartyId)
        val demandMessage = getDemandKmehrMessage(hcp, patient, requestType, references.commonReference, civicsVersion, incomplete, start, end, verses, appendices, ref, decisionReference, ioRequestReference, paragraph)

        val bos = ByteArrayOutputStream()
        JAXBContext.newInstance(org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller().marshal(demandMessage, bos!!)
        val msg = bos.toByteArray()
        val v1Message = JAXBContext.newInstance(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createUnmarshaller().unmarshal(ByteArrayInputStream(msg)) as be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage

        val service = ChapterIVSessionServiceFactory.getChapterIVService()
        val responseBuilder = BuilderFactory.getBuilderFactoryForSession().responseBuilder

        val agreementStartDate = FolderTypeUtils.retrieveConsultationStartDateOrAgreementStartDate(v1Message.folders[0])
        val request = createAgreementRequest(v1Message, isTest, references, AskXmlObjectFactory(), agreementStartDate ?: DateTime()).askChap4MedicalAdvisorAgreementRequest
        val response = try { service.askChap4MedicalAdvisorAgreementResponse(request) } catch(e:SoaErrorException) {
			AskChap4MedicalAdvisorAgreementResponse()
		}
		val retrievedKmehrResponse = responseBuilder.validateTimestampAndretrieveChapterIVKmehrResponseWithTimeStampInfo(response).kmehrresponse

		val agreementResponse = AgreementResponse()
        agreementResponse.isAcknowledged = retrievedKmehrResponse.acknowledge != null && retrievedKmehrResponse.acknowledge.isIscomplete
        agreementResponse.warnings = retrievedKmehrResponse.acknowledge?.warnings?.map {errorType -> errorType?.let { Problem(it.cds, getWarningDescription(it, demandMessages), it.url)}} ?: ArrayList<Problem>()
        agreementResponse.errors = retrievedKmehrResponse.acknowledge?.errors?.map {errorType -> errorType?.let { Problem(it.cds, getWarningDescription(it, demandMessages), it.url)}} ?: ArrayList<Problem>()

        if (retrievedKmehrResponse.kmehrmessage != null) {
            val mh = MarshallerHelper(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java, be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java)
            agreementResponse.content = mh.toXMLByteArray(retrievedKmehrResponse.kmehrmessage)
        }

        if (agreementResponse.content != null) {
			retrievedKmehrResponse.kmehrmessage.apply {
                includeMessageInResponse(agreementResponse, this)
            }
        }
        if (agreementResponse.isAcknowledged) {
            val descr = paragraph + ":Demande d'accord"
            val document = Document()
            document.id = idg.newGUID().toString()
            document.attachment = msg

            document.name = descr

            document.mainUti = "public.xml"
            val dbMessage = Message()
            dbMessage.id = idg.newGUID().toString()

            dbMessage.transportGuid = "CHAP4OUT:" + ref
            dbMessage.toAddresses = Collections.singleton("CHAP4OUT")
            dbMessage.fromHealthcarePartyId = hcp.id

            dbMessage.sent = Instant.now().toEpochMilli()
            dbMessage.received = dbMessage.sent
            dbMessage.status = Message.STATUS_UNREAD
            dbMessage.subject = descr

            agreementResponse.document = mapper!!.map(documentLogic!!.createDocument(document, hcp.id), DocumentDto::class.java)
            agreementResponse.message = mapper!!.map(messageLogic!!.createMessage(dbMessage), MessageDto::class.java)
        }

        agreementResponse.transactions.forEach { at ->
            val descr = if (at.isAccepted) paragraph + ":Accord accepté" else if (at.isInTreatment) paragraph + ":Accord en attente" else paragraph + ":Accord rejetée"
            val document = Document()
            document.id = idg.newGUID().toString()
            document.attachment = at.content

            document.name = descr

            document.mainUti = "public.xml"
            val dbMessage = Message()
            dbMessage.id = idg.newGUID().toString()

            dbMessage.transportGuid = "CHAP4IN:" + ref
            dbMessage.fromAddress = "CHAP4IN"
            dbMessage.recipients = Collections.singleton(hcp.id)

            val responsetype = if (at.isAccepted) paragraph + "accepted" else if (at.isInTreatment) paragraph + "intreatment" else paragraph + "refusal"
            dbMessage.metas = mapOf(
                    "paragraph" to paragraph,
                    "myref" to at.careProviderReference,
                    "iorref" to at.ioRequestReference,
                    "dref" to at.decisionReference,
                    "response" to responsetype
            )

            dbMessage.sent = Instant.now().toEpochMilli()
            dbMessage.received = dbMessage.sent
            dbMessage.status = Message.STATUS_UNREAD
            dbMessage.subject = descr

            at.document = mapper!!.map(documentLogic!!.createDocument(document, hcp.id), DocumentDto::class.java)
            at.message = mapper!!.map(messageLogic!!.createMessage(dbMessage), MessageDto::class.java)
        }
        return agreementResponse
    }

    override fun agreementRequestsConsultation(token: String, patient: Patient, civicsVersion: String, paragraph: String?, start: Long, end: Long?, reference: String?): AgreementResponse {
        val isTest = config.getProperty("endpoint.ch4.consultation.v1").contains("-acpt")

        val ref = reference ?: "" + System.currentTimeMillis()
        val references = ChapterIVReferences(true)

        val hcp = healthcarePartyLogic!!.getHealthcareParty(sessionLogic!!.currentSessionContext.user.healthcarePartyId)
        val consultationMessage = getConsultationTransaction(hcp, patient, references.commonReference, start, end, civicsVersion,
			paragraph, ref )

        val bos = ByteArrayOutputStream()
        JAXBContext.newInstance(org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller().marshal(consultationMessage, bos)
        val msg = bos.toByteArray()
        val v1Message = JAXBContext.newInstance(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createUnmarshaller().unmarshal(ByteArrayInputStream(msg)) as be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage

        val service = ChapterIVSessionServiceFactory.getChapterIVService()
        val responseBuilder = BuilderFactory.getBuilderFactoryForSession().responseBuilder

        val agreementStartDate = FolderTypeUtils.retrieveConsultationStartDateOrAgreementStartDate(v1Message.folders[0])
        val request = createAgreementRequest(v1Message, isTest, references, ConsultationXmlObjectFactory(), agreementStartDate ?: DateTime()).consultChap4MedicalAdvisorAgreementRequest

        val response = service.consultChap4MedicalAdvisorAgreement(request)

        val retrievedKmehrResponse = responseBuilder.validateTimestampAndretrieveChapterIVKmehrResponseWithTimeStampInfo(response)

        val agreementResponse = AgreementResponse()
        agreementResponse.isAcknowledged = retrievedKmehrResponse.kmehrresponse.acknowledge.isIscomplete
        agreementResponse.warnings = CollectionUtils.collect(retrievedKmehrResponse.kmehrresponse.acknowledge.warnings) { errorType -> Problem(errorType.cds, getWarningDescription(errorType, consultMessages), errorType.url) }
        agreementResponse.errors = CollectionUtils.collect(retrievedKmehrResponse.kmehrresponse.acknowledge.errors) { errorType -> Problem(errorType.cds, getErrorDescription(errorType, consultMessages), errorType.url) }

        if (retrievedKmehrResponse.kmehrresponse.kmehrmessage != null) {
            val mh = MarshallerHelper(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java, be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java)
            agreementResponse.content = mh.toXMLByteArray(retrievedKmehrResponse.kmehrresponse.kmehrmessage)
        }

        if (agreementResponse.content != null) {
            retrievedKmehrResponse.kmehrresponse.kmehrmessage.apply {
                includeMessageInResponse(agreementResponse, this)
            }
        }
        if (agreementResponse.isAcknowledged) {
            val descr = paragraph + ":Consultation d'accord"
            val document = Document()
            document.id = idg.newGUID().toString()
            document.attachment = agreementResponse.content

            document.name = descr

            document.mainUti = "public.xml"
            val dbMessage = Message()
            dbMessage.id = idg.newGUID().toString()

            dbMessage.transportGuid = "CHAP4IN:" + ref
            dbMessage.fromAddress = "CHAP4IN"
            dbMessage.recipients = Collections.singleton(hcp.id)

            dbMessage.sent = Instant.now().toEpochMilli()
            dbMessage.received = dbMessage.sent
            dbMessage.status = Message.STATUS_UNREAD
            dbMessage.subject = descr

            agreementResponse.document = mapper!!.map(documentLogic!!.createDocument(document, hcp.id), DocumentDto::class.java)
            agreementResponse.message = mapper!!.map(messageLogic!!.createMessage(dbMessage), MessageDto::class.java)
        }
        return agreementResponse
    }

    override fun cancelAgreement(token: String, patient: Patient, decisionReference: String?, iorequestReference: String?): AgreementResponse {
        val folderType: FolderType
        try {
            folderType = getCancelTransaction(healthcarePartyLogic!!.getHealthcareParty(sessionLogic!!.currentSessionContext.user.healthcarePartyId), patient, decisionReference, iorequestReference)
        } catch (e: IOException) {
            val error = generateError(ChapterIVBusinessConnectorException(ChapterIVBusinessConnectorExceptionValues.UNKNOWN_ERROR, e))
            return error
        }

        return agreementModification(token, folderType, decisionReference ?: iorequestReference ?: throw IllegalArgumentException("Either decisionReference or iorequestReference must be provided"))
    }


    override fun closeAgreement(token: String, patient: Patient, decisionReference: String): AgreementResponse {
        val folderType: FolderType
        try {
            folderType = getCloseTransaction(healthcarePartyLogic!!.getHealthcareParty(sessionLogic!!.currentSessionContext.user.healthcarePartyId), patient, decisionReference, null)
        } catch (e: IOException) {
            val error = generateError(ChapterIVBusinessConnectorException(ChapterIVBusinessConnectorExceptionValues.UNKNOWN_ERROR, e))
            return error
        }

        return agreementModification(token, folderType, decisionReference)
    }

    fun agreementModification(token: String, folder: FolderType, reference: String): AgreementResponse {
        val isTest = config.getProperty("endpoint.ch4.admission.v1").contains("-acpt")

        val references = ChapterIVReferences(true)

        val hcp = healthcarePartyLogic!!.getHealthcareParty(sessionLogic!!.currentSessionContext.user.healthcarePartyId)
        val demandMessage = getKmehrMessage(references.commonReference, hcp, folder)

        val bos = ByteArrayOutputStream()
        JAXBContext.newInstance(org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller().marshal(demandMessage, bos)
        val msg = bos.toByteArray()
        val v1Message = JAXBContext.newInstance(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createUnmarshaller().unmarshal(ByteArrayInputStream(msg)) as be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage

        val service = ChapterIVSessionServiceFactory.getChapterIVService()
        val responseBuilder = BuilderFactory.getBuilderFactoryForSession().responseBuilder

        val agreementStartDate = FolderTypeUtils.retrieveConsultationStartDateOrAgreementStartDate(v1Message.folders[0])
        val request = createAgreementRequest(v1Message, isTest, references, AskXmlObjectFactory(), agreementStartDate ?: DateTime()).askChap4MedicalAdvisorAgreementRequest

        val response = service.askChap4MedicalAdvisorAgreementResponse(request)
        val retrievedKmehrResponse = responseBuilder.validateTimestampAndretrieveChapterIVKmehrResponseWithTimeStampInfo(response)

        val agreementResponse = AgreementResponse()
        agreementResponse.isAcknowledged = retrievedKmehrResponse.kmehrresponse.acknowledge.isIscomplete
        agreementResponse.warnings = CollectionUtils.collect(retrievedKmehrResponse.kmehrresponse.acknowledge.warnings) { errorType -> Problem(errorType.cds, getWarningDescription(errorType, demandMessages), errorType.url) }
        agreementResponse.errors = CollectionUtils.collect(retrievedKmehrResponse.kmehrresponse.acknowledge.errors) { errorType -> Problem(errorType.cds, getErrorDescription(errorType, demandMessages), errorType.url) }

        if (retrievedKmehrResponse.kmehrresponse.kmehrmessage != null) {
            val mh = MarshallerHelper(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java, be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java)
            agreementResponse.content = mh.toXMLByteArray(retrievedKmehrResponse.kmehrresponse.kmehrmessage)
        }

        //Parse response
        if (agreementResponse.content != null) {
            retrievedKmehrResponse.kmehrresponse.kmehrmessage.apply {
                includeMessageInResponse(agreementResponse, this)
            }
        }
        if (agreementResponse.isAcknowledged) {
            val descr = "Clôture/Annulation d'accord"
            val document = Document()
            document.id = idg.newGUID().toString()
            document.attachment = msg

            document.name = descr

            document.mainUti = "public.xml"
            val dbMessage = Message()
            dbMessage.id = idg.newGUID().toString()

            dbMessage.transportGuid = "CHAP4OUT:" + reference
            dbMessage.toAddresses = Collections.singleton("CHAP4OUT")
            dbMessage.fromHealthcarePartyId = hcp.id

            dbMessage.sent = Instant.now().toEpochMilli()
            dbMessage.received = dbMessage.sent
            dbMessage.status = Message.STATUS_UNREAD
            dbMessage.subject = descr

            agreementResponse.document = mapper!!.map(documentLogic!!.createDocument(document, hcp.id), DocumentDto::class.java)
            agreementResponse.message = mapper!!.map(messageLogic!!.createMessage(dbMessage), MessageDto::class.java)
        }

        agreementResponse.transactions.forEach { at ->
            val descr = "Réponse à la demande"
            val document = Document()
            document.id = idg.newGUID().toString()
            document.attachment = at.content

            document.name = descr

            document.mainUti = "public.xml"
            val dbMessage = Message()
            dbMessage.id = idg.newGUID().toString()

            dbMessage.transportGuid = "CHAP4IN:" + reference
            dbMessage.fromAddress = "CHAP4IN"
            dbMessage.recipients = Collections.singleton(hcp.id)

            dbMessage.metas = mapOf(
                    "myref" to at.careProviderReference,
                    "iorref" to at.ioRequestReference,
                    "dref" to at.decisionReference
            )

            dbMessage.sent = Instant.now().toEpochMilli()
            dbMessage.received = dbMessage.sent
            dbMessage.status = Message.STATUS_UNREAD
            dbMessage.subject = descr

            at.document = mapper!!.map(documentLogic!!.createDocument(document, hcp.id), DocumentDto::class.java)
            at.message = mapper!!.map(messageLogic!!.createMessage(dbMessage), MessageDto::class.java)
        }
        return agreementResponse

    }

    private fun getErrorDescription(errorType: be.fgov.ehealth.standards.kmehr.schema.v1.ErrorType, messages: List<AbstractMessage>): String {
        val result = StringBuilder(errorType.description.value).append("\n")

        var flag = false
        if (errorType.url != null) {
            for (cd in errorType.cds) {
                if (cd.value != null) {
                    for (m in messages) {
                        if (m.pattern != null && m.pattern.length > 0 && cd.value == m.code && errorType.url.contains(m.pattern)) {
                            result.append(m.message["fr"]).append(" in zone ").append(m.zone).append("\n")
                            flag = true
                        }
                    }
                }
            }
        }

        if (!flag) {
            for (cd in errorType.cds) {
                if (cd.value != null) {
                    for (m in messages) {
                        if (cd.value == m.code) {
                            result.append(m.message["fr"]).append(" in zone ").append(m.zone).append(" ?\n")
                        }
                    }
                }
            }
        }

        return result.toString()
    }

    private fun getWarningDescription(errorType: be.fgov.ehealth.standards.kmehr.schema.v1.ErrorType, messages: List<AbstractMessage>): String {
        val result = StringBuilder(errorType?.description?.value ?: "")

        for (cd in errorType?.cds ?: ArrayList<CDERROR>()) {
            if (cd.value != null) {
                for (m in messages) {
                    if (m is WarningMessage) {
                        if (cd.value == m.getCode()) {
                            result.append(m.getMessage()["fr"]).append("\n")
                        }
                    }
                }
            }
        }

        return result.toString()
    }

    private fun includeMessageInResponse(agreementResponse: AgreementResponse, kmsg: Kmehrmessage) {
        val v1LOCAL = be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes.LOCAL
        val v1CDITEMMAA = be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes.CD_ITEM_MAA
        val v1CDMAARESPONSETYPE = be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes.CD_MAA_RESPONSETYPE
		val v1CDCHAPTER4PARAGRAPH = be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes.CD_CHAPTER_4_PARAGRAPH

        kmsg.folders.forEach { f ->
            f.transactions?.forEach { t ->
				val at = agreementResponse.addTransaction(AgreementTransaction())
                val its = t?.getItem()
                its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == RESPONSETYPE.value() } }?.contents?.map { it.cds?.find { it.s == v1CDMAARESPONSETYPE }?.value }?.find { it != null }?.let {
                    at.isAccepted = it == AGREEMENT.value()
                    at.isInTreatment = it == INTREATMENT.value()
                }
                at.careProviderReference = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == CAREPROVIDERREFERENCE.value() } }?.contents?.map { it.texts?.firstOrNull()?.value }?.firstOrNull()
                at.decisionReference = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == DECISIONREFERENCE.value() } }?.contents?.map { it.ids?.find { it.s == v1LOCAL }?.value }?.find { it != null }
                at.ioRequestReference = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == IOREQUESTREFERENCE.value() } }?.contents?.map { it.ids?.find { it.s == v1LOCAL }?.value }?.find { it != null }
                at.start = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == AGREEMENTSTARTDATE.value() } }?.contents?.map { it.date }?.find { it != null }?.toGregorianCalendar()?.time
                at.end = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == AGREEMENTENDDATE.value() } }?.contents?.map { it.date }?.find { it != null }?.toGregorianCalendar()?.time
                at.unitNumber = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == UNITNUMBER.value() } }?.contents?.map { it.decimal }?.find { it != null }?.toDouble()
                //TODO at.strength = its.find { it.cds.any { it.s == be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes.CD_ITEM_MAA && it.value == UNITNUMBER.value() } }?.contents?.map { it.decimal }?.find {it != null}?.toDouble()
                at.responseType = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == RESPONSETYPE.value() } }?.contents?.map { it.cds?.find { it.s == v1CDMAARESPONSETYPE }?.value }?.find { it != null }
				at.paragraph = its?.find { it.cds.any { it.s == v1CDITEMMAA && it.value == CHAPTER_4_REFERENCE.value() } }?.contents?.map { it.cds?.find { it.s == v1CDCHAPTER4PARAGRAPH }?.value }?.find { it != null }
				val bos = ByteArrayOutputStream()
				JAXBContext.newInstance(be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage::class.java).createMarshaller().marshal(Kmehrmessage().apply { folders.add(be.fgov.ehealth.standards.kmehr.schema.v1.FolderType().apply { transactions.add(t) })}, bos)
				at.content = bos.toByteArray()
			}
        }
    }

    private fun generateError(e: ChapterIVBusinessConnectorException): AgreementResponse {
        val error = AgreementResponse()
        error.isAcknowledged = false
        val ec = be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR()
        ec.s = be.fgov.ehealth.standards.kmehr.cd.v1.CDERRORschemes.CD_ERROR
        ec.value = e.errorCode + ":" + e.message

        error.errors = Arrays.asList(Problem(arrayListOf(ec), e.message, null))
        return error
    }

    private fun generateError(e: SoaErrorException): AgreementResponse {
        val error = AgreementResponse()
        error.isAcknowledged = false

        val rt = e.responseType as AbstractChap4MedicalAdvisorAgreementResponseType

        val ec = be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR()
        ec.s = be.fgov.ehealth.standards.kmehr.cd.v1.CDERRORschemes.CD_ERROR
        ec.value = rt.returnInfo.faultCode + ":" + rt.returnInfo.faultSource
        error.errors = Arrays.asList(Problem(Arrays.asList(ec), rt.returnInfo.message.value, null))
        return error
    }

    override fun getDemandKmehrMessage(sender: HealthcareParty, patient: Patient, requestType: RequestType, commonInput: String, civicsVersion: String, incomplete: Boolean?, start: Long?, end: Long?, verses: List<String>?, appendices: List<Appendix>?, reference: String?, decisionReference: String?, ioRequestReference: String?, paragraph: String?): org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage {
        val startDate = start?.let { FuzzyValues.getDateTime(it) } ?: LocalDateTime.now().minus(12, ChronoUnit.MONTHS)
        val endDate = end?.let { FuzzyValues.getDateTime(it) }

        return getKmehrMessage(commonInput, sender, FolderType().apply {
            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "1" })
            this.patient = makePersonBase(patient)
            transactions.add(TransactionType().apply {
                initialiseTransactionTypeWithSender(sender, "agreementrequest", requestType, kmehrId = "1")

                if (requestType != RequestType.complimentaryannex) {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                        cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = AGREEMENTSTARTDATE.value() })
                        contents.add(ContentType().apply {
                            date = makeXGC(startDate.toInstant(ZoneOffset.UTC).toEpochMilli())
                        })
                    })

                    endDate?.let {
                        headingsAndItemsAndTexts.add(ItemType().apply {
                            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                            cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = AGREEMENTENDDATE.value() })
                            contents.add(ContentType().apply {
                                date = makeXGC(endDate.toInstant(ZoneOffset.UTC).toEpochMilli())
                            })
                        })
                    }
                }

                reference?.let {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                        cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CAREPROVIDERREFERENCE.value() })
                        contents.add(ContentType().apply {
                            texts.add(TextType().apply { l = "FR"; value = reference })
                        })
                    })
                }

                decisionReference?.let {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "3" })
                        cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = DECISIONREFERENCE.value() })
                        contents.add(ContentType().apply {
                            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "OAreferencesystemname"; value = decisionReference })
                        })
                    })
                } ?: ioRequestReference?.let {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "3" })
                        cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = IOREQUESTREFERENCE.value() })
                        contents.add(ContentType().apply {
                            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "OAreferencesystemname"; value = ioRequestReference })
                        })
                    })
                }

                if (requestType != RequestType.complimentaryannex) {
                    paragraph?.let {
                        headingsAndItemsAndTexts.add(ItemType().apply {
                            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                            cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CHAPTER_4_REFERENCE.value() })
                            contents.add(ContentType().apply {
                                cds.add(CDCONTENT().apply { s = CD_CHAPTER_4_PARAGRAPH; sv = civicsVersion; value = paragraph })
                            })
                            if (verses?.isNotEmpty() ?: false) {
                                contents.add(ContentType().apply {
                                    verses?.forEach {
                                        cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_CHAPTER_4_VERSE); sv = civicsVersion; value = it })
                                    }
                                })
                            }
                        })
                    }
                }
            })
            appendices?.forEach { app ->
                if (app.data != null && app.mimeType != null) {
                    transactions.add(TransactionType().apply {
                        initialiseTransactionTypeWithSender(sender, app.verseSeq?.let { "reglementaryappendix" } ?: "freeappendix", kmehrId = (transactions.size + 1).toString())

                        headingsAndItemsAndTexts.add(ItemType().apply {
                            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                            cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CHAPTER_4_ANNEXREFERENCE.value() })
                            contents.add(ContentType().apply {
                                cds.add(CDCONTENT().apply { s = CD_CHAPTER_4_PARAGRAPH; sv = civicsVersion; value = paragraph })
                                app.verseSeq?.let { cds.add(CDCONTENT().apply { s = CD_CHAPTER_4_VERSESEQAPPENDIX; sv = civicsVersion; value = it.toString() }) }
                                app.documentSeq?.let { cds.add(CDCONTENT().apply { s = CD_CHAPTER_4_DOCUMENTSEQAPPENDIX; sv = civicsVersion; value = it.toString() }) }
                            })
                        })

                        lnks.add(LnkType().apply {
                            type = MULTIMEDIA;
                            try {
                                mediatype = CDMEDIATYPEvalues.fromValue(app.mimeType)
                            } catch(e: Exception) {
                                mediatype = CDMEDIATYPEvalues.TEXT_XML
                            }
                            value = app.data })
                        lnks.add(LnkType().apply { type = ISANAPPENDIXOF; url = "//folder[position()=1]" })
                    })
                }
            }
        })
    }

    @Throws(IOException::class)
    override fun getConsultationTransaction(sender: HealthcareParty, patient: Patient, commonInput: String, start: Long?, end: Long?, civicsVersion: String, paragraph: String?, reference: String?): org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage {
        val startDate = start?.let { FuzzyValues.getDateTime(it) } ?: LocalDateTime.now().minus(12, ChronoUnit.MONTHS)
        val endDate = end?.let { FuzzyValues.getDateTime(it) } ?: startDate.plus(23, ChronoUnit.MONTHS)

        return getKmehrMessage(commonInput, sender, FolderType().apply {
            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "1" })
            this.patient = makePersonBase(patient)

            transactions.add(TransactionType().apply {
                initialiseTransactionTypeWithSender(sender, "consultationrequest")
                headingsAndItemsAndTexts.add(ItemType().apply {
                    ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                    cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CONSULTATIONSTARTDATE.value() })
                    contents.add(ContentType().apply {
                        date = makeXGC(startDate.toInstant(ZoneOffset.UTC).toEpochMilli())
                    })
                })

                headingsAndItemsAndTexts.add(ItemType().apply {
                    ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                    cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CONSULTATIONENDDATE.value() })
                    contents.add(ContentType().apply {
                        date = makeXGC(endDate.toInstant(ZoneOffset.UTC).toEpochMilli())
                    })
                })

                reference?.let {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
                        cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CAREPROVIDERREFERENCE.value() })
                        contents.add(ContentType().apply {
                            texts.add(TextType().apply { l = "FR"; value = reference })
                        })
                    })
                }

				paragraph?.let { p ->
					headingsAndItemsAndTexts.add(ItemType().apply {
						ids.add(IDKMEHR().apply { s = ID_KMEHR; value = (headingsAndItemsAndTexts.size + 1).toString() })
						cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = CHAPTER_4_REFERENCE.value() })
						contents.add(ContentType().apply {
							cds.add(CDCONTENT().apply { s = CD_CHAPTER_4_PARAGRAPH; sv = civicsVersion; value = p })
						})
					})
				}
            })
        })
    }

    private fun getKmehrMessage(commonInput: String, sender: HealthcareParty, folderType: FolderType): org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage {
        return org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage().apply {
            val inami = sender.nihii?.replace("[^0-9]".toRegex(), "")
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { value = "20121001" } }
                makeXGC(Instant.now().toEpochMilli()).let {
                    date = it
                    time = it
                }
                ids.add(IDKMEHR().apply { s = ID_KMEHR; value = inami + '.' + commonInput })
                this.sender = SenderType().apply {
                    hcparties.add(HcpartyType().apply {
                        ids.add(IDHCPARTY().apply { s = ID_HCPARTY; value = inami })
                        sender.ssin.let { ssin -> ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.INSS; value = ssin }) }
                        cds.add(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; value = "persphysician" })
                        name = (sender.firstName?.plus(" ") ?: "") + (sender.lastName ?: "")
                    })
                }
                recipients.add(RecipientType().apply {
                    hcparties.add(HcpartyType().apply {
                        cds.add(CDHCPARTY().apply { s = CDHCPARTYschemes.CD_HCPARTY; value = "application" })
                        name = "mycarenet"
                    })
                })
            }
            folders.add(folderType)
        }
    }

    override fun getCancelTransaction(sender: HealthcareParty, patient: Patient, decisionReference: String?, ioRequestReference: String?, date: Date?): FolderType {
        return FolderType().apply {
            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "1" })
            this.patient = makePersonBase(patient)
            transactions.add(TransactionType().apply {
                initialiseTransactionTypeWithSender(sender, "agreementrequest", cancellation, date)

                decisionReference?.let {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "3" })
                        val scheme = CD_ITEM_MAA
                        cds.add(CDITEM().apply { s(scheme); value = DECISIONREFERENCE.value() })
                        contents.add(ContentType().apply {
                            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "OAreferencesystemname"; value = decisionReference })
                        })
                    })
                } ?: ioRequestReference?.let {
                    headingsAndItemsAndTexts.add(ItemType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "3" })
                        cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = IOREQUESTREFERENCE.value() })
                        contents.add(ContentType().apply {
                            ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "OAreferencesystemname"; value = ioRequestReference })
                        })
                    })
                } ?: throw IllegalArgumentException("Any of decisionReference or iorequestReference should be included")
            })
        }
    }

    override fun getCloseTransaction(sender: HealthcareParty, patient: Patient, decisionReference: String, date: Date?): FolderType {
        return FolderType().apply {
            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "1" })
            this.patient = makePersonBase(patient)
            transactions.add(TransactionType().apply {
                initialiseTransactionTypeWithSender(sender, "agreementrequest", closure, date)

                headingsAndItemsAndTexts.add(ItemType().apply {
                    ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "3" })
                    cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = DECISIONREFERENCE.value() })
                    contents.add(ContentType().apply {
                        ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = "OAreferencesystemname"; value = decisionReference })
                    })
                })

            })
        }
    }

    private fun TransactionType.initialiseTransactionTypeWithSender(sender: HealthcareParty, maa: String, requestType: RequestType? = null, date: Date? = null, kmehrId: String = "1") {
        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = kmehrId })
        cds.add(CDTRANSACTION().apply { s(CD_TRANSACTION); value = "medicaladvisoragreement" })
        cds.add(CDTRANSACTION().apply { s(CD_TRANSACTION_MAA); value = maa })
        author = AuthorType().apply { hcparties.add(createParty(sender, emptyList())) }
        recorddatetime = makeXGC(Instant.now().toEpochMilli())
        makeXGC(date?.time ?: Instant.now().toEpochMilli()).let {
            this.date = it
            this.time = it
        }
        isIscomplete = true
        isIsvalidated = true

        headingsAndItemsAndTexts.add(ItemType().apply {
            ids.add(IDKMEHR().apply { s = ID_KMEHR; value = kmehrId })
            cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = AGREEMENTTYPE.value() })
            contents.add(ContentType().apply {
                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_MAA_TYPE); value = "chapter4" })
            })
        })
        requestType?.let {
            headingsAndItemsAndTexts.add(ItemType().apply {
                ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "2" })
                cds.add(CDITEM().apply { s(CD_ITEM_MAA); value = REQUESTTYPE.value() })
                contents.add(ContentType().apply {
                    cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_MAA_REQUESTTYPE); value = requestType.name })
                })
            })
        }
    }
}
