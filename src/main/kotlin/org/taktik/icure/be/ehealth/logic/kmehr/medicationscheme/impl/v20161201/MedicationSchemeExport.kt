/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.kmehr.medicationscheme.impl.v20161201

import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.HealthElementLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.asynclogic.UserLogic
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.Utils
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENT
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDCONTENTschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEM
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDITEMschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDLNKvalues
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.cd.v1.LnkType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHRschemes
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ContentType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.ItemType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.RecipientType
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1.TransactionType
import org.taktik.icure.be.ehealth.logic.kmehr.Config
import org.taktik.icure.be.ehealth.logic.kmehr.emitMessage
import org.taktik.icure.be.ehealth.logic.kmehr.v20161201.KmehrExport
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto
import org.taktik.icure.services.external.rest.v1.dto.filter.UnionFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.rest.v1.mapper.embed.ServiceMapper

/**
 * @author Bernard Paulus on 29/05/17.
 */
@Suppress("UNNECESSARY_SAFE_CALL")
@org.springframework.stereotype.Service
class MedicationSchemeExport(
	patientLogic: PatientLogic,
	codeLogic: CodeLogic,
	healthElementLogic: HealthElementLogic,
	healthcarePartyLogic: HealthcarePartyLogic,
	contactLogic: ContactLogic,
	documentLogic: DocumentLogic,
	sessionLogic: AsyncSessionLogic,
	userLogic: UserLogic,
	filters: org.taktik.icure.asynclogic.impl.filter.Filters,
	val serviceMapper: ServiceMapper
) : KmehrExport(patientLogic, codeLogic, healthElementLogic, healthcarePartyLogic, contactLogic, documentLogic, sessionLogic, userLogic, filters) {

	fun exportMedicationScheme(
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
		config: Config = Config(
			_kmehrId = System.currentTimeMillis().toString(),
			date = Utils.makeXGC(Instant.now().toEpochMilli())!!,
			time = Utils.makeXGC(Instant.now().toEpochMilli(), true)!!,
			soft = Config.Software(name = "iCure", version = ICUREVERSION),
			clinicalSummaryType = "",
			defaultLanguage = "en"
		)
	) = flow {

		config.defaultLanguage = if (sender.languages.firstOrNull() == "nl") "nl-BE" else if (sender.languages.firstOrNull() == "de") "de-BE" else "fr-BE"
		config.format = Config.Format.MEDICATIONSCHEME //sumehr and medicationscheme have same exception of patient/usuallanguage field (vitalink)
		val message = initializeMessage(sender, config)
		message.header.recipients.add(
			RecipientType().apply {
				hcparties.add(
					HcpartyType().apply {
						cds.add(CDHCPARTY().apply { s(CDHCPARTYschemes.CD_HCPARTY); value = "application" })
						name = recipientSafe
					}
				)
			}
		)

		val folder = makePatientFolder(
			1,
			patient,
			version,
			sender,
			config,
			language,
			services ?: getActiveServices(sender.id, sfks, listOf("medication"), decryptor),
			recipientSafe,
			serviceAuthors
		)
		emitMessage(message.apply { folders.add(folder) }).collect { emit(it) }
	}

	private suspend fun makePatientFolder(
		patientIndex: Int,
		patient: Patient,
		version: Int?,
		healthcareParty: HealthcareParty,
		config: Config,
		language: String,
		medicationServices: List<Service>,
		recipientSafe: String?,
		serviceAuthors: List<HealthcareParty>?
	): FolderType {

		//creation of Patient
		val folder = FolderType().apply {
			ids.add(idKmehr(patientIndex))
			this.patient = makePatient(patient, config)
		}

		var idkmehrIdx = 1

		folder.transactions.add(
			TransactionType().apply {
				ids.add(idKmehr(idkmehrIdx))
				idkmehrIdx++
				cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.10"; value = "medicationscheme" })
				date = config.date
				time = config.time
				author = AuthorType().apply {
					hcparties.add(createParty(healthcareParty, emptyList()))
					hcparties.add(
						createHubHcParty(
							HealthcareParty(
								id = UUID.randomUUID().toString(),
								nihii = if (recipientSafe == "VITALINK") "1990001916" else if (recipientSafe == "RSW") "1990000035" else "1990000728",
								name = recipientSafe,
								speciality = "hub"
							)
						)
					)
				}

				//TODO: is there a way to quit the .map once we've found what we where looking for ? (or use something else ?)
				val (_idOnSafeName, _idOnSafe, _medicationSchemeSafeVersion) = medicationServices.flatMap { svc ->
					svc.content.values.filter { c ->
						(
							c.medicationValue?.let { m ->
								m.idOnSafes != null && m.medicationSchemeSafeVersion != null
							} == true
							)
					}.map { c -> c.medicationValue }
				}.lastOrNull()?.let {
					Triple("vitalinkuri", it.idOnSafes, it.medicationSchemeSafeVersion)
				} ?: Triple("vitalinkuri", null, null)

				_idOnSafe?.let { idName ->
					ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = idName; sv = "1.0"; value = _idOnSafe })
				}
				isIscomplete = true
				isIsvalidated = true

				//TODO: decide what tho do with the Version On Safe
				this.version = (version ?: (_medicationSchemeSafeVersion ?: 0) + 1).toString()
			}
		)

		var currentMedicationIdx: Int
		var trs = medicationServices.map { svc ->
			svc.content.values.find { c -> c.medicationValue != null }?.let { cnt ->
				cnt.medicationValue?.let { m ->
					listOf(
						TransactionType().apply {
							ids.add(idKmehr(idkmehrIdx))
							currentMedicationIdx = idkmehrIdx
//                            if(m.suspension.isNullOrEmpty()){
//                                m.suspension = emptyList<Suspension>();
//                            }
							idkmehrIdx++
							m.idOnSafes?.let { idOnSafe ->
								ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = m.safeIdName; sv = "1.0"; value = m.idOnSafes })
							}
							cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.10"; value = "medicationschemeelement" })
							date = config.date
							time = config.time
							var tmp = serviceAuthors?.find { aut -> aut.id == svc.author }

							if (tmp != null) {
								author = AuthorType().apply {
									hcparties.add(createParty(tmp))
								}
							} else {
								author = AuthorType().apply {
									hcparties.add(healthcarePartyLogic.getHealthcareParty(svc.author?.let { userLogic.getUser(it)?.healthcarePartyId } ?: healthcareParty.id)?.let { createParty(it) })
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
										contents.add(
											ContentType().apply {
												cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "adaptationflag" })
												cds.add(
													CDCONTENT().apply {
														s(CDCONTENTschemes.CD_MS_ADAPTATION); value = when {
															m.timestampOnSafe == null -> "medication"
															m.timestampOnSafe == svc.modified -> "nochange"
															else -> "posology" //TODO: handle medication and/or posology changes ! allowed values: nochange, medication, posology, treatmentsuspension (medication cannot be changed in Topaz)
														}
													}
												)
											}
										)
									},
									createItemWithContent(svc, itemsIdx++, "medication", listOf(makeContent(language, cnt)!!), language = language, texts = listOfNotNull(makeText(language, cnt)), config = config)
								)
							)

							//handle treatmentsuspension
							//      ITEM: transactionreason: Text
							//      ITEM: medication contains Link to medication <lnk TYPE="isplannedfor" URL="//transaction[id[@S='ID-KMEHR']='18']"/>
							//            Lifecycle: suspended (begin and enddate) or stopped (only begindate)
							m.medicationUse?.let { usage ->
								headingsAndItemsAndTexts.add(
									ItemType().apply {
										ids.add(idKmehr(itemsIdx++))
										cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
										contents.add(
											ContentType().apply {
												cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "medicationuse" })
											}
										)
										contents.add(
											ContentType().apply {
												texts.add(TextType().apply { l = language; value = m.medicationUse })
											}
										)
									}
								)
							}

							m.beginCondition?.let { cond ->
								headingsAndItemsAndTexts.add(
									ItemType().apply {
										ids.add(idKmehr(itemsIdx++))
										cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
										contents.add(
											ContentType().apply {
												cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "begincondition" })
											}
										)
										contents.add(
											ContentType().apply {
												texts.add(TextType().apply { l = language; value = m.beginCondition })
											}
										)
									}
								)
							}

							//endcondition is now used as 'endreason', Medication will need extra field for endreason --> MS-6840
//                            m.endCondition?.let { cond ->
//                                headingsAndItemsAndTexts.add(
//                                        ItemType().apply {
//                                            ids.add(idKmehr(itemsIdx++))
//                                            cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
//                                            contents.add(ContentType().apply {
//                                                cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "endcondition" })
//                                            })
//                                            contents.add(ContentType().apply {
//                                                texts.add(TextType().apply { l = language; value = m.endCondition })
//                                            })
//                                        })
//                            }

							m.origin?.let { cond ->
								headingsAndItemsAndTexts.add(
									ItemType().apply {
										ids.add(idKmehr(itemsIdx++))
										cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "healthcareelement" })
										contents.add(
											ContentType().apply {
												cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_ITEM_MS); value = "origin" })
												cds.add(CDCONTENT().apply { s(CDCONTENTschemes.CD_MS_ADAPTATION); value = m.origin })
											}
										)
									}
								)
							}
						},
						*(m.suspension ?: listOf()).map { suspension ->
							TransactionType().apply {
								ids.add(idKmehr(idkmehrIdx))
								idkmehrIdx++
								m.idOnSafes?.let { idOnSafe ->
									ids.add(IDKMEHR().apply { s = IDKMEHRschemes.LOCAL; sl = m.safeIdName; sv = "1.0"; value = m.idOnSafes })
								}
								cds.add(CDTRANSACTION().apply { s = CDTRANSACTIONschemes.CD_TRANSACTION; sv = "1.10"; value = "treatmentsuspension" })
								date = config.date
								time = config.time
								var tmp = serviceAuthors?.find { aut -> aut.id == svc.author }
								if (tmp != null) {
									author = AuthorType().apply {
										hcparties.add(createParty(tmp))
									}
								} else {
									author = AuthorType().apply {
										hcparties.add(healthcarePartyLogic.getHealthcareParty(svc.author?.let { userLogic.getUser(it)?.healthcarePartyId } ?: healthcareParty.id)?.let { createParty(it) })
									}
								}
								isIscomplete = true
								isIsvalidated = true
								var itemsIdx = 1
								//The treatmentsuspension transaction contains a copy of the medication where only beginmoment and endmoment are changed and equal to the duration of the suspension
								headingsAndItemsAndTexts.addAll(
									listOf(
										createItemWithContent(
											svc, itemsIdx++, "medication", listOf(makeContent(language, cnt)!!), language = language, texts = listOfNotNull(makeText(language, cnt)),
											link = LnkType().apply {
												type = CDLNKvalues.ISPLANNEDFOR
												url = "//transaction[id[@S='ID-KMEHR']='$currentMedicationIdx']"
											},
											config = config, altBeginMoment = suspension.beginMoment, altEndMoment = suspension.endMoment
										),
										ItemType().apply {
											ids.add(idKmehr(itemsIdx++))
											cds.add(CDITEM().apply { s(CDITEMschemes.CD_ITEM); value = "transactionreason" })
											contents.add(ContentType().apply { texts.add(TextType().apply { l = language; value = suspension.suspensionReason!! }) })
										}
									)
								)
							}
						}.toTypedArray()
					)
				}
			}
		}

		var flatTrs = trs.filterNotNull().flatten()

		folder.transactions.addAll(flatTrs)
		return folder
	}

	private suspend fun getActiveServices(hcPartyId: String, sfks: List<String>, cdItems: List<String>, decryptor: AsyncDecrypt?): List<Service> {
		val hcPartyIds = healthcarePartyLogic.getHealthcareParty(hcPartyId)?.let { healthcarePartyLogic.getHcpHierarchyIds(it) } ?: HashSet()

		val f = UnionFilter(
			null,
			hcPartyIds.map { hcpId ->
				UnionFilter(
					null,
					sfks.map { k ->
						UnionFilter(
							null,
							cdItems.map { cd ->

								ServiceByHcPartyTagCodeDateFilter(hcpId, k, "CD-ITEM", cd, null, null, null, null)
							}
						)
					}
				)
			}
		)

		var services = filters.resolve(f).toList().let { contactLogic.getServices(it) }.filter { s ->
			s.endOfLife == null && //Not end of lifed
				!((((s.status ?: 0) and 1) != 0) || s.tags?.any { it.type == "CD-LIFECYCLE" && (it.code == "inactive" || it.code == "stopped") }) && //Inactive
				(s.content.values.any { null != (it.binaryValue ?: it.booleanValue ?: it.documentId ?: it.instantValue ?: it.measureValue ?: it.medicationValue) || it.stringValue?.length ?: 0 > 0 } || s.encryptedContent?.length ?: 0 > 0 || s.encryptedSelf?.length ?: 0 > 0) //And content
		}.toList()

		val toBeDecryptedServices = services?.filter { it.encryptedContent?.length ?: 0 > 0 || it.encryptedSelf?.length ?: 0 > 0 }?.toList()

		return if (decryptor != null && toBeDecryptedServices?.size > 0) {
			val decryptedServices = decryptor.decrypt(toBeDecryptedServices?.map { serviceMapper.map(it) }, ServiceDto::class.java).map { serviceMapper.map(it) }
			services.map { if (toBeDecryptedServices.contains(it)) decryptedServices[toBeDecryptedServices.indexOf(it)] else it }
		} else services
	}

	override fun addServiceCodesAndTags(svc: Service, item: ItemType, skipCdItem: Boolean, restrictedTypes: List<String>?, uniqueTypes: List<String>?, excludedTypes: List<String>?) {
		super.addServiceCodesAndTags(
			svc, item, skipCdItem, restrictedTypes, uniqueTypes,
			(
				excludedTypes
					?: emptyList()
				) + listOf("LOCAL", "RELEVANCE", "SUMEHR", "SOAP", "CD-TRANSACTION", "CD-TRANSACTION-TYPE")
		)
	}
}
