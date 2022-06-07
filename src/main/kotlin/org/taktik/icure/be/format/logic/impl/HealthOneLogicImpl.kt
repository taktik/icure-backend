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
package org.taktik.icure.be.format.logic.impl

import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.Reader
import java.io.StringReader
import java.io.UnsupportedEncodingException
import java.nio.charset.UnsupportedCharsetException
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.LinkedList
import java.util.regex.Pattern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asFlow
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.ContactLogic
import org.taktik.icure.asynclogic.DocumentLogic
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.asynclogic.PatientLogic
import org.taktik.icure.be.format.logic.HealthOneLogic
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Content
import org.taktik.icure.entities.embed.Measure
import org.taktik.icure.utils.FuzzyValues

@Service
class HealthOneLogicImpl(healthcarePartyLogic: HealthcarePartyLogic, formLogic: FormLogic, val patientLogic: PatientLogic, val documentLogic: DocumentLogic, val contactLogic: ContactLogic) : GenericResultFormatLogicImpl(healthcarePartyLogic, formLogic), HealthOneLogic {
	private val shortDateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

	/* Import a series of protocols from a document into a contact

	 */
	override suspend fun doImport(language: String, doc: Document, hcpId: String?, protocolIds: List<String>, formIds: List<String>, planOfActionId: String?, ctc: Contact, enckeys: List<String>): Contact? {
		val text = decodeRawData(doc.decryptAttachment(enckeys))
		return if (text != null) {
			val r: Reader = StringReader(text)
			val lls = parseReportsAndLabs(language, protocolIds, r).filterNotNull()
			val subContactsWithServices = fillContactWithLines(lls, planOfActionId, hcpId, protocolIds, formIds)
			contactLogic.modifyContact(ctc.copy(subContacts = ctc.subContacts + subContactsWithServices.map { it.first }, services = ctc.services + subContactsWithServices.flatMap { it.second }))
		} else {
			throw UnsupportedCharsetException("Charset could not be detected")
		}
	}

	@Throws(IOException::class)
	fun parseReportsAndLabs(language: String, protocols: List<String?>, r: Reader): List<LaboLine> {
		val result: MutableList<LaboLine> = LinkedList()
		var line: String? = null
		val reader = BufferedReader(r)
		var ll: LaboLine? = null
		var position: Long = 0
		while ((reader.readLine()?.also { line = it }) != null && position < 10000000L /* ultimate safeguard */) {
			position++
			if (isLaboLine(line!!)) {
				ll?.let { createServices(it, language, position) }
				ll = getLaboLine(line!!)
				if (protocols.contains(ll.resultReference) || protocols.size == 1 && protocols[0] != null && protocols[0]!!.startsWith("***")) {
					result.add(ll)
				} else {
					ll = null
				}
			} else if (ll != null && isLaboResultLine(line!!)) {
				val lrl = getLaboResultLine(line!!, ll)
				if (lrl != null) {
					ll.isResultLabResult = true
					if (ll.labosList.size > 0 && !(lrl.analysisCode == ll.labosList[0]!!.analysisCode && lrl.analysisType == ll.labosList[0]!!.analysisType)) {
						createServices(ll, language, position)
					}
					ll.labosList.add(lrl)
				}
			} else if (ll != null && isProtocolLine(line!!)) {
				val pl = getProtocolLine(line!!)
				if (pl != null) { // Less than 20 lines ... If the codes are different,
// We probably have a bad header... Just concatenate
					if (ll.protoList.size > 20 && pl.code != ll.protoList[ll.protoList.size - 1]!!.code) {
						createServices(ll, language, position)
					}
					ll.protoList.add(pl)
				}
			} else if (ll != null && isResultsInfosLine(line!!)) {
				ll.ril = getResultsInfosLine(line!!)
			} else if (ll != null && isPatientAddressLine(line!!)) {
				ll.pal = getPatientAddressLine(line!!)
			}
		}
		ll?.let { createServices(it, language, position) }
		return result
	}

	protected fun createServices(ll: LaboLine, language: String, position: Long) {
		if (ll.labosList.size > 0 && ll.ril != null) {
			ll.services.addAll(importLaboResult(language, ll.labosList, position, ll.ril!!))
			ll.labosList.clear()
		}
		if (ll.protoList.size > 0 && ll.ril != null) {
			ll.services.add(importProtocol(language, ll.protoList, position, ll.ril!!))
			ll.protoList.clear()
		}
	}

	protected fun importProtocol(language: String, protoList: List<*>, position: Long, ril: ResultsInfosLine): org.taktik.icure.entities.embed.Service {
		var text = (protoList[0] as ProtocolLine).text
		for (i in 1 until protoList.size) {
			text += "\n" + (protoList[i] as ProtocolLine).text
		}
		val s = org.taktik.icure.entities.embed.Service(
			id = uuidGen.newGUID().toString(),
			content = mapOf(language to Content(stringValue = text)),
			label = "Protocol",
			index = position,
			valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS)
		)
		return s
	}

	protected fun importLaboResult(language: String, labResults: List<*>, position: Long, ril: ResultsInfosLine): List<org.taktik.icure.entities.embed.Service> {
		var result: MutableList<org.taktik.icure.entities.embed.Service> = ArrayList()
		if (labResults.size > 1) {
			var lrl = labResults[0] as LaboResultLine
			var comment: String
			if (tryToGetValueAsNumber(lrl.value) != null) {
				val lrl2 = labResults[1] as LaboResultLine
				comment = lrl2.value ?: ""
				for (i in 2 until labResults.size) {
					lrl = labResults[i] as LaboResultLine
					if (StringUtils.isNotEmpty(lrl.value)) {
						comment += "\n" + lrl.value
					}
				}
				result = addLaboResult(labResults[0] as LaboResultLine, language, position, ril, comment)
			} else {
				val label = lrl.analysisType
				var value = lrl.value
				for (i in 1 until labResults.size) {
					lrl = labResults[i] as LaboResultLine
					if (StringUtils.isNotEmpty(lrl.value)) {
						value += "\n" + lrl.value
					}
				}
				val s = org.taktik.icure.entities.embed.Service(
					id = uuidGen.newGUID().toString(),
					content = mapOf(language to Content(stringValue = value)),
					label = label ?: "",
					index = position,
					valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS)
				)
				result.add(s)
			}
		} else {
			result = addLaboResult(labResults[0] as LaboResultLine, language, position, ril, null)
		}
		return result
	}

	protected fun addLaboResult(lrl: LaboResultLine, language: String, position: Long, ril: ResultsInfosLine, comment: String?): MutableList<org.taktik.icure.entities.embed.Service> {
		val result: MutableList<org.taktik.icure.entities.embed.Service> = ArrayList()
		val laboResultLineValue = lrl.value!!.replace("<".toRegex(), "").replace(">".toRegex(), "")
		val d = tryToGetValueAsNumber(laboResultLineValue)
		if (d != null) { //We import as a Measure
			result.add(importNumericLaboResult(language, d, lrl, position, ril, comment))
		} else {
			result.add(importPlainStringLaboResult(language, lrl, position, ril))
		}
		return result
	}

	protected fun importPlainStringLaboResult(language: String, lrl: LaboResultLine, position: Long, ril: ResultsInfosLine): org.taktik.icure.entities.embed.Service {
		val referenceValue = lrl.referenceValues!!.trim { it <= ' ' }
		val severity = lrl.severity?.trim { it <= ' ' }
		var value = "${lrl.value} ${lrl.unit}" + (
			if (referenceValue.isNotEmpty()) {
				" (${lrl.referenceValues} )"
			} else ""
			) + (
			if (severity?.isNotEmpty() == true) {
				" ($severity )"
			} else ""
			)

		return org.taktik.icure.entities.embed.Service(
			id = uuidGen.newGUID().toString(),
			content = mapOf(language to Content(stringValue = value)),
			label = lrl.analysisType ?: "",
			index = position,
			valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS),
			codes = if (severity?.isNotEmpty() == true) setOf(CodeStub.from("CD-SEVERITY", "abnormal", "1")) else setOf()
		)
	}

	protected fun importNumericLaboResult(language: String, d: Double?, lrl: LaboResultLine, position: Long, ril: ResultsInfosLine, comment: String?): org.taktik.icure.entities.embed.Service {
		val r = lrl.referenceValues?.let { tryToGetReferenceValues(it) }
		val severity = lrl.severity?.trim { it <= ' ' }
		return org.taktik.icure.entities.embed.Service(
			codes = if (severity?.isNotEmpty() == true) setOf(CodeStub.from("CD-SEVERITY", "abnormal", "1")) else setOf(),
			id = uuidGen.newGUID().toString(),
			content = mapOf(
				language to Content(
					measureValue = Measure(
						value = d,
						comment = comment,
						unit = lrl.unit?.let { if (it.isBlank()) r?.unit else it } ?: r?.unit,
						min = r?.minValue,
						max = r?.maxValue,
						severity = if (severity?.isNotEmpty() == true) 1 else null,
						severityCode = severity,
                        sign = lrl?.sign?.let{ if(!it.isBlank()) it else null }
                    )
				)
			),
			label = lrl.analysisType ?: "",
			index = position,
			valueDate = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(ril.demandDate, ZoneId.systemDefault()), ChronoUnit.DAYS)
		)
	}

	protected fun tryToGetValueAsNumber(value: String?): Double? {
		val numberS = value!!.replace(",".toRegex(), ".")
		return try {
			numberS.toDouble()
		} catch (e: Exception) { //System.out.println("--------- Failed to parse '" + numberS + "'");
			null
		}
	}

	protected fun tryToGetReferenceValues(refValues: String): Reference? {
		try {
			var m = betweenReference.matcher(refValues)
			if (m.matches()) {
				val r = Reference()
				r.minValue = m.group(1).replace(",".toRegex(), ".").toDouble()
				r.maxValue = m.group(2).replace(",".toRegex(), ".").toDouble()
				if (m.group(3) != null) {
					r.unit = m.group(3)
				}
				if (m.group(4) != null) {
					r.unit = m.group(4)
				}
				return r
			}
			m = lessThanReference.matcher(refValues)
			if (m.matches()) {
				val r = Reference()
				r.maxValue = m.group(1).replace(",".toRegex(), ".").toDouble()
				if (m.group(2) != null) {
					r.unit = m.group(2)
				}
				if (m.group(3) != null) {
					r.unit = m.group(3)
				}
				return r
			}
			m = greaterThanReference.matcher(refValues)
			if (m.matches()) {
				val r = Reference()
				r.minValue = m.group(1).replace(",".toRegex(), ".").toDouble()
				if (m.group(2) != null) {
					r.unit = m.group(2)
				}
				if (m.group(3) != null) {
					r.unit = m.group(3)
				}
				return r
			}
		} catch (e: Exception) {
			return null
		}
		return null
	}

	@Throws(IOException::class)
	override fun getInfos(doc: Document, full: Boolean, language: String, enckeys: List<String>): List<ResultInfo> {
		val br = getBufferedReader(doc, enckeys) ?: throw IllegalArgumentException("Cannot get document")
		val documentId = doc.id
		return extractResultInfos(br, language, documentId, full)
	}

	@Throws(IOException::class)
	protected fun extractResultInfos(br: BufferedReader, language: String, documentId: String?, full: Boolean): List<ResultInfo> {
		val l: MutableList<ResultInfo> = LinkedList()
		var position: Long = 0
		var line = br.readLine()
		while (line != null && position < 10000000L /* ultimate safeguard */) {
			position++
			if (isLaboLine(line)) {
				val ll = getLaboLine(line)
				val ri = ResultInfo()
				ri.labo = ll.labo
				line = br.readLine()
				while (line != null && position < 10000000L /* ultimate safeguard */) {
					position++
					if (isPatientLine(line)) {
						val p = getPatientLine(line)
						ri.lastName = p.lastName
						ri.firstName = p.firstName
						if (p.dn != null) {
							ri.dateOfBirth = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.dn!!.time), ZoneId.systemDefault()), ChronoUnit.DAYS)
						}
						ri.protocol = p.protocol
						ri.sex = p.sex
						ri.documentId = documentId
					} else if (isExtraPatientLine(line)) {
						val p = getExtraPatientLine(line)
						if (p.dn != null) {
							ri.dateOfBirth = FuzzyValues.getFuzzyDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(p.dn!!.time), ZoneId.systemDefault()), ChronoUnit.DAYS)
						}
						if (p.sex != null) {
							ri.sex = p.sex
						}
					} else if (isResultsInfosLine(line)) {
						val r = getResultsInfosLine(line)
						ll.ril = r
						if (r != null) {
							ri.complete = r.isComplete
							ri.demandDate = r.demandDate!!.toEpochMilli()
						}
					} else if (isPatientSSINLine(line)) {
						val p = getPatientSSINLine(line)
						if (p != null) {
							ri.ssin = p.ssin
						}
					} else if (isProtocolLine(line)) {
						if (ri.codes.size == 0) {
							ri.codes.add(CodeStub.from("CD-TRANSACTION", "report", "1"))
						}
						if (full) {
							val lrl = getProtocolLine(line)
							if (lrl != null) {
								if (ll.protoList.size > 20 && lrl.code != ll.protoList[ll.protoList.size - 1]!!.code) {
									createServices(ll, language, position)
								}
								ll.protoList.add(lrl)
							}
						}
					} else if (isLaboResultLine(line)) {
						if (ri.codes.size == 0) {
							ri.codes.add(CodeStub.from("CD-TRANSACTION", "labresult", "1"))
						}
						if (full) {
							val lrl = getLaboResultLine(line, ll)
							if (lrl != null) {
								if (ll.labosList.size > 0 && !(lrl.analysisCode == ll.labosList[0]!!.analysisCode && lrl.analysisType == ll.labosList[0]!!.analysisType)) {
									createServices(ll, language, position)
								}
								ll.labosList.add(lrl)
							}
						}
					} else if (isLaboLine(line)) {
						break
					}
					line = br.readLine()
				}
				if (full) {
					createServices(ll, language, position)
					ri.services = ll.services
				}
				if (ri.protocol == null || ri.protocol.length == 0) {
					ri.protocol = "***" + ri.demandDate
				}
				l.add(ri)
			} else {
				line = br.readLine()
			}
		}
		br.close()
		return l
	}

	protected fun isPatientLine(line: String): Boolean {
		return line.startsWith("A2") || line.matches(Regex(headerPattern + "S2.*"))
	}

	protected fun isExtraPatientLine(line: String): Boolean {
		return line.matches(Regex(headerPattern + "S4.*"))
	}

	protected fun isPatientAddressLine(line: String): Boolean {
		return line.startsWith("A3") || line.matches(Regex(headerPattern + "S3.*"))
	}

	protected fun isResultsInfosLine(line: String): Boolean {
		return line.startsWith("A4") || line.matches(Regex(headerPattern + "S5.*"))
	}

	protected fun isPatientSSINLine(line: String): Boolean {
		return line.startsWith("A5")
	}

	protected fun isLaboLine(line: String): Boolean {
		return line.startsWith("A1") || line.matches(Regex(headerPattern + "S1.*"))
	}

	protected fun isLaboResultLine(line: String): Boolean {
		return line.startsWith("L1") || line.matches(Regex(headerPattern + "R1.*"))
	}

	protected fun isProtocolLine(line: String): Boolean {
		return line.startsWith("L5") || line.startsWith("L2")
	}

	protected fun getLaboLine(line: String): LaboLine {
		val parts = splitLine(line)
		val ll = LaboLine()
		if (parts.size > 1) {
			ll.resultReference = parts[1].trim { it <= ' ' }
		}
		if (parts.size > 2) {
			ll.labo = parts[2].trim { it <= ' ' }
		}
		ll.fullLine = line
		return ll
	}

	protected fun getPatientLine(line: String): PatientLine {
		val parts = splitLine(line)
		val pl = PatientLine()
		if (parts.size > 1) {
			pl.protocol = parts[1].trim { it <= ' ' }
		}
		if (parts.size > 3) {
			pl.firstName = parts[3].trim { it <= ' ' }
		}
		if (parts.size > 2) {
			pl.lastName = parts[2].trim { it <= ' ' }
		}
		if (parts.size > 4) {
			pl.sex = if (parts[4].trim { it <= ' ' } == "V") "F" else parts[4].trim { it <= ' ' }
			if (parts.size > 5) {
				pl.dn = parseBirthDate(parts[5].trim { it <= ' ' })
			}
		}
		return pl
	}

	protected fun getExtraPatientLine(line: String): PatientLine {
		val parts = splitLine(line)
		val pl = PatientLine()
		if (parts.size > 1) {
			pl.protocol = parts[1]
		}
		if (parts.size > 3) {
			pl.sex = if (parts[3].trim { it <= ' ' } == "V") "F" else parts[3].trim { it <= ' ' }
		}
		if (parts.size > 2) {
			pl.dn = parseBirthDate(parts[2].trim { it <= ' ' })
		}
		return pl
	}

    protected fun getLaboResultLine(line: String, ll: LaboLine): LaboResultLine? {
        return try {
            val parts = splitLine(line)
            val lrl = LaboResultLine()
            lrl.value = ""
            lrl.severity = lrl.value
            lrl.unit = lrl.severity
            lrl.referenceValues = lrl.unit
            lrl.analysisType = lrl.referenceValues
            lrl.analysisCode = lrl.analysisType
            lrl.protocol = lrl.analysisCode
            if (parts.size > 1) {
                lrl.protocol = parts[1].trim { it <= ' ' }
            }
            if (parts.size > 2) {
                lrl.analysisCode = parts[2].trim { it <= ' ' }
            }
            if (parts.size > 3) {
                lrl.analysisType = parts[3].trim { it <= ' ' }
            }
            if (!line.startsWith("L1")) {
                if (parts.size > 5) {
                    lrl.referenceValues = parts[4].trim { it <= ' ' } + " - " + parts[5].trim { it <= ' ' }
                }
                if (parts.size > 6) {
                    lrl.unit = parts[6].trim { it <= ' ' }
                }
                lrl.severity = ""
            } else {
                if (lrl.analysisType!!.length == 0 && ll.labosList.size > 0 && ll.labosList[ll.labosList.size - 1]!!.analysisCode != null && ll.labosList[ll.labosList.size - 1]!!.analysisCode == lrl.analysisCode) {
                    lrl.analysisType = ll.labosList[ll.labosList.size - 1]!!.analysisType
                    lrl.value = parts[4].trim { it <= ' ' }
                } else {
                    if (parts.size > 4) {
                        lrl.referenceValues = parts[4].trim { it <= ' ' }
                    }
                    if (parts.size > 5) {
                        lrl.unit = parts[5].trim { it <= ' ' }
                    }
                    if (parts.size > 6) {
                        lrl.severity = parts[6].trim { it <= ' ' }
                    }
                }
            }
            if (lrl.value == "" && parts.size > 7) {
                lrl.value = parts[7].trim { it <= ' ' }
                lrl.sign = parts[7].trim().let { if (it.startsWith("<") || it.startsWith(">")) it.substring(0, 1) else null }
            }
            if (lrl.analysisType == null || lrl.analysisType == "") {
                lrl.analysisType = "untitled"
            }
            lrl
        } catch (e: Exception) {
            println("------------Line = $line")
            e.printStackTrace()
            null
        }
    }

	protected fun getProtocolLine(line: String): ProtocolLine? {
		return try {
			val parts = splitLine(line)
			val pl = ProtocolLine()
			if (parts.size > 1) {
				pl.protocol = parts[1].trim { it <= ' ' }
			}
			if (parts.size > 2) {
				pl.code = parts[2].trim { it <= ' ' }
			}
			if (parts.size > 7) {
				pl.text = parts[7].trim { it <= ' ' }
			} else if (parts.size > 3) {
				pl.text = parts[3].trim { it <= ' ' }
			}
			pl
		} catch (e: Exception) {
			println("------------Line = $line")
			e.printStackTrace()
			null
		}
	}

	protected fun getResultsInfosLine(line: String): ResultsInfosLine? {
		return try {
			val parts = splitLine(line)
			val ril = ResultsInfosLine()
			if (parts.size > 1) {
				ril.protocol = parts[1].trim { it <= ' ' }
			}
			ril.isComplete = parts.size <= 5 || parts[5].toLowerCase().contains("c")
			if (parts.size > 3) {
				ril.demandDate = parseDemandDate(parts[3].trim { it <= ' ' })
			}
			ril
		} catch (e: Exception) {
			println("------------Line = $line")
			e.printStackTrace()
			null
		}
	}

	protected fun getPatientSSINLine(line: String): PatientSSINLine? {
		return try {
			val parts = splitLine(line)
			val psl = PatientSSINLine()
			if (parts.size > 1) {
				psl.protocol = parts[1]
			}
			if (parts.size > 3 && FuzzyValues.isSsin(parts[3])) {
				psl.ssin = parts[3]
			}
			if (parts.size > 4 && FuzzyValues.isSsin(parts[4])) {
				psl.ssin = parts[4]
			}
			psl
		} catch (e: Exception) {
			println("------------Line = $line")
			e.printStackTrace()
			null
		}
	}

	protected fun getPatientAddressLine(line: String): PatientAddressLine {
		val parts = splitLine(line)
		val pal = PatientAddressLine()
		if (parts.size > 1) {
			pal.protocol = parts[1].trim { it <= ' ' }
		}
		if (parts.size > 4) {
			pal.locality = parts[4].trim { it <= ' ' }
		}
		if (parts.size > 3) {
			val zipMatcher = zipCode.matcher(parts[3].trim { it <= ' ' })
			if (zipMatcher.matches()) {
				pal.zipCode = zipMatcher.group(1)
			}
		}
		if (parts.size > 2) {
			val addressMatcher = address.matcher(parts[2].trim { it <= ' ' })
			if (addressMatcher.matches()) {
				pal.address = if (addressMatcher.group(1) == null) addressMatcher.group(3) else addressMatcher.group(2)
				pal.number = if (addressMatcher.group(1) == null) addressMatcher.group(4) else addressMatcher.group(1)
			} else {
				pal.address = parts[2].trim { it <= ' ' }
			}
		}
		return pal
	}

	override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, text: String?): Flow<DataBuffer> {
		val pw: PrintWriter
		val os = ByteArrayOutputStream(10000)

		pw = try {
			PrintWriter(OutputStreamWriter(os, "UTF-8"))
		} catch (e: UnsupportedEncodingException) {
			throw IllegalStateException(e)
		}
		var namePat = if (patient!!.lastName != null) patient.lastName else ""
		var firstPat = if (patient.firstName != null) patient.firstName else ""
		var sexPat = if (patient.gender != null) patient.gender.code else ""
		var birthPat = if (patient.dateOfBirth != null) patient.dateOfBirth.toString().replace("(....)(..)(..)".toRegex(), "$3$2$1") else ""
		var ssinPat = if (patient.ssin != null) patient.ssin else ""
		val a = patient.addresses.stream().filter { ad: Address -> ad.addressType == AddressType.home }.findFirst()
		var addrPat3 = a.map { obj: Address -> obj.city }.orElse("")
		var addrPat2 = a.map { obj: Address -> obj.postalCode }.orElse("")
		var addrPat1 = a.map { obj: Address -> obj.street }.orElse("")
		var inamiMed = if (sender!!.nihii != null) sender.nihii else ""
		var nameMed = if (sender.lastName != null) sender.lastName else ""
		var firstMed = if (sender.firstName != null) sender.firstName else ""
		var dateAnal = if (date != null) date.format(shortDateTimeFormatter) else ""
		val isFull = "C"
		namePat = namePat!!.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		firstPat = firstPat.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		sexPat = sexPat.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		birthPat = birthPat.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		ssinPat = ssinPat.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		addrPat3 = addrPat3!!.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		addrPat2 = addrPat2!!.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		addrPat1 = addrPat1!!.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		inamiMed = inamiMed!!.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		nameMed = nameMed.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		firstMed = firstMed.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		dateAnal = dateAnal.replace("\n".toRegex(), "").replace("\r".toRegex(), "")
		pw.print("A1\\$ref\\$inamiMed $nameMed $firstMed\\\r\n")
		pw.print("A2\\$ref\\$namePat\\$firstPat\\$sexPat\\$birthPat\\\r\n")
		pw.print("A3\\$ref\\$addrPat1\\$addrPat2\\$addrPat3\\\r\n")
		pw.print("A4\\$ref\\$inamiMed $nameMed $firstMed\\$dateAnal\\\\$isFull\\\r\n")
		pw.print("A5\\$ref\\\\$ssinPat\\\\\\\\\r\n")
		for (line in text!!.replace("\u2028".toRegex(), "\n").split("\n").toTypedArray()) {
			pw.print("L5\\$ref\\DIVER\\\\\\\\\\$line\\\r\n")
		}
		pw.flush()

		return DataBufferUtils.read(ByteArrayResource(os.toByteArray()), DefaultDataBufferFactory(), 10000).asFlow()
	}

	override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, mimeType: String?, content: ByteArray?) = flowOf<DataBuffer>()

	fun splitLine(line: String): Array<String> {
		val m = headerCompiledPrefix.matcher(line)
		return if (m.matches()) {
			val l: MutableList<String> = ArrayList()
			l.add(m.group(2))
			l.add(m.group(1))
			l.addAll(listOf(*m.group(3).split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
			l.toTypedArray()
		} else {
			line.split("\\\\".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		}
	}

	@Throws(IOException::class)
	override fun canHandle(doc: Document, enckeys: List<String>): Boolean {
		val br = getBufferedReader(doc, enckeys)
		val firstLine = br!!.readLine()
		br.close()
		return firstLine != null && isLaboLine(firstLine)
	}

	@Throws(ParseException::class, NumberFormatException::class)
	protected fun parseDate(date: String): Long {
		if (date.length == 8) {
			return shortDateFormat.parse(date.trim { it <= ' ' }).time
		} else if (date.length == 6) {
			return shorterDateFormat.parse(date).time
		} else if (date.length == 10) {
			return extraDateFormat.parse(date).time
		}
		throw NumberFormatException("Unreadable date: \"$date\"")
	}

	protected fun parseBirthDate(date: String): Timestamp? {
		try {
			val d = parseDate(date)
			if (d > parseDate("01011800")) {
				return Timestamp(d)
			}
		} catch (e: ParseException) {
			e.printStackTrace()
		} catch (e: NumberFormatException) {
			e.printStackTrace()
		}
		return null
	}

	protected fun parseDemandDate(date: String): Instant {
		try {
			val d = parseDate(date)
			if (d > parseDate("01011800")) {
				return Instant.ofEpochMilli(d)
			}
		} catch (e: ParseException) {
			log.error("Date {} could not be parsed", date)
		} catch (e: NumberFormatException) {
			log.error("Date {} could not be parsed", date)
		}
		return Instant.now()
	}

	companion object {
		private val log = LoggerFactory.getLogger(HealthOneLogicImpl::class.java)
		var shorterDateFormat = SimpleDateFormat("ddMMyy")
		var shortDateFormat = SimpleDateFormat("ddMMyyyy")
		var extraDateFormat = SimpleDateFormat("dd/MM/yyyy")

		//\s*>\s*((?:-|\+)?[0-9]*(?:\.|,)?[0-9]*) matches __>__-01.29 and >+2,245 and >1  into $1
//(?:(?:\s*([^0-9\s]\S*))|(?:\s+(\S+)))?\s* matches a0eraa and __a5656 (first part) or (_898989) in other words: any garbage that is separed by a space or
//an alphanumerical character
//We also allow for an open parenthesis, an open [ or both
		var greaterThanReference = Pattern.compile("\\s*(?:[\\(\\[]+\\s*)?>\\s*((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)(?:(?:\\s*([^0-9\\s]\\S*))|(?:\\s+(\\S+)))?\\s*")

		//The same with <
		var lessThanReference = Pattern.compile("\\s*(?:[\\(\\[]+\\s*)?<\\s*((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)(?:(?:\\s*([^0-9\\s]\\S*))|(?:\\s+(\\S+)))?\\s*")

		//GROUPA = ((?:-|\+)?[0-9]*(?:\.|,)?[0-9]*)\s* matches -01.29 and +2,245 and 1  into $1
//We match _GROUPA__-__GROUPA[GARBAGE]
//We also allow for an open parenthesis
		var betweenReference = Pattern.compile("\\s*(?:[\\(\\[]+\\s*)?((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)\\s*[-:]\\s*((?:-|\\+)?[0-9]*(?:\\.|,)?[0-9]*)(?:(?:\\s*([^0-9\\s]\\S*))|(?:\\s+(\\S+)))?\\s*")
		var address = Pattern.compile("^(?:\\s*(\\d+)(?:\\s*,\\s*|\\s+)(\\S.*?\\S)\\s*)|(?:\\s*(\\S.*?\\S)(?:\\s*,\\s*|\\s+)(\\d+)\\s*)$")
		var zipCode = Pattern.compile("^\\s*(\\d+)\\s*$")
		var headerPattern = "^\\s*(\\d+)\\s+"
		var headerCompiledPrefix = Pattern.compile("^\\s*[0-9][0-9][0-9][0-9](\\d+)\\s+([A-Z][0-9])(.*)$")
	}
}
