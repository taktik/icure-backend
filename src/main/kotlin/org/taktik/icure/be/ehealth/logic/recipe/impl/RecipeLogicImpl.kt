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

@file:Suppress("DEPRECATION")

package org.taktik.icure.be.ehealth.logic.recipe.impl

import be.ehealth.businessconnector.recipe.exception.RecipeBusinessConnectorException
import be.ehealth.businessconnector.recipe.session.RecipeSessionServiceFactory
import be.ehealth.technicalconnector.exception.ConnectorException
import be.ehealth.technicalconnector.handler.ErrorCollectorHandler
import be.recipe.services.GetPrescriptionForPrescriberResult
import com.google.common.cache.CacheBuilder
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import org.taktik.icure.be.drugs.dto.MppId
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException
import org.taktik.icure.be.ehealth.TokenNotAvailableException
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes.CD_HCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDHEADINGschemes.CD_HEADING
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes.CD_ITEM
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes.CD_TRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.cd.v1.CDUNITschemes.CD_UNIT
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDHCPARTYschemes.ID_HCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes.ID_KMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.id.v1.IDPATIENTschemes.ID_PATIENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.logic.kmehr.v20121001.KmehrExport
import org.taktik.icure.be.ehealth.logic.recipe.RecipeLogic
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Medication
import org.taktik.icure.logic.ICureLogic
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.nio.charset.Charset
import java.security.KeyStoreException
import java.security.cert.CertificateExpiredException
import java.time.Instant
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.zip.DataFormatException
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

/**
 * Created with IntelliJ IDEA.
 * User: aduchateTechn
 * Date: 16/06/13
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
@Service
class RecipeLogicImpl : KmehrExport(), RecipeLogic {
    internal val ridCache = CacheBuilder.newBuilder().build<String, GetPrescriptionForPrescriberResult>()
    internal val feedbacksCache = CacheBuilder.newBuilder().build<String, SortedSet<Feedback>>()

    override val log = LogFactory.getLog(this.javaClass)

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    override fun revokePrescription(token: String, rid: String, reason: String) {
        val service = RecipeSessionServiceFactory.getRecipePrescriberService()
        service.revokePrescription(rid, reason)
    }

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    override fun updateFeedbackFlag(token: String, rid: String, feedbackFlag: Boolean) {
        val service = RecipeSessionServiceFactory.getRecipePrescriberService()
        service.updateFeedbackFlag(rid, feedbackFlag)

        ridCache.getIfPresent(rid)?.let {
            it.isFeedbackAllowed = feedbackFlag
        }
    }

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, DataFormatException::class, KeyStoreException::class, CertificateExpiredException::class)
    override fun sendNotification(token: String, patientId: String, executorId: String, rid: String, text: String) {
        val service = RecipeSessionServiceFactory.getRecipePrescriberService()

        val os = ByteArrayOutputStream()
        JAXBContext.newInstance(RecipeNotification::class.java).createMarshaller().marshal(RecipeNotification().apply {  this.text = text; kmehrmessage = getPrescriptionMessage(rid) },os)
        val bytes = os.toByteArray()

        service.sendNotification(bytes, java.lang.Long.parseLong(patientId), java.lang.Long.parseLong(executorId))
    }

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, DataFormatException::class)
    private fun getPrescriptionMessage(rid: String): Kmehrmessage {
        val service = RecipeSessionServiceFactory.getRecipePrescriberService()
        val p = service.getPrescription(rid)

        return JAXBContext.newInstance(Kmehrmessage::class.java).createUnmarshaller().unmarshal(ByteArrayInputStream(p.prescription)) as Kmehrmessage
    }

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, DataFormatException::class, KeyStoreException::class, CertificateExpiredException::class)
    override fun listFeedbacks(token: String): List<Feedback> {
        val service = RecipeSessionServiceFactory.getRecipePrescriberService()
        val feedbackItemList = service.listFeedback(true)
        return feedbackItemList.map { Feedback(it.rid, it.sentBy, it.sentDate?.toDate(), it.content?.toString(Charset.forName("UTF-8"))) }
    }

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    override fun listOpenPrescriptions(token: String): List<Prescription> {
        val service = RecipeSessionServiceFactory.getRecipePrescriberService()
        val ridList = service.listOpenPrescription()

        val es = Executors.newFixedThreadPool(5)
        try {
            val getFeedback = es.submit<List<Feedback>> { listFeedbacks(token) }
            val futures = es.invokeAll<GetPrescriptionForPrescriberResult>(ridList.map { rid -> Callable<GetPrescriptionForPrescriberResult>{ ridCache[rid, { service.getPrescription(rid) }] } })
            val result = futures.map {f -> f.get()}.map { r -> Prescription(r.creationDate.toDate(),r.encryptionKeyId,r.rid,r.isFeedbackAllowed,r.patientId) }

            try {
                for (d in getFeedback.get()) {  feedbacksCache[d.rid!!, { TreeSet() }].add(d) }
            } catch (e: ExecutionException) {
                log.error(e)
            }

            es.shutdown()
            return result
        } catch (e: InterruptedException) {
            log.error(e)
        }

        return emptyList()
    }

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    override fun listOpenPrescriptions(token: String, patientId: String): List<Prescription> {
        val prescritpionsList = listOpenPrescriptions(token)
        val patId = java.lang.Long.parseLong(patientId)
        return prescritpionsList.filter { it.patientId == patId }
    }

    @Throws(JAXBException::class)
    override fun getPrescription(rid: String): PrescriptionFullWithFeedback? {
        val r = ridCache.getIfPresent(rid) ?: return null
        val fd = feedbacksCache.getIfPresent(rid)
        val result = PrescriptionFullWithFeedback(r.creationDate.toDate(), r.encryptionKeyId, r.rid, r.isFeedbackAllowed, r.patientId)
        fd?.let { result.feedbacks = ArrayList(fd) }
        //TODO result.setPatientName(patientLogic.getPatientWithInss(result.getPatientId().toString()).getFullName());

        val jaxbContext = JAXBContext.newInstance(Kmehrmessage::class.java)
        val jaxbUnmarshaller = jaxbContext.createUnmarshaller()
        val pm = jaxbUnmarshaller.unmarshal(ByteArrayInputStream(r.prescription)) as Kmehrmessage

        pm.folders.firstOrNull()?.transactions?.find { t -> t.cds.find { it.s == CD_TRANSACTION }?.value == "pharmaceuticalprescription"}?.let { t -> t.headingsAndItemsAndTexts?.let { hs ->
            result.deliverableTo = t.expirationdate?.toGregorianCalendar()?.time
            t.author?.hcparties?.firstOrNull()?.let { hcp ->
                result.nihii = hcp.ids?.find { it.s == ID_HCPARTY }?.value
                result.fullAuthorName = hcp.name ?: ((hcp.firstname?.plus(" ")?:"") + (hcp.familyname?:""))
            }
            hs.flatMap {
                when(it) {
                    is ItemType -> listOf(it)
                    is HeadingType -> it.headingsAndItemsAndTexts
                    else -> emptyList()
                }
            }.forEach { item ->
                when(item) {
                    is ItemType -> {
                        item.deliverydate?.toGregorianCalendar()?.time?.let { dd -> result.deliverableFrom = if (result.deliverableFrom?.after(dd)?:false) result.deliverableFrom else dd }
                        item.contents.forEach { c -> (
                                c.medicinalproduct?.intendedname ?:
                                c.substanceproduct?.intendedname ?:
                                c?.compoundprescription?.content?.find {item is TextType}.let { (item as TextType).value }
                                ).let {
                            result.medicines.add(it + (item.posology?.let {
                                "\nS/ " + it.text
                            } ?: ""))
                        }
                    }
                }
            }
        }}}
        return result
    }

    fun getKmehrPrescription(patient: Patient, hcp: HealthcareParty, medications: List<Medication>, deliveryDate: Date?, expirationDate: Date?): Kmehrmessage {
        return Kmehrmessage().apply {
            val inami = hcp.nihii?.replace("[^0-9]".toRegex(), "")
            header = HeaderType().apply {
                standard = StandardType().apply { cd = CDSTANDARD().apply { value = "20121001" } }
                makeXGC(Instant.now().toEpochMilli()).let {
                    date = it
                    time = it
                }
                ids.add(IDKMEHR().apply { s = ID_KMEHR; value = inami + '.' + System.currentTimeMillis() })
                sender = SenderType().apply {
                    hcparties.add(HcpartyType().apply {
                        ids.add(IDHCPARTY().apply { s = ID_HCPARTY; value = inami })
                        hcp.ssin.let { ssin -> ids.add(IDHCPARTY().apply { s = IDHCPARTYschemes.INSS; value = ssin }) }
                        cds.add(CDHCPARTY().apply { s = CD_HCPARTY; value = "persphysician" })
                        name = (hcp.firstName?.plus(" ")?:"") + (hcp.lastName?:"")
                    })
                }
                recipients.add(RecipientType().apply {
                    hcparties.add(HcpartyType().apply {
                        ids.add(IDHCPARTY().apply { s = ID_HCPARTY; value = "RECIPE" })
                        cds.add(CDHCPARTY().apply { s = CD_HCPARTY; value = "orgpublichealth" })
                        name = "Recip-e"
                    })
                })
            }
            folders.add(FolderType().apply {
                ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "1" })
                this.patient = PersonType().apply {
                    ids.add(IDPATIENT().apply { s = ID_PATIENT; value = patient.ssin })
                    patient.firstName?.let { firstnames.add(it) }
                    familyname = patient.lastName
                    patient.dateOfBirth?.let { birthdate = Utils().makeDateTypeFromFuzzyLong(it.toLong())!! }
                    patient.gender?.name?.let { gender -> sex = SexType().apply { cd = CDSEX().apply {
                        s = "CD-SEX"; value = CDSEXvalues.fromValue(gender)
                    }}}
                }
                transactions.add(TransactionType().apply {
                    ids.add(IDKMEHR().apply { s = ID_KMEHR; sv="1.0"; value = "1" })
                    cds.add(CDTRANSACTION().apply { s(CD_TRANSACTION); value = "pharmaceuticalprescription" })
                    makeXGC(Instant.now().toEpochMilli()).let {
                        date = it
                        time = it
                        recorddatetime = it
                    }
                    expirationDate?.let { expirationdate = makeXGC(expirationDate.time) }
                    author = AuthorType().apply { hcparties.addAll(header.sender.hcparties) }
                    isIscomplete = true
                    isIsvalidated = true
                    headingsAndItemsAndTexts.add(HeadingType().apply {
                        ids.add(IDKMEHR().apply { s = ID_KMEHR; value = "1" })
                        cds.add(CDHEADING().apply { s = CD_HEADING; value = "prescription" })
                        medications.forEachIndexed { idx, med ->
                            headingsAndItemsAndTexts.add(ItemType().apply {
                                ids.add(IDKMEHR().apply { s = ID_KMEHR; value = idx.toString() })
                                cds.add(CDITEM().apply { s(CD_ITEM); value = "medication" })
                                lifecycle = LifecycleType().apply { cd = CDLIFECYCLE().apply { s = "CD-LIFECYCLE"; value = CDLIFECYCLEvalues.PRESCRIBED } }
                                med.commentForDelivery?.let { comment ->
                                    contents.add(ContentType().apply { texts.add(TextType().apply { l = "FR"; value = comment }) })
                                }
                                med.medicinalProduct?.intendedcds?.let { contents.addAll(it.filter { it.type == "CD-DRUG-CNK" }.map { c ->
                                    ContentType().apply {
                                        medicinalproduct = ContentType.Medicinalproduct().apply {
                                            intendedcd = CDDRUGCNK().apply { value = c.code }
                                            intendedname = med.medicinalProduct?.intendedname
                                        }
                                    }
                                })}
                                med.substanceProduct?.intendedcds?.let { contents.addAll(it.filter { it.type == "CD-INNCLUSTER" }.map { c ->
                                    ContentType().apply {
                                        substanceproduct = ContentType.Substanceproduct().apply {
                                            intendedcd = CDINNCLUSTER().apply { s = "CD-INNCLUSTER"; value = c.code }
                                            intendedname = med.substanceProduct?.intendedname
                                        }
                                    }
                                })}
                                if (contents.size == 0) {
                                    (med.compoundPrescription ?: med.medicinalProduct?.intendedname ?: med.substanceProduct?.intendedname)?.let { text ->
                                        contents.add(ContentType().apply { compoundprescription = CompoundprescriptionType().apply { content.add(
                                                text //TextType().apply { l = "FR"; value = text }
                                        ) } })
                                    }
                                }
                                val posologyText = med.posology
                                if (!StringUtils.isEmpty(posologyText)) {
                                    posology = ItemType.Posology().apply { text = TextType().apply { l = "FR"; value = posologyText } }
                                }
                                quantity = QuantityType().apply {
                                    decimal = med.numberOfPackages?.let { nr -> BigDecimal.valueOf(nr.toLong()) } ?: BigDecimal.ONE
                                    unit = UnitType().apply { cd = CDUNIT().apply { s = CD_UNIT; value = "pkg" } }
                                }
                                if (!StringUtils.isEmpty(med.batch)) { batch = med.batch }
                                deliverydate = makeXGC(deliveryDate?.time)
                            })
                        }
                    })
                })
            })
        }
    }


    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class)
    override fun createPrescription(token: String?, patient: Patient, hcp: HealthcareParty, feedback: Boolean, medications: List<Medication>, prescriptionType: String?, notification: String?, executorId: String?, deliveryDate: Date?, expirationDate: Date?): Prescription {
        var selectedType = prescriptionType
        if (token == null) {
            throw TokenNotAvailableException("Cannot obtain token for Ehealth Box operations")
        }

        if (selectedType == null) {
            selectedType = "P0"
            OUTER@ for (med in medications) {
                if (med.medicinalProduct?.intendedcds != null && med.medicinalProduct!!.intendedcds.size > 0) {
                    for (c in med.medicinalProduct!!.intendedcds) {
                        if (StringUtils.equals(c.type, "CD-DRUG-CNK")) {
                            val infos = drugsLogic!!.getInfos(MppId(c.code, "fr"))
                            if (!StringUtils.isEmpty(infos?.ssec) && infos?.ssec?.equals("chr", ignoreCase = true) != true) {
                                selectedType = "P1"
                                break@OUTER
                            }
                        }
                    }
                }
                if (med.substanceProduct?.intendedcds != null && med.substanceProduct!!.intendedcds.size > 0) {
                    for (c in med.substanceProduct!!.intendedcds) {
                        if (StringUtils.equals(c.type, "CD-INNCLUSTER")) {
                            for (mpp in drugsLogic!!.getMedecinePackagesFromInn(c.code, "fr")) {
                                val infos = drugsLogic!!.getInfos(mpp.id)
								if (!StringUtils.isEmpty(infos?.ssec) && infos?.ssec?.equals("chr", ignoreCase = true) != true) {
                                    selectedType = "P1"
                                    break@OUTER
                                }
                            }
                        }
                    }
                }
                if (med.options?.get(Medication.REIMBURSED)?.booleanValue ?: false) {
                    selectedType = "P1"
                    break
                }
            }
        }

        val m = getKmehrPrescription(patient, hcp, medications, deliveryDate, expirationDate)

        val os = ByteArrayOutputStream()
        JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller().marshal(m,os)
        val prescription = os.toByteArray()

        val service = RecipeSessionServiceFactory.getRecipePrescriberService()
        val errors = validateStream(prescription)

        val prescriptionId = service.createPrescription(feedback, java.lang.Long.valueOf(patient.ssin?.replace("[^0-9]".toRegex(), ""))!!, prescription, selectedType)

        val result = Prescription(Date(),"",prescriptionId)

        if (notification != null && executorId != null) {
            try {
                val osn = ByteArrayOutputStream()
                JAXBContext.newInstance(RecipeNotification::class.java).createMarshaller().marshal(RecipeNotification().apply { text = notification; kmehrmessage = m  }, osn)
                service.sendNotification(osn.toByteArray(), java.lang.Long.valueOf(patient.ssin?.replace("[^0-9]".toRegex(), ""))!!, java.lang.Long.parseLong(executorId))
                result.notificationWasSent = true
            } catch (e: RecipeBusinessConnectorException) {
                log.error("Notification could not be sent", e)
                result.notificationWasSent = false
            }
        }
        return result
    }

    fun validateStream(xmlDocument : ByteArray) : List<String> {
        val kmehrVersion = "v20121001"
        val s = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(ICureLogic::class.java.getResource("impl/be/ehealth/kmehr/schema/$kmehrVersion/ehealth-kmehr/XSD/kmehr_elements-1_5.xsd"))
        s?.newValidator()?.let { v ->
            val ech = ErrorCollectorHandler()
            v.errorHandler = ech
            v.validate(StreamSource(ByteArrayInputStream(xmlDocument)))

            ech.getExceptionList("WARN").forEach { log.warn(it) }
            return ech.getExceptionList("ERROR","FATAL")
        }
        return emptyList()
    }

}
