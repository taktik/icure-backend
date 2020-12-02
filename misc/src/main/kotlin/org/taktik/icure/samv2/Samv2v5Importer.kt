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

package org.taktik.icure.samv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.couchdb.create
import org.taktik.couchdb.update
import org.taktik.icure.asyncdao.impl.CouchDbDispatcher
import org.taktik.icure.asyncdao.samv2.impl.AmpDAOImpl
import org.taktik.icure.asyncdao.samv2.impl.NmpDAOImpl
import org.taktik.icure.asyncdao.samv2.impl.PharmaceuticalFormDAOImpl
import org.taktik.icure.asyncdao.samv2.impl.ProductIdDAOImpl
import org.taktik.icure.asyncdao.samv2.impl.SubstanceDAOImpl
import org.taktik.icure.asyncdao.samv2.impl.VmpDAOImpl
import org.taktik.icure.asyncdao.samv2.impl.VmpGroupDAOImpl
import org.taktik.icure.be.samv2v5.entities.CommentedClassificationFullDataType
import org.taktik.icure.be.samv2v5.entities.ExportActualMedicinesType
import org.taktik.icure.be.samv2v5.entities.ExportNonMedicinalType
import org.taktik.icure.be.samv2v5.entities.ExportReimbursementsType
import org.taktik.icure.be.samv2v5.entities.ExportVirtualMedicinesType
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.samv2.Amp
import org.taktik.icure.entities.samv2.Nmp
import org.taktik.icure.entities.samv2.ProductId
import org.taktik.icure.entities.samv2.SamVersion
import org.taktik.icure.entities.samv2.Vmp
import org.taktik.icure.entities.samv2.VmpGroup
import org.taktik.icure.entities.samv2.embed.AmpComponent
import org.taktik.icure.entities.samv2.embed.AmpStatus
import org.taktik.icure.entities.samv2.embed.Ampp
import org.taktik.icure.entities.samv2.embed.AmppComponent
import org.taktik.icure.entities.samv2.embed.Atc
import org.taktik.icure.entities.samv2.embed.CommentedClassification
import org.taktik.icure.entities.samv2.embed.Commercialization
import org.taktik.icure.entities.samv2.embed.Company
import org.taktik.icure.entities.samv2.embed.ContainsAlcohol
import org.taktik.icure.entities.samv2.embed.ContentType
import org.taktik.icure.entities.samv2.embed.Copayment
import org.taktik.icure.entities.samv2.embed.Crushable
import org.taktik.icure.entities.samv2.embed.DeliveryEnvironment
import org.taktik.icure.entities.samv2.embed.DeviceType
import org.taktik.icure.entities.samv2.embed.Dmpp
import org.taktik.icure.entities.samv2.embed.DmppCodeType
import org.taktik.icure.entities.samv2.embed.Ingredient
import org.taktik.icure.entities.samv2.embed.IngredientType
import org.taktik.icure.entities.samv2.embed.MedicineType
import org.taktik.icure.entities.samv2.embed.MultipleType
import org.taktik.icure.entities.samv2.embed.NoGenericPrescriptionReason
import org.taktik.icure.entities.samv2.embed.NoSwitchReason
import org.taktik.icure.entities.samv2.embed.NumeratorRange
import org.taktik.icure.entities.samv2.embed.PackagingType
import org.taktik.icure.entities.samv2.embed.PharmaceuticalForm
import org.taktik.icure.entities.samv2.embed.Pricing
import org.taktik.icure.entities.samv2.embed.Quantity
import org.taktik.icure.entities.samv2.embed.Reimbursement
import org.taktik.icure.entities.samv2.embed.ReimbursementCriterion
import org.taktik.icure.entities.samv2.embed.RouteOfAdministration
import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.StandardSubstance
import org.taktik.icure.entities.samv2.embed.StandardSubstanceType
import org.taktik.icure.entities.samv2.embed.StrengthRange
import org.taktik.icure.entities.samv2.embed.Substance
import org.taktik.icure.entities.samv2.embed.SupplyProblem
import org.taktik.icure.entities.samv2.embed.VirtualForm
import org.taktik.icure.entities.samv2.embed.VirtualIngredient
import org.taktik.icure.entities.samv2.embed.VmpComponent
import org.taktik.icure.entities.samv2.embed.Vtm
import org.taktik.icure.entities.samv2.embed.Wada
import org.taktik.icure.entities.samv2.stub.VmpGroupStub
import org.taktik.icure.entities.samv2.stub.VmpStub
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.utils.NoCloseInputStream
import java.net.URI
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.xml.bind.JAXBContext
import kotlin.collections.HashMap
import kotlin.collections.HashSet

fun main(args: Array<String>) = Samv2v5Import().main(args)

fun commentedClassificationMapper(cc:CommentedClassificationFullDataType) : CommentedClassification? = cc.data?.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let { lcc ->
    CommentedClassification(
            lcc.title?.let { SamText(it.fr, it.nl, it.de, it.en) },
            lcc.url?.let { SamText(it.fr, it.nl, it.de, it.en) },
            cc.commentedClassification?.mapNotNull { cc -> commentedClassificationMapper(cc) } ?: listOf()
    )
}

@ExperimentalCoroutinesApi
@Suppress("NestedLambdaShadowedImplicitParameter")
class Samv2v5Import : CliktCommand() {
    val log = LoggerFactory.getLogger(this::class.java)
    val samv2url: String? by option(help = "The url of the zip file")
    val url: String by option(help = "The database server to connect to").prompt("Database server url")
    val username: String by option(help = "The Username").prompt("Username")
    val password: String by option(help = "The Password").prompt("Password")
    val dbName: String by option(help = "The database name").prompt("Database name")
    val update: String by option(help = "Force update of existing entries").prompt("Force update")

    val vaccineIndicationsMap = Gson().fromJson<ArrayList<VaccineCode>>(
            this.javaClass.getResource("vaccines.json").openStream().bufferedReader(),
            object : TypeToken<ArrayList<VaccineCode>>() {}.type
    ).fold(mutableMapOf<String, List<String>>(), { map, it ->
        it.cnk?.let { cnk -> map[cnk] = it.codes }
        map
    }).toMap()

    override fun run() {
        val couchDbProperties = CouchDbProperties().apply {
            this.username = this@Samv2v5Import.username
            this.password = this@Samv2v5Import.password
            this.url = this@Samv2v5Import.url
        }

        val httpClient = WebClient.builder().build()
        val couchDbDispatcher = CouchDbDispatcher(httpClient, ObjectMapper().registerModule(KotlinModule()), dbName, "drugs", username, password)
        val updateExistingDocs = (update == "true" || update == "yes")
        val reimbursements: MutableMap<Triple<String?, String?, String?>, MutableList<Reimbursement>> = HashMap()
        val vmps: MutableMap<String, VmpStub> = HashMap()
        var vers: String? = null
        var fileName: String? = null
        val productIds = HashMap<String, String>()

        val zipData = if (samv2url == null) URI("https://www.vas.ehealth.fgov.be/websamcivics/samcivics/download/samv2-full-getLastVersion?xsd=5").toURL().readBytes().toString(Charsets.UTF_8).let {
            URI("https://www.vas.ehealth.fgov.be/websamcivics/samcivics/download/samv2-download?type=full&version=${it}&xsd=5").toURL().openConnection().let { conn ->
                fileName = (conn.headerFields["Content-Disposition"]
                        ?: conn.headerFields["content-disposition"])?.let { it[0].replace(Regex("attachment;filename=\"(.+)\""), "$1") }
                conn.getInputStream().readBytes()
            }
        } else null

        val versionDate = fileName?.replace(Regex("([0-9]{4})-([0-9]{4})-([0-9]{2})-([0-9]{2})-([0-9]{2})-([0-9]{2})-([0-9]{2})\\.zip"), "$2$3$4")?.toInt()
                ?: 99999999
        (zipData?.let { it.inputStream() } ?: samv2url?.let { URI(it).toURL().openStream() })?.let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry;entry != null }) {
                when {
                    entry!!.name.startsWith("VMP") ->
                        (JAXBContext.newInstance(ExportVirtualMedicinesType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportVirtualMedicinesType)?.let {
                            runBlocking {
                                productIds.putAll(importVirtualMedicines(it, vmps, couchDbProperties, couchDbDispatcher, updateExistingDocs))
                            }
                        }
                    entry!!.name.startsWith("RMB") ->
                        (JAXBContext.newInstance(ExportReimbursementsType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportReimbursementsType)?.let {
                            runBlocking {
                                importReimbursements(it, reimbursements, couchDbProperties, couchDbDispatcher, updateExistingDocs)
                            }
                        }
                    entry!!.name.startsWith("NONMEDICINAL") ->
                        (JAXBContext.newInstance(ExportNonMedicinalType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportNonMedicinalType)?.let {
                            runBlocking {
                                productIds.putAll(importNonMedicinals(it, couchDbProperties, couchDbDispatcher, updateExistingDocs))
                            }
                        }

                }
            }
        }

        (zipData?.let { it.inputStream() } ?: samv2url?.let { URI(it).toURL().openStream() }).let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry; entry != null }) {
                when {
                    entry!!.name.startsWith("AMP") ->
                        runBlocking {
                        (JAXBContext.newInstance(ExportActualMedicinesType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportActualMedicinesType)?.let {
                            vers = it.samId
                            productIds.putAll(importActualMedicines(it, vmps, reimbursements, couchDbProperties, couchDbDispatcher, updateExistingDocs))
                        }
                }
            }
        }
    }

    val dbInstanceUri = URI(couchDbProperties.url)
    val client = couchDbDispatcher.getClient(dbInstanceUri)

    runBlocking {
            val ampDAO = AmpDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { forceInitStandardDesignDocument(true) }
            val productIdDAO = ProductIdDAOImpl(couchDbProperties, couchDbDispatcher , UUIDGenerator()).apply { forceInitStandardDesignDocument(true) }

            val samVersion = ampDAO.getVersion()
            samVersion?.let { client.update(it.copy(version = vers, date = versionDate)) } ?: client.create(SamVersion(id = "org.taktik.icure.samv2" , version = vers, date = versionDate))

            retry(10) { productIdDAO.getAllIds().toList() }.let {
            val ids = HashSet(it)
            productIds.filterKeys { ids.contains(it) }.entries.sortedWith(compareBy { it.key }).chunked(100).forEach {
                    productIdDAO.save(retry(10) { productIdDAO.list(it.map { it.key }) }.map { p ->
                        p.let { it.copy(productId = productIds[p.id]) }
                })
            }
            productIds.filterKeys { !ids.contains(it) }.entries.sortedWith(compareBy { it.key }).chunked(100).forEach {
                    productIdDAO.save(it.map { ProductId(id = it.key, productId = it.value) })
            }
            ids.filter { !productIds.containsKey(it) }.sortedWith(compareBy { it }).chunked(100).forEach {
                    retry(10) { productIdDAO.purge(productIdDAO.list(it)) }
            }
        }
    }
}

    private suspend fun <E> retry(count: Int, executor: suspend () -> E): E {
        return try { executor() } catch(e: Exception) { if (count>0) retry(count-1, executor) else throw e }
    }

    private suspend fun importReimbursements(export: ExportReimbursementsType, reimbursements: MutableMap<Triple<String?, String?, String?>, MutableList<Reimbursement>>, couchDbProperties: CouchDbProperties, couchDbDispatcher: CouchDbDispatcher, force: Boolean) {
        export.reimbursementContext.forEach { reimb ->
            reimb.data.forEach { reimbd ->
                val from = reimbd.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis
                val to = reimbd.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis

                reimbursements[Triple(reimb.deliveryEnvironment?.value(), reimb.codeType?.value(), reimb.code)].let { if (it != null) it else {
                    val newList = LinkedList<Reimbursement>()
                    reimbursements[Triple(reimb.deliveryEnvironment?.value(), reimb.codeType?.value(), reimb.code)] = newList
                    newList
                } }.add(Reimbursement(
                        from = from,
                        to = to,
                        deliveryEnvironment = reimb.deliveryEnvironment?.let { DeliveryEnvironment.valueOf(it.value()) },
                        code = reimb.code,
                        codeType = reimb.codeType?.let { DmppCodeType.valueOf(it.value()) },
                        multiple = reimbd.multiple?.let { MultipleType.valueOf(it.value()) },
                        temporary = reimbd.isTemporary,
                        reference = reimbd.isReference,
                        legalReferencePath = reimb.legalReferencePath,
                        flatRateSystem = reimbd.isFlatRateSystem,
                        reimbursementBasePrice = reimbd.reimbursementBasePrice,
                        referenceBasePrice = reimbd.referenceBasePrice,
                        reimbursementCriterion = reimbd.reimbursementCriterion?.let { ReimbursementCriterion(it.category, it.code, it.description?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                        copaymentSupplement = reimbd.copaymentSupplement,
                        pricingUnit = reimbd.pricingUnit?.let { Pricing(it.quantity, it.label?.let { SamText(it.fr, it.nl, it.de, it.en)}) },
                        pricingSlice = reimbd.pricingSlice?.let { Pricing(it.quantity, it.label?.let { SamText(it.fr, it.nl, it.de, it.en)}) },
                        copayments = reimb.copayment?.mapNotNull { cop -> cop.data?.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let { copd -> Copayment(regimeType = cop.regimeType, from = copd.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, to = copd.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, feeAmount = copd.feeAmount?.toString()) } }
                ))
            }
        }

        val ampDAO = AmpDAOImpl(couchDbProperties, couchDbDispatcher , UUIDGenerator()).apply { force }
        HashSet<String>(retry(10) { ampDAO.getAllIds().toList() }).chunked(100).forEach { ids ->
            ampDAO.save(ampDAO.list(ids).fold(listOf<Amp>()) { acc, amp ->
                val ampps = amp.ampps.map { it.copy(dmpps = it.dmpps.map { dmpp: Dmpp ->
                    reimbursements[Triple(dmpp.deliveryEnvironment?.name, dmpp.codeType?.name, dmpp.code)]?.let {
                        if (dmpp.reimbursements != it) {
                            dmpp.reimbursements?.forEachIndexed { index, reimbursement ->
                                if (index>=it.size) {
                                    log.info("≠ in the number of reimbursements for dmpp ${dmpp.codeType?.name}-${dmpp.code}")
                                } else {
                                    if (reimbursement != it[index]) {
                                        log.info("≠ in the reimbursement $index for dmpp ${dmpp.codeType?.name}-${dmpp.code}")
                                    }
                                }
                            }
                            dmpp.copy(reimbursements = it)
                        } else dmpp
                    } ?: dmpp
                })}
                if (ampps != amp.ampps) acc + amp.copy(ampps = ampps) else acc
            }.asFlow())
        }
    }

    private suspend fun importVirtualMedicines(export: ExportVirtualMedicinesType, vmpsMap: MutableMap<String, VmpStub>, couchDbProperties: CouchDbProperties, couchDbDispatcher: CouchDbDispatcher, force: Boolean) : Map<String, String>  {
        val result = HashMap<String, String>()
        val vmpGroupDAO = VmpGroupDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { force }
        val vmpDAO = VmpDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { force }

        val currentVmpGroups = HashSet(retry(10) { vmpGroupDAO.getAllIds().toList() })
        val currentVmps = HashSet(retry(10) { vmpDAO.getAllIds().toList() })

        val vmpGroupIds = HashMap<Int, String>()

        val newVmpIds = mutableListOf<String>()
        val newVmpGroupIds = mutableListOf<String>()

        export.vmpGroup.forEach { vmpg ->
            vmpg.data.map { d ->
                val code = vmpg.code.toString()
                val from = d.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis
                val to = d.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis

                val id = "VMPGROUP:$code:$from".md5()

                result["SAMID:$id"] = vmpg.productId
                newVmpGroupIds.add(id)
                VmpGroup(
                        id = id,
                        from = from,
                        to = to,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        code = code,
                        noGenericPrescriptionReason = d.noGenericPrescriptionReason?.let { reason ->
                            NoGenericPrescriptionReason(reason.code, reason.description?.let { SamText(it.fr, it.nl, it.de, it.en) })
                        },
                        noSwitchReason = d.noSwitchReason?.let { reason ->
                            NoSwitchReason(reason.code, reason.description?.let { SamText(it.fr, it.nl, it.de, it.en) })
                        }
                ).let { vmpg ->
                    if (!currentVmpGroups.contains(id)) {
                        log.info("New VMP group VMPGROUP:$code:$from with id ${id}")
                        try { vmpGroupDAO.save(vmpg) } catch (e:Exception) { vmpGroupDAO.get(vmpg.id)?.let { vmpGroupDAO.update(vmpg.copy(rev = it.rev)) } }
                    } else if (force) {
                        val prev = vmpGroupDAO.get(vmpg.id)
                        if (prev != vmpg) {
                            log.info("Modified VMP group VMPGROUP:$code:$from with id ${id}")
                            vmpGroupDAO.update(vmpg.copy(rev = prev?.rev ))
                        } else vmpg
                    } else vmpg
                }
            }.filterNotNull().maxBy { it?.to ?: Long.MAX_VALUE }?.let {
                latestVmpGroup -> vmpGroupIds[vmpg.code] = latestVmpGroup.id
            }
        }
        export.vmp.flatMap { vmp ->
            vmp.data.map { d ->
                val code = vmp.code.toString()
                val from = d.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis
                val to = d.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis

                val id = "VMP:$code:$from".md5()
                newVmpIds.add(id)

                Vmp(
                        id = id,
                        from = from,
                        to = to,
                        code = code,
                        vmpGroup = d.vmpGroup?.let { vmpGroupIds[d.vmpGroup.code]?.let { vmpgId -> VmpGroupStub(id = vmpgId, code = it.code.toString(), name = it.data?.maxBy { c -> c.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis ?: 0L }?.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) } },
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        abbreviation = d.abbreviation?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        vtm = Vtm(code = d.vtm?.code?.toString(), name = d.vtm?.data?.last()?.name?.let { SamText(it.fr, it.nl, it.de, it.en) }),
                        commentedClassifications = d.commentedClassification?.mapNotNull { cc -> commentedClassificationMapper(cc) }
                                ?: listOf(),
                        components = vmp.vmpComponent?.mapNotNull { vmpc ->
                            vmpc?.data?.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let { comp ->
                                VmpComponent(
                                        code = vmpc.code.toString(),
                                        virtualForm = comp.virtualForm?.let { virtualForm ->
                                            VirtualForm(virtualForm.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, virtualForm.standardForm?.map { CodeStub.from(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        },
                                        routeOfAdministrations = comp.routeOfAdministration?.map { roa ->
                                            RouteOfAdministration(roa.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, roa.standardRoute?.map { CodeStub.from(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        phaseNumber = comp.phaseNumber,
                                        name = comp.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        virtualIngredients = vmpc.virtualIngredient?.mapNotNull { vi ->
                                            vi.data.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let {
                                                VirtualIngredient(
                                                        from = it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                        rank = vi.rank?.toInt(),
                                                        type = it.type?.let { IngredientType.valueOf(it.value()) },
                                                        strengthRange = it.strength?.let { StrengthRange(NumeratorRange(it.numeratorRange.min, it.numeratorRange.max, it.numeratorRange.unit), Quantity(it.denominator.value, it.denominator.unit)) },
                                                        substance = it.substance?.let {
                                                            Substance(
                                                                    id = "SAM-SUBSTANCE:${it.code}:${it.chemicalForm}".md5(),
                                                                    code = it.code,
                                                                    chemicalForm = it.chemicalForm,
                                                                    name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                    note = it.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                    standardSubstances = it.standardSubstance?.mapNotNull {
                                                                        StandardSubstance(
                                                                                code = it.code,
                                                                                type = it.standard?.let { StandardSubstanceType.withValue(it.value()) },
                                                                                name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                                url = it.url,
                                                                                definition = it.definition?.let { SamText(it.fr, it.nl, it.de, it.en) }
                                                                        )
                                                                    } ?: listOf()
                                                            )
                                                        }
                                                )
                                            }
                                        }
                                )
                            }
                        } ?: listOf(),
                        wadas = d.wada.mapNotNull { Wada(code = it.code, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, description = it.description?.let { SamText(it.fr, it.nl, it.de, it.en) }) }
                )
            }
        }.chunked(100).map { vmps ->
                    val currentVmpsWithDoc = vmpDAO.getList(vmps.map { it.id }.filter { currentVmps.contains(it) }).toList()
            vmps.forEach { vmp ->
                vmp.code?.let { vmpsMap[it] = VmpStub(code = vmp.code, id = vmp.id, vmpGroup = vmp.vmpGroup?.let { VmpGroupStub(it.id, it.code, it.name) }, name = vmp.name) }
                if (!currentVmps.contains(vmp.id)) {
                            log.info("New VMP VMP:${vmp.code}:${vmp.from} with id ${vmp.id}")
                            try {
                                vmpDAO.save(vmp)
                            } catch (e: Exception) {
                                vmpDAO.get(vmp.id)?.let { vmpDAO.update(vmp.copy(rev = it.rev)) }
                    }
                } else if (force) {
                    val prev = currentVmpsWithDoc.find { it.id == vmp.id }!!
                    if (prev != vmp) {
                                log.info("Modified VMP VMP:${vmp.code}:${vmp.from} with id ${vmp.id}")
                                vmpDAO.update(vmp.copy(rev = prev?.rev))
                    }
                    vmp
                } else vmp
            }
        }

        (currentVmpGroups - newVmpGroupIds).chunked(100).forEach { vmpGroupDAO.remove(vmpGroupDAO.list(it)) }
        (currentVmps - newVmpIds).chunked(100).forEach { vmpDAO.remove(vmpDAO.list(it)) }

        return result
    }

    private suspend fun importActualMedicines(export: ExportActualMedicinesType, vmps: Map<String, VmpStub>, reimbursements: Map<Triple<String?, String?, String?>, MutableList<Reimbursement>>, couchDbProperties: CouchDbProperties, couchDbDispatcher: CouchDbDispatcher, force: Boolean) : Map<String, String> {
        val result = HashMap<String, String>()

        val ampDAO = AmpDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { forceInitStandardDesignDocument(true) }
        val substanceDAO = SubstanceDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { forceInitStandardDesignDocument(true) }
        val pharmaceuticalFormDAO = PharmaceuticalFormDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { forceInitStandardDesignDocument(true) }

        val currentAmps = HashSet(retry(10) { ampDAO.getAllIds().toList() })
        val newAmpIds = mutableListOf<String>()

        val substances = mutableMapOf<String, Substance>()
        val pharmaceuticalForms = mutableMapOf<String, PharmaceuticalForm>()

        export.amp.flatMap { amp ->
            amp.data.map { d ->
                val code = amp.code.toString()
                val from = d.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis
                val to = d.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis

                val id = "AMP:$code:$from".md5()
                newAmpIds.add(id)

                Amp(
                        id = id,
                        from = from,
                        to = to,
                        code = code,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        abbreviatedName = d.abbreviatedName?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        officialName = d.officialName,
                        vmp = amp.vmpCode?.let { vmps[it.toString()] },
                        status = d.status?.value()?.let { AmpStatus.valueOf(it) },
                        blackTriangle = d.isBlackTriangle,
                        medicineType = d.medicineType?.value()?.let { MedicineType.valueOf(it) },
                        company = d.company?.data?.maxBy { c -> c.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis ?: 0L }?.let {
                            Company(it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, it.authorisationNr,
                                    it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let {v -> mapOf(Pair(cc,v))}}, it.europeanNr, it.denomination, it.legalForm, it.building,
                                    it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
                        },
                        proprietarySuffix = d.proprietarySuffix?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        prescriptionName = (d.prescriptionName ?: d.prescriptionNameFamhp)?.let { SamText(it.fr ?: d.prescriptionNameFamhp?.fr, it.nl ?: d.prescriptionNameFamhp?.nl, it.de ?: d.prescriptionNameFamhp?.de, it.en ?: d.prescriptionNameFamhp?.en)},
                        ampps = amp.ampp?.mapNotNull { ampp ->
                            ampp.data?.maxBy { d -> d.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis ?: 0 }?.let { amppd ->
                                Ampp(
                                        from = amppd.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                        to = amppd.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                        index = amppd.index?.toDouble(),
                                        ctiExtended = ampp.ctiExtended,
                                        orphan = amppd.isOrphan,
                                        leafletLink = amppd.leafletLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        spcLink = amppd.spcLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        rmaPatientLink = amppd.rmaPatientLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        rmaProfessionalLink = amppd.rmaProfessionalLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        parallelCircuit = amppd.parallelCircuit,
                                        parallelDistributor = amppd.parallelDistributor,
                                        packMultiplier = amppd.packMultiplier,
                                        packAmount = amppd.packAmount?.let { Quantity(amppd.packAmount.value, amppd.packAmount?.unit) },
                                        packDisplayValue = amppd.packDisplayValue,
                                        status = amppd.status?.value()?.let { AmpStatus.valueOf(it) },
                                        atcs = amppd.atc?.map { Atc(it.code, it.description) } ?: listOf(),
                                        crmLink = amppd.crmLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        deliveryModusCode = amppd.deliveryModus?.code,
                                        deliveryModus = amppd.deliveryModus?.description?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        deliveryModusSpecification = amppd.deliveryModusSpecification?.description?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        dhpcLink = amppd.dhpcLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        distributorCompany = amppd.distributorCompany ?.let {
                                            it.data.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let {
                                                Company(it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, it.authorisationNr,
                                                        it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let { v -> mapOf(Pair(cc, v)) } }, it.europeanNr, it.denomination, it.legalForm, it.building,
                                                        it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
                                            }
                                        },
                                        singleUse = amppd.isSingleUse,
                                        speciallyRegulated = amppd.speciallyRegulated,
                                        abbreviatedName = amppd.abbreviatedName?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        prescriptionName = (amppd.prescriptionName ?: amppd.prescriptionNameFamhp)?.let { SamText(it.fr ?: amppd.prescriptionNameFamhp?.fr, it.nl ?: amppd.prescriptionNameFamhp?.nl, it.de ?: amppd.prescriptionNameFamhp?.de, it.en ?: amppd.prescriptionNameFamhp?.en)},
                                        note = amppd.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        posologyNote = amppd.posologyNote?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        noGenericPrescriptionReasons = amppd.noGenericPrescriptionReason?.map { SamText(it.description.fr, it.description.nl, it.description.de, it.description.en) } ?: listOf(),
                                        exFactoryPrice = amppd.exFactoryPrice?.toDouble(),
                                        reimbursementCode = amppd.reimbursementCode,
                                        definedDailyDose = Quantity(amppd.definedDailyDose?.value, amppd.definedDailyDose?.unit),
                                        officialExFactoryPrice = amppd.officialExFactoryPrice?.toDouble(),
                                        realExFactoryPrice = amppd.realExFactoryPrice?.toDouble(),
                                        pricingInformationDecisionDate = amppd.pricingInformationDecisionDate?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                        components = ampp.amppComponent?.map { component ->
                                            component.data.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let {
                                                AmppComponent(
                                                        from = it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis, to = it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                        contentType = it.contentType?.let { ContentType.valueOf(it.value()) },
                                                        deviceType = it.deviceType?.let { DeviceType(code = it.code, edqmCode = it.edqmCode, edqmDefinition = it.edqmDefinition, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                                                        packagingType = it.packagingType?.let { PackagingType(code = it.code, edqmCode = it.edqmCode, edqmDefinition = it.edqmDefinition, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                                                        packSpecification = it.packSpecification,
                                                        contentMultiplier = it.contentMultiplier
                                                )
                                            }
                                        } ?: listOf(),
                                        commercializations = ampp.commercialization?.data?.mapNotNull {
                                            Commercialization(
                                                    from = it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                    to = it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                    reason = it.reason?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                    endOfComercialization = it.endOfCommercialization?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                    impact = it.impact?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                    additionalInformation = it.additionalInformation?.let { SamText(it.fr, it.nl, it.de, it.en) }
                                            )
                                        } ?: listOf(),
                                        supplyProblems = ampp.supplyProblem?.data?.mapNotNull {
                                            SupplyProblem(
                                                    from = it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                    to = it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                    reason = it.reason?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                    expectedEndOn = it.expectedEndOn?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                    impact = it.impact?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                    additionalInformation = it.additionalInformation?.let { SamText(it.fr, it.nl, it.de, it.en) }

                                            )
                                        },
                                        dmpps = ampp.dmpp?.mapNotNull { dmpp ->
                                            dmpp.data.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let {
                                                val dmppFrom = it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis
                                                val dmppId = "AMP:$code:$from:AMPP:${ampp.ctiExtended}:DMPP:${dmpp.code}:${dmppFrom}:${dmpp.deliveryEnvironment}".md5()
                                                if (result["SAMID:$dmppId"] != null && result["SAMID:$dmppId"] != dmpp.productId) {
                                                    throw IllegalStateException("duplicate dmpp in db ${code} - ${from}")
                                                }
                                                result["SAMID:$dmppId"] = dmpp.productId

                                                Dmpp(id = dmppId,
                                                        from = dmppFrom,
                                                        to = it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                        deliveryEnvironment = dmpp.deliveryEnvironment?.let { DeliveryEnvironment.valueOf(it.value()) },
                                                        code = dmpp.code,
                                                        codeType = dmpp.codeType?.let { DmppCodeType.valueOf(it.value()) },
                                                        price = it.price?.toString(), cheap = it.isCheap, cheapest = it.isCheapest, reimbursable = it.isReimbursable,
                                                        reimbursements = reimbursements[Triple(dmpp.deliveryEnvironment?.value(), dmpp.codeType?.value(), dmpp.code)])
                                            }
                                        } ?: listOf(),
                                        vaccineIndicationCodes = ampp.dmpp?.flatMap { dmpp ->
                                            dmpp.data.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let {
                                                vaccineIndicationsMap[dmpp.code]
                                            } ?: listOf<String>()}?.toSet()?.toList()
                                )
                            }
                        } ?: listOf(),
                        components = amp.ampComponent?.mapNotNull { ampc ->
                            ampc?.data?.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let { comp ->
                                AmpComponent(
                                        pharmaceuticalForms = comp.pharmaceuticalForm?.map { pharmForm ->
                                            val id = "SAM-PHARMAFORM:${pharmForm.code}:${pharmForm.name?.fr}:${pharmForm.name?.nl}:${pharmForm.name?.de}:${pharmForm.name?.en}".md5()
                                            pharmaceuticalForms[id] ?: PharmaceuticalForm(
                                                    id = id,
                                                    code = pharmForm.code,
                                                    name = pharmForm.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                    standardForms = pharmForm.standardForm?.map { CodeStub.from(it.standard.value(), it.code, "1.0") } ?: listOf()
                                            ).also {
                                                    pharmaceuticalForms[id] = it
                                                }
                                        } ?: listOf(),
                                        routeOfAdministrations = comp.routeOfAdministration?.map { roa ->
                                            RouteOfAdministration(roa.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, roa.standardRoute?.map { CodeStub.from(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        dividable = comp.dividable,
                                        scored = comp.scored,
                                        crushable = comp.crushable?.value()?.let { Crushable.valueOf(it) },
                                        containsAlcohol = comp.containsAlcohol?.value()?.let { ContainsAlcohol.valueOf(it) },
                                        sugarFree = comp.isSugarFree,
                                        modifiedReleaseType = comp.modifiedReleaseType,
                                        specificDrugDevice = comp.specificDrugDevice,
                                        dimensions = comp.dimensions,
                                        name = comp.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        note = comp.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        ingredients = ampc.realActualIngredient?.mapNotNull { ingredient ->
                                            ingredient.data.maxBy { d -> d.from.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null).timeInMillis }?.let {
                                                Ingredient(
                                                        from = it.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis,
                                                        rank = ingredient.rank?.toInt(),
                                                        type = it.type?.let { IngredientType.valueOf(it.value()) },
                                                        knownEffect = it.isKnownEffect,
                                                        strength = it.strength?.let { Quantity(it.value, it.unit) },
                                                        strengthDescription = it.strengthDescription,
                                                        additionalInformation = it.additionalInformation,
                                                        substance = it.substance?.let {
                                                            val id = "SAM-SUBSTANCE:${it.code}:${it.chemicalForm}".md5()
                                                            substances[id] ?: Substance(
                                                                    id = id,
                                                                    code = it.code,
                                                                    chemicalForm = it.chemicalForm,
                                                                    name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                    note = it.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                    standardSubstances = it.standardSubstance?.mapNotNull {
                                                                        StandardSubstance(
                                                                                code = it.code,
                                                                                type = it.standard?.let { StandardSubstanceType.withValue(it.value()) },
                                                                                name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                                url = it.url,
                                                                                definition = it.definition?.let { SamText(it.fr, it.nl, it.de, it.en) }
                                                                        )
                                                                    } ?: listOf()
                                                            ).also {
                                                                    substances[id] = it
                                                                }

                                                        }
                                                )
                                            }
                                        } ?: listOf()
                                )
                            }
                        } ?: listOf()
                )
            }
        }.chunked(100).map { amps ->
            val currentAmpsWithDoc = ampDAO.getList(amps.map { it.id }.filter { currentAmps.contains(it) }).toList()
            amps.forEach { amp ->
                if (!currentAmps.contains(amp.id)) {
                    log.info("New AMP AMP:${amp.code}:${amp.from} with id ${amp.id}")
                    try {
                        ampDAO.save(amp)
                    } catch (e: Exception) {
                        ampDAO.get(amp.id)?.let { ampDAO.update(amp.copy(rev = it.rev)) }
                    }
                } else if (force) {
                    val prev = currentAmpsWithDoc.find { it.id == amp.id }!!
                    if (amp.ampps.all { it.dmpps.all { it.reimbursements == null } != false }) {
                        prev.let { it.copy(ampps = it.ampps.map { it.copy(dmpps = it.dmpps.map { it.copy(reimbursements = null) }) }) }
                    } else ampDAO.get(amp.id)
                    if (prev != amp) {
                        log.info("Modified AMP AMP:${amp.code}:${amp.from} with id ${amp.id}")
                        ampDAO.update(amp.copy(rev = prev.rev))
                    }
                    amp
                } else amp
            }
        }

        val currentSubstances = HashSet<String>(retry(10) { substanceDAO.getAllIds().toList() })
        val currentPharmaceuticalForms = HashSet<String>(retry(10) { pharmaceuticalFormDAO.getAllIds().toList() })

        (substances - currentSubstances).values.chunked(100).forEach {
            try {
                substanceDAO.save(it)
            } catch(e:Exception) {
                it.forEach {
                    try {
                        substanceDAO.save(it)
                    } catch (e: Exception) {
                        substanceDAO.get(it.id)?.let {
                            substanceDAO.update(it.copy(rev = it.rev))
                        }
                    }
                }
            }
        }

        (pharmaceuticalForms - currentPharmaceuticalForms).values.chunked(100).forEach {
            try {
                pharmaceuticalFormDAO.save(it)
            } catch (e: Exception) {
                it.forEach {
                    try {
                        pharmaceuticalFormDAO.save(it)
                    } catch (e: Exception) {
                        pharmaceuticalFormDAO.get(it.id)?.let {
                            pharmaceuticalFormDAO.update(it.copy(rev = it.rev))
                        }
                    }
                }
            }
        }
        substances.filterKeys { currentSubstances.contains(it) }.values.chunked(100).forEach {
            val revs = substanceDAO.getList(it.map { it.id }).toList().fold(mapOf<String, String>()) { acc, it -> acc + (it.id to it.rev!!) }
            it.forEach { substanceDAO.update(it.copy(rev = it.rev)) }
        }
        pharmaceuticalForms.filterKeys { currentPharmaceuticalForms.contains(it) }.values.chunked(100).forEach {
            val revs = pharmaceuticalFormDAO.getList(it.map { it.id }).toList().fold(mapOf<String, String>()) { acc, it -> acc + (it.id to it.rev!!) }
            it.forEach { pharmaceuticalFormDAO.update(it.copy(rev = it.rev)) }
        }

        (currentSubstances - substances.keys).chunked(100).forEach { substanceDAO.remove(substanceDAO.getList(it)) }
        (currentPharmaceuticalForms - pharmaceuticalForms.keys).chunked(100).forEach { pharmaceuticalFormDAO.remove(pharmaceuticalFormDAO.getList(it)) }
        (currentAmps - newAmpIds).chunked(100).forEach { ampDAO.remove(ampDAO.list(it)) }

        return result
    }

    private suspend fun importNonMedicinals(export: ExportNonMedicinalType, couchDbProperties: CouchDbProperties, couchDbDispatcher: CouchDbDispatcher, force: Boolean) : Map<String, String> {
        val result = HashMap<String, String>()
        val nmpDAO = NmpDAOImpl(couchDbProperties, couchDbDispatcher, UUIDGenerator()).apply { forceInitStandardDesignDocument(true) }
        val currentNmps = HashSet(retry(10) { nmpDAO.getAllIds().toList() })
        val newNmpIds = mutableListOf<String>()

        export.nonMedicinalProduct.flatMap { nmp ->
            nmp.data.map { d ->
                val code = nmp.code.toString()
                val from = d.from?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis
                val to = d.to?.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null, null)?.timeInMillis

                val id = "NMP:$code:$from".md5()
                newNmpIds.add(id)
                if (result["SAMID:$id"] != null && result["SAMID:$id"] != nmp.productId) {
                    throw IllegalStateException("duplicate nmp in db ${code} - ${from}")
                }
                result["SAMID:$id"] = nmp.productId

                Nmp(
                        id = id,
                        from = from,
                        to = to,
                        code = code,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        producer = d.producer?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        distributor = d.producer?.let { SamText(it.fr, it.nl, it.de, it.en) }
                )
            }
        }.chunked(100).map { nmps ->
            val currentNmpsWithDoc = nmpDAO.getList(nmps.map { it.id }.filter { currentNmps.contains(it) }).toList()
            nmps.map { nmp ->
                if (!currentNmps.contains(nmp.id)) {
                    log.info("New NMP NMP:${nmp.code}:${nmp.from} with id ${nmp.id}")
                    try {
                        nmpDAO.save(nmp)
                    } catch (e: Exception) {
                        nmpDAO.get(nmp.id)?.let { nmpDAO.update(nmp.copy(rev = it.rev)) }
                    }
                } else if (force) {
                    val prev = currentNmpsWithDoc.find { it.id == nmp.id }!!
                    if (prev != nmp) {
                        log.info("Modified NMP NMP:${nmp.code}:${nmp.from} with id ${nmp.id}")
                        nmpDAO.update(nmp.copy(rev = prev.rev))
                    }
                    nmp
                } else nmp
            }
        }
        (currentNmps - newNmpIds).chunked(100).forEach { nmpDAO.remove(nmpDAO.getList(it)) }
        return result
    }

    class VaccineCode {
        var cnk: String? = null
        var codes: List<String> = listOf()
    }
}
