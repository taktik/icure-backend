package org.taktik.icure.samv2

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sun.xml.internal.ws.util.NoCloseInputStream
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.be.samv2v4.entities.CommentedClassificationFullDataType
import org.taktik.icure.be.samv2v4.entities.ExportActualMedicinesType
import org.taktik.icure.be.samv2v4.entities.ExportReimbursementsType
import org.taktik.icure.be.samv2v4.entities.ExportVirtualMedicinesType
import org.taktik.icure.dao.impl.ektorp.CouchDbICureConnector
import org.taktik.icure.dao.impl.ektorp.StdCouchDbICureConnector
import org.taktik.icure.dao.impl.idgenerators.UUIDGenerator
import org.taktik.icure.dao.samv2.impl.AmpDAOImpl
import org.taktik.icure.dao.samv2.impl.VmpDAOImpl
import org.taktik.icure.dao.samv2.impl.VmpGroupDAOImpl
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.samv2.Amp
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
import org.taktik.icure.entities.samv2.embed.VirtualForm
import org.taktik.icure.entities.samv2.embed.VirtualIngredient
import org.taktik.icure.entities.samv2.embed.VmpComponent
import org.taktik.icure.entities.samv2.embed.Vtm
import org.taktik.icure.entities.samv2.stub.VmpGroupStub
import org.taktik.icure.entities.samv2.stub.VmpStub
import java.net.URI
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.xml.bind.JAXBContext
import kotlin.collections.HashMap
import kotlin.collections.HashSet

fun main(args: Array<String>) = Samv2v4Import().main(args)

fun commentedClassificationMapper(cc:CommentedClassificationFullDataType) : CommentedClassification? = cc.data?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { lcc ->
    CommentedClassification(
            lcc.title?.let { SamText(it.fr, it.nl, it.de, it.en) },
            lcc.url?.let { SamText(it.fr, it.nl, it.de, it.en) },
            cc.commentedClassification?.mapNotNull { cc -> commentedClassificationMapper(cc) } ?: listOf()
    )
}

@Suppress("NestedLambdaShadowedImplicitParameter")
class Samv2v4Import : CliktCommand() {
    val samv2url: String by option(help="The url of the zip file").prompt("Samv2 file url")
    val url: String by option(help="The database server to connect to").prompt("Database server url")
    val username: String by option(help="The Username").prompt("Username")
    val password: String by option(help="The Password").prompt("Password")
    val dbName: String by option(help="The database name").prompt("Database name")
    val update: String by option(help="Force update of existing entries").prompt("Force update")

    val vaccineIndicationsMap = Gson().fromJson<ArrayList<Map<String, *>>>(
            this.javaClass.getResource("vaccines.json").openStream().bufferedReader(),
            object : TypeToken<ArrayList<Map<String, *>>>() {}.type
    )
            .fold(mutableMapOf<String, List<String>>(), { map, it ->
                map[it["cnk"] as String] = it["codes"] as List<String>
                map
            })

    override fun run() {
        val httpClient = StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url(url).username(username).password(password).build()
        val dbInstance = StdCouchDbInstance(httpClient)
        val couchdbConfig = StdCouchDbICureConnector(dbName, dbInstance)
        val updateExistingDocs = (update == "true" || update == "yes")
        val reimbursements: MutableMap<Triple<String?, String?, String?>, MutableList<Reimbursement>> = HashMap()
        val vmps : MutableMap<String, VmpStub> = HashMap()
        val vers = samv2url.replace(Regex("(.*/)?(.+?)-.+.zip"),"$2")

        URI(samv2url).toURL().openStream().let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry;entry != null }) {
                when {
                    entry!!.name.startsWith("VMP") ->
                        (JAXBContext.newInstance(ExportVirtualMedicinesType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportVirtualMedicinesType)?.let { importVirtualMedicines(it, vmps, couchdbConfig, updateExistingDocs) }
                    entry!!.name.startsWith("RMB") ->
                        (JAXBContext.newInstance(ExportReimbursementsType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportReimbursementsType)?.let { importReimbursements(it, reimbursements, couchdbConfig, updateExistingDocs) }
                }
            }
        }

        URI(samv2url).toURL().openStream().let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry; entry != null }) {
                when {
                    entry!!.name.startsWith("AMP") ->
                        (JAXBContext.newInstance(ExportActualMedicinesType::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportActualMedicinesType)?.let { importActualMedicines(it, vmps, reimbursements, couchdbConfig, updateExistingDocs) }
                }
            }
        }

        val samVersion = couchdbConfig.find(SamVersion::class.java, "org.taktik.icure.samv2")
        samVersion?.let { it.version = vers; couchdbConfig.update(it) } ?: couchdbConfig.create(SamVersion(vers).apply { id = "org.taktik.icure.samv2" })
    }

    private fun <E> retry(count: Int, executor: () -> E): E {
        return try { executor() } catch(e: Exception) { if (count>0) retry(count-1, executor) else throw e }
    }

    private fun importReimbursements(export: ExportReimbursementsType, reimbursements: MutableMap<Triple<String?, String?, String?>, MutableList<Reimbursement>>, couchdbConfig: StdCouchDbICureConnector, force: Boolean) {
        export.reimbursementContext.forEach { reimb ->
            reimb.data.forEach { reimbd ->
                val from = reimbd.from?.toGregorianCalendar()?.timeInMillis
                val to = reimbd.to?.toGregorianCalendar()?.timeInMillis

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
                        flatRateSystem = reimbd.isFlatRateSystem,
                        reimbursementBasePrice = reimbd.reimbursementBasePrice,
                        referenceBasePrice = reimbd.referenceBasePrice,
                        reimbursementCriterion = reimbd.reimbursementCriterion?.let { ReimbursementCriterion(it.category, it.code, it.description?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                        copaymentSupplement = reimbd.copaymentSupplement,
                        pricingUnit = reimbd.pricingUnit?.let { Pricing(it.quantity, it.label?.let { SamText(it.fr, it.nl, it.de, it.en)}) },
                        pricingSlice = reimbd.pricingSlice?.let { Pricing(it.quantity, it.label?.let { SamText(it.fr, it.nl, it.de, it.en)}) },
                        copayments = reimb.copayment?.mapNotNull { cop -> cop.data?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { copd -> Copayment(regimeType = cop.regimeType, from = copd.from?.toGregorianCalendar()?.timeInMillis, to = copd.to?.toGregorianCalendar()?.timeInMillis, feeAmount = copd.feeAmount?.toString()) } }
                ))
            }
        }

        val ampDAO = AmpDAOImpl(couchdbConfig , UUIDGenerator())
        HashSet<String>(retry(10) { ampDAO.allIds }).chunked(100).forEach { ids ->
            ampDAO.save(ampDAO.getList(ids).fold(LinkedList<Amp>(), operation = { acc, amp ->
                var shouldAdd = false
                amp.ampps.flatMap { it.dmpps ?: listOf() }.filterNotNull().forEach { dmpp: Dmpp ->
                    reimbursements[Triple(dmpp.deliveryEnvironment?.name, dmpp.codeType?.name, dmpp.code)]?.let {
                        if (dmpp.reimbursements != it) {
                            dmpp.reimbursements = it
                            shouldAdd = true
                        }
                    }
                }
                if (shouldAdd) acc.add(amp)
                acc
            }))
        }
    }

    private fun importVirtualMedicines(export: ExportVirtualMedicinesType, vmps: MutableMap<String, VmpStub>, couchdbConfig: CouchDbICureConnector, force: Boolean) {
        val vmpGroupDAO = VmpGroupDAOImpl(couchdbConfig , UUIDGenerator())
        val vmpDAO = VmpDAOImpl(couchdbConfig , UUIDGenerator())

        val currentVmpGroups = HashSet(retry(10) { vmpGroupDAO.allIds })
        val currentVmps = HashSet(retry(10) { vmpDAO.allIds })

        val vmpGroupIds = HashMap<Int, String>()

        export.vmpGroup.forEach { vmpg ->
            vmpg.data.map { d ->
                val code = vmpg.code.toString()
                val from = d.from?.toGregorianCalendar()?.timeInMillis
                val to = d.to?.toGregorianCalendar()?.timeInMillis

                val id = "VMPGROUP:$code:$from".md5()

                VmpGroup(
                        from = from,
                        to = to,
                        productId = vmpg.productId,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        code = code,
                        noGenericPrescriptionReason = d.noGenericPrescriptionReason?.let { reason ->
                            NoGenericPrescriptionReason(reason.code, reason.description?.let { SamText(it.fr, it.nl, it.de, it.en) })
                        },
                        noSwitchReason = d.noSwitchReason?.let { reason ->
                            NoSwitchReason(reason.code, reason.description?.let { SamText(it.fr, it.nl, it.de, it.en) })
                        }
                ).apply {
                    this.id = id
                }.let { vmpg ->
                    if (!currentVmpGroups.contains(id)) {
                        vmpGroupDAO.create(vmpg)
                    } else if (force) {
                        val prev = vmpGroupDAO.get(vmpg.id)
                        if (prev != vmpg) {
                            vmpGroupDAO.update(vmpg.apply { this.rev = prev.rev })
                        }
                        vmpg
                    } else vmpg
                }
            }.maxBy { it.to ?: Long.MAX_VALUE }?.let {
                latestVmpGroup -> vmpGroupIds[vmpg.code] = latestVmpGroup.id
            }
        }
        export.vmp.forEach { vmp ->
            vmp.data.map { d ->
                val code = vmp.code.toString()
                val from = d.from?.toGregorianCalendar()?.timeInMillis
                val to = d.to?.toGregorianCalendar()?.timeInMillis

                val id = "VMP:$code:$from".md5()

                if (!currentVmps.contains(id) || force) Vmp(
                        id = id,
                        from = from,
                        to = to,
                        code = code,
                        vmpGroup = d.vmpGroup?.let { VmpGroupStub(id = vmpGroupIds[d.vmpGroup.code], productId = it.productId, code = it.code.toString(), name = it.data?.maxBy { c -> c.from?.toGregorianCalendar()?.timeInMillis ?: 0L }?.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        abbreviation = d.abbreviation?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        vtm = Vtm(code = d.vtm?.code?.toString(), name = d.vtm?.data?.last()?.name?.let { SamText(it.fr, it.nl, it.de, it.en) }),
                        commentedClassifications = d.commentedClassification?.mapNotNull { cc -> commentedClassificationMapper(cc)} ?: listOf(),
                        components = vmp.vmpComponent?.mapNotNull { vmpc ->
                            vmpc?.data?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { comp ->
                                VmpComponent(
                                        code = vmpc.code.toString(),
                                        virtualForm = comp.virtualForm?.let { virtualForm ->
                                            VirtualForm(virtualForm.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, virtualForm.standardForm?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        },
                                        routeOfAdministrations = comp.routeOfAdministration?.map { roa ->
                                            RouteOfAdministration(roa.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, roa.standardRoute?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        phaseNumber = comp.phaseNumber,
                                        name = comp.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        virtualIngredients = vmpc.virtualIngredient?.mapNotNull { vi ->
                                            vi.data.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                VirtualIngredient(
                                                        from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                        rank = vi.rank?.toInt(),
                                                        type = it.type?.let { IngredientType.valueOf(it.value()) },
                                                        strengthRange = it.strength?.let { StrengthRange(NumeratorRange(it.numeratorRange.min, it.numeratorRange.max, it.numeratorRange.unit), Quantity(it.denominator.value, it.denominator.unit)) },
                                                        substance = it.substance?.let {
                                                            Substance(
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
                        } ?: listOf()
                ).let { vmp ->
                    vmp.code?.let { vmps[it] = VmpStub(code = vmp.code, id = vmp.id, vmpGroup = vmp.vmpGroup?.let { VmpGroupStub(it.id, it.productId, it.code, it.name) }, name = vmp.name) }
                    if (!currentVmps.contains(id)) {
                        vmpDAO.create(vmp)
                    } else if (force) {
                        val prev = vmpDAO.get(vmp.id)
                        if(prev != vmp) {
                            vmpDAO.update(vmp.apply {this.rev=prev.rev})
                        }
                        vmp
                    } else vmp
                }
            }
        }
    }

    private fun importActualMedicines(export: ExportActualMedicinesType, vmps: Map<String, VmpStub>, reimbursements: Map<Triple<String?, String?, String?>, MutableList<Reimbursement>>, couchdbConfig: CouchDbICureConnector, force: Boolean) {
        val ampDAO = AmpDAOImpl(couchdbConfig , UUIDGenerator())
        val currentAmps = HashSet(retry(10) { ampDAO.allIds })

        export.amp.forEach { amp ->
            amp.data.map { d ->
                val code = amp.code.toString()
                val from = d.from?.toGregorianCalendar()?.timeInMillis
                val to = d.to?.toGregorianCalendar()?.timeInMillis

                val id = "AMP:$code:$from".md5()

                if (!currentAmps.contains(id) || force) Amp(
                        id = id,
                        from = from,
                        to = to,
                        code = code,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        abbreviatedName = d.abbreviatedName?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        officialName = d.officialName,
                        vmp = amp.vmpCode?.let { vmps[it.toString()] },
                        status = d.status?.value()?.let { AmpStatus.valueOf(it) },
                        blackTriangle = d.isBlackTriangle,
                        medicineType = d.medicineType?.value()?.let { MedicineType.valueOf(it) },
                        company = d.company?.data?.maxBy { c -> c.from?.toGregorianCalendar()?.timeInMillis ?: 0L }?.let {
                            Company(it.from?.toGregorianCalendar()?.timeInMillis, it.to?.toGregorianCalendar()?.timeInMillis, it.authorisationNr,
                                    it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let {v -> mapOf(Pair(cc,v))}}, it.europeanNr, it.denomination, it.legalForm, it.building,
                                    it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
                        },
                        proprietarySuffix = d.proprietarySuffix?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        prescriptionName = d.prescriptionName?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        ampps = amp.ampp?.mapNotNull { ampp ->
                            ampp.data?.maxBy { d -> d.from?.toGregorianCalendar()?.timeInMillis ?: 0 }?.let { amppd ->
                                Ampp(
                                        from = amppd.from?.toGregorianCalendar()?.timeInMillis,
                                        to = amppd.to?.toGregorianCalendar()?.timeInMillis,
                                        ctiExtended = ampp.ctiExtended,
                                        isOrphan = amppd.isOrphan,
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
                                        distributorCompany = amppd.distributorCompany ?.let {
                                            it.data.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                Company(it.from?.toGregorianCalendar()?.timeInMillis, it.to?.toGregorianCalendar()?.timeInMillis, it.authorisationNr,
                                                        it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let { v -> mapOf(Pair(cc, v)) } }, it.europeanNr, it.denomination, it.legalForm, it.building,
                                                        it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
                                            }
                                        },
                                        isSingleUse = amppd.isSingleUse,
                                        speciallyRegulated = amppd.speciallyRegulated,
                                        abbreviatedName = amppd.abbreviatedName?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        prescriptionName = amppd.prescriptionName?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        note = amppd.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        posologyNote = amppd.posologyNote?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        noGenericPrescriptionReasons = amppd.noGenericPrescriptionReason?.map { SamText(it.description.fr, it.description.nl, it.description.de, it.description.en) } ?: listOf(),
                                        exFactoryPrice = amppd.exFactoryPrice?.toDouble(),
                                        reimbursementCode = amppd.reimbursementCode,
                                        definedDailyDose = Quantity(amppd.definedDailyDose?.value, amppd.definedDailyDose?.unit),
                                        officialExFactoryPrice = amppd.officialExFactoryPrice?.toDouble(),
                                        realExFactoryPrice = amppd.realExFactoryPrice?.toDouble(),
                                        pricingInformationDecisionDate = amppd.pricingInformationDecisionDate?.toGregorianCalendar()?.timeInMillis,
                                        components = ampp.amppComponent?.map { component ->
                                            component.data.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                AmppComponent(
                                                        from = it.from?.toGregorianCalendar()?.timeInMillis, to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                        contentType = it.contentType?.let { ContentType.valueOf(it.value()) },
                                                        deviceType = it.deviceType?.let { DeviceType(code = it.code, edqmCode = it.edqmCode, edqmDefinition = it.edqmDefinition, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                                                        packagingType = it.packagingType?.let { PackagingType(code = it.code, edqmCode = it.edqmCode, edqmDefinition = it.edqmDefinition, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                                                        packSpecification = it.packSpecification,
                                                        contentMultiplier = it.contentMultiplier
                                                )
                                            } } ?: listOf(),
                                        commercializations = ampp.commercialization?.data?.mapNotNull {
                                            Commercialization(
                                                    from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                    to = it.to?.toGregorianCalendar()?.timeInMillis
                                            ) }?: listOf(),
                                        dmpps = ampp.dmpp?.mapNotNull { dmpp ->
                                            dmpp.data.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                Dmpp( from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                        deliveryEnvironment = dmpp.deliveryEnvironment?.let { DeliveryEnvironment.valueOf(it.value()) },
                                                        productId = dmpp.productId,
                                                        code = dmpp.code,
                                                        codeType = dmpp.codeType?.let { DmppCodeType.valueOf(it.value()) },
                                                        price = it.price?.toString(), cheap = it.isCheap, cheapest = it.isCheapest, reimbursable = it.isReimbursable,
                                                        reimbursements = reimbursements[Triple(dmpp.deliveryEnvironment?.value(), dmpp.codeType?.value(), dmpp.code)])
                                            }
                                        },
                                        vaccineIndicationCodes = ArrayList(HashSet(ampp.dmpp?.flatMap { dmpp ->
                                            dmpp.data.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                vaccineIndicationsMap[dmpp.code]
                                            } ?: listOf()}))
                                )
                            }
                        } ?: listOf(),
                        components = amp.ampComponent?.mapNotNull { ampc ->
                            ampc?.data?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { comp ->
                                AmpComponent(
                                        pharmaceuticalForms = comp.pharmaceuticalForm?.map { pharmForm ->
                                            PharmaceuticalForm( pharmForm.code, pharmForm.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, pharmForm.standardForm?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        routeOfAdministrations = comp.routeOfAdministration?.map { roa ->
                                            RouteOfAdministration(roa.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, roa.standardRoute?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        dividable = comp.dividable,
                                        scored = comp.scored,
                                        crushable = comp.crushable?.value()?.let { Crushable.valueOf(it) },
                                        containsAlcohol = comp.containsAlcohol?.value()?.let { ContainsAlcohol.valueOf(it) },
                                        isSugarFree = comp.isSugarFree,
                                        modifiedReleaseType = comp.modifiedReleaseType,
                                        specificDrugDevice = comp.specificDrugDevice,
                                        dimensions = comp.dimensions,
                                        name = comp.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        note = comp.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        ingredients = ampc.realActualIngredient?.mapNotNull { ingredient ->
                                            ingredient.data.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                Ingredient(
                                                        from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                        rank = ingredient.rank?.toInt(),
                                                        type = it.type?.let { IngredientType.valueOf(it.value()) },
                                                        knownEffect = it.isKnownEffect,
                                                        strength = it.strength?.let { Quantity(it.value, it.unit) },
                                                        strengthDescription = it.strengthDescription,
                                                        additionalInformation = it.additionalInformation,
                                                        substance = it.substance?.let {
                                                            Substance(
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
                                        } ?: listOf()
                                )
                            }
                        } ?: listOf()
                ).let { amp ->
                    if (!currentAmps.contains(id)) {
                        ampDAO.create(amp)
                    } else if (force) {
                        val prev = ampDAO.get(amp.id)
                        if (amp.ampps.all { it.dmpps?.all { it?.reimbursements == null } != false }) {
                            prev.ampps.forEach { it.dmpps?.forEach { it?.reimbursements = null } }
                        }
                        if(prev != amp) {
                            ampDAO.update(amp.apply { this.rev = prev.rev })
                        }
                        amp
                    } else amp
                }
            }
        }
    }
}
