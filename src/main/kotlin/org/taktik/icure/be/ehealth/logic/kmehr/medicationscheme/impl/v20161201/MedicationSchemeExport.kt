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

package org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.impl.v20161201

import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.*
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v1.dto.filter.Filters
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

/**
 * @author Bernard Paulus on 29/05/17.
 */
@org.springframework.stereotype.Service
class MedicationSchemeExport : KmehrExport() {

	fun exportMedicationScheme(
			os: OutputStream,
			patient: Patient,
			sfks: List<String>,
			sender: HealthcareParty,
			language: String,
            recipientSafe: String?,
            version: Int?,
            services: List<Service>?,
            serviceAuthors: List<HealthcareParty>?,
			decryptor: AsyncDecrypt?,
			progressor: AsyncProgress?,
			config: Config = Config(_kmehrId = System.currentTimeMillis().toString(),
                                         date = makeXGC(Instant.now().toEpochMilli())!!,
                                         time = makeXGC(Instant.now().toEpochMilli(), true)!!,
                                         soft = Config.Software(name = "iCure", version = ICUREVERSION),
                                         clinicalSummaryType = "",
                                         defaultLanguage = "en"
                                        )) {

		val message = initializeMessage(sender, config)
		message.header.recipients.add(RecipientType().apply {
			hcparties.add(HcpartyType().apply {
				cds.add(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "application" })
				name = recipientSafe
			})
		})

		// TODO split marshalling
		message.folders.add(makePatientFolder(1, patient, version, sender, config, language, services ?: getActiveServices(sender.id, sfks, listOf("medication"), decryptor), serviceAuthors, decryptor, progressor))

        val jaxbMarshaller = JAXBContext.newInstance(Kmehrmessage::class.java).createMarshaller()

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")

		jaxbMarshaller.marshal(message, OutputStreamWriter(os, "UTF-8"))
	}


	private fun makePatientFolder(patientIndex: Int, patient: Patient, version: Int?, healthcareParty: HealthcareParty,
                                  config: Config, language: String, medicationServices: List<Service>, serviceAuthors: List<HealthcareParty>?, decryptor: AsyncDecrypt?, progressor: AsyncProgress?): FolderType {

		//creation of Patient
        val folder = FolderType().apply {
			ids.add(idKmehr(patientIndex))
			this.patient = makePatient(patient, config)
		}

        var idkmehrIdx = 1

        folder.transactions.add(TransactionType().apply {
			ids.add(idKmehr(idkmehrIdx))
            idkmehrIdx++
            cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.10"; value = "medicationscheme" })
			date = config.date
			time = config.time
			author = AuthorType().apply { hcparties.add(createParty(healthcareParty, emptyList())) }

            //TODO: is there a way to quit the .map once we've found what we where looking for ? (or use something else ?)
            val (_idOnSafeName, _idOnSafe, _medicationSchemeSafeVersion) = medicationServices.flatMap { svc ->
                svc.content.values.filter{c ->
                            (c.medicationValue?.let { m ->
                                m.idOnSafes != null && m.medicationSchemeSafeVersion != null
                            } == true)
                }.map{c -> c.medicationValue}
            }.lastOrNull()?.let {
                Triple("vitalinkuri", it.idOnSafes, it.medicationSchemeSafeVersion)
            } ?: Triple("vitalinkuri", null, null)

            _idOnSafe?.let{ idName ->
                ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = idName; sv = "1.0"; value = _idOnSafe})
            }
			isIscomplete = true
			isIsvalidated = true

            //TODO: decide what tho do with the Version On Safe
            this.version = (version ?: (_medicationSchemeSafeVersion ?: 0)+1).toString()
		})

        folder.transactions.addAll(medicationServices.map { svc ->
            svc.content.values.find { c -> c.medicationValue != null }?.let { cnt -> cnt.medicationValue?.let { m ->
            TransactionType().apply {
                ids.add(idKmehr(idkmehrIdx))
                idkmehrIdx++
                m.idOnSafes?.let{idOnSafe ->
                    ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = m.safeIdName; sv = "1.0"; value = m.idOnSafes})
                }
                cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.10"; value = "medicationschemeelement" })
                date = config.date
                time = config.time
                var tmp = serviceAuthors?.find{aut -> aut.id == svc.author}

                if(tmp != null){
                    author = AuthorType().apply {
                        hcparties.add(createParty(tmp))
                    }
                }else {
                    author = AuthorType().apply {
                        hcparties.add(createParty(healthcarePartyLogic!!.getHealthcareParty(svc.author?.let { userLogic!!.getUser(it)?.healthcarePartyId } ?: healthcareParty.id)))
                    }
                }

                isIscomplete = true
                isIsvalidated = true

                var itemsIdx = 1

                headingsAndItemsAndTexts.addAll(//This adds 1 adaptationflag ITEM and 1 medication ITEM
                    listOf(
                        ItemType().apply {
                            ids.add(idKmehr(itemsIdx++))
                            cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
                            contents.add(ContentType().apply {
                                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "adaptationflag" })
                                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_MS_ADAPTATION); value = when {
                                    m.timestampOnSafe == null -> "medication"
                                    m.timestampOnSafe == svc.modified -> "nochange"
                                    else -> "posology" //TODO: handle medication and/or posology changes ! allowed values: nochange, medication, posology, treatmentsuspension (medication cannot be changed in Topaz)
                                }})
                            })
                        },
                        createItemWithContent(svc, itemsIdx++, "medication", listOf(makeContent(language, cnt)!!), language = language)))
                //TODO: handle treatmentsuspension
                //      ITEM: transactionreason: Text
                //      ITEM: medication contains Link to medication <lnk TYPE="isplannedfor" URL="//transaction[id[@S='ID-KMEHR']='18']"/>
                //            Lifecycle: suspended (begin and enddate) or stopped (only begindate)
                m.medicationUse?.let { usage ->
                    headingsAndItemsAndTexts.add(
                        ItemType().apply {
                            ids.add(idKmehr(itemsIdx++))
                            cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
                            contents.add(ContentType().apply {
                                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "medicationuse" })
                            })
                            contents.add(ContentType().apply {
                                texts.add(TextType().apply { l = language; value = m.medicationUse })
                            })
                        })
                }

                m.beginCondition?.let { cond ->
                    headingsAndItemsAndTexts.add(
                        ItemType().apply {
                            ids.add(idKmehr(itemsIdx++))
                            cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
                            contents.add(ContentType().apply {
                                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "begincondition" })
                            })
                            contents.add(ContentType().apply {
                                texts.add(TextType().apply { l = language; value = m.beginCondition })
                            })
                        })
                }

                m.endCondition?.let { cond ->
                    headingsAndItemsAndTexts.add(
                        ItemType().apply {
                            ids.add(idKmehr(itemsIdx++))
                            cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
                            contents.add(ContentType().apply {
                                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "endcondition" })
                            })
                            contents.add(ContentType().apply {
                                texts.add(TextType().apply { l = language; value = m.endCondition })
                            })
                        })
                }

                m.origin?.let { cond ->
                    headingsAndItemsAndTexts.add(
                            ItemType().apply {
                                ids.add(idKmehr(itemsIdx++))
                                cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
                                contents.add(ContentType().apply {
                                    cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "origin" })
                                    cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_MS_ADAPTATION); value = m.origin })
                                })
                            })
                }

            }}}
        })
        return folder
	}


    private fun getActiveServices(hcPartyId: String, sfks: List<String>, cdItems: List<String>, decryptor: AsyncDecrypt?): List<Service> {
        val f = Filters.UnionFilter(sfks.map { k ->
                Filters.UnionFilter(cdItems.map { cd ->
                    ServiceByHcPartyTagCodeDateFilter(hcPartyId, k, "CD-ITEM", cd, null, null, null, null)
                })
            })

        var services = contactLogic?.getServices(filters?.resolve(f))?.filter { s ->
            s.endOfLife == null && //Not end of lifed
                !((((s.status ?: 0) and 1) != 0) || s.tags?.any { it.type == "CD-LIFECYCLE" && (it.code == "inactive" || it.code == "stopped") } ?: false) //Inactive
                && (s.content.values.any { null != (it.binaryValue ?: it.booleanValue ?: it.documentId ?: it.instantValue ?: it.measureValue ?: it.medicationValue) || it.stringValue?.length ?: 0 > 0 } || s.encryptedContent?.length ?: 0 > 0 || s.encryptedSelf?.length ?: 0 > 0) //And content
        }

        val toBeDecryptedServices = services?.filter { it.encryptedContent?.length ?: 0 > 0 || it.encryptedSelf?.length ?: 0 > 0 }

        if (decryptor != null && toBeDecryptedServices?.size ?: 0 > 0) {
            val decryptedServices = decryptor.decrypt(toBeDecryptedServices?.map { mapper!!.map(it, ServiceDto::class.java) }, ServiceDto::class.java).get().map { mapper!!.map(it, Service::class.java) }
            services = services?.map { if (toBeDecryptedServices?.contains(it) == true) decryptedServices[toBeDecryptedServices.indexOf(it)] else it }
            services = services?.filter(Objects::nonNull)
        }

        return services ?: emptyList()
    }


    override fun addServiceCodesAndTags(svc: Service, item: ItemType, skipCdItem: Boolean, restrictedTypes: List<String>?, uniqueTypes: List<String>?, excludedTypes: List<String>?) {
		super.addServiceCodesAndTags(svc, item, skipCdItem, restrictedTypes, uniqueTypes, (excludedTypes
				?: emptyList()) + listOf("LOCAL", "RELEVANCE", "SUMEHR", "SOAP", "CD-TRANSACTION", "CD-TRANSACTION-TYPE"))
	}
}

