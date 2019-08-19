package org.taktik.icure.samv2

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.be.samv2.entities.ExportActualMedicines
import org.taktik.icure.be.samv2.entities.ExportVirtualMedicines
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
import org.taktik.icure.entities.samv2.embed.*
import java.math.BigInteger
import java.net.URI
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.xml.bind.JAXBContext
import com.sun.xml.internal.ws.util.NoCloseInputStream
import org.taktik.icure.entities.samv2.embed.AmppComponent
import org.taktik.icure.be.samv2.entities.CommentedClassificationFullDataType
import org.taktik.icure.be.samv2.entities.ExportReimbursements
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


fun main(args: Array<String>) = Samv2Import().main(args)

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun commentedClassificationMapper(cc:CommentedClassificationFullDataType) : CommentedClassification? = cc.datas?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { lcc ->
    CommentedClassification(
            lcc.title?.let { SamText(it.fr, it.nl, it.de, it.en) },
            lcc.url?.let { SamText(it.fr, it.nl, it.de, it.en) },
            cc.commentedClassifications?.mapNotNull { cc -> commentedClassificationMapper(cc) } ?: listOf()
    )
}

@Suppress("NestedLambdaShadowedImplicitParameter")
class Samv2Import : CliktCommand() {
    val samv2url: String by option(help="The url of the zip file").prompt("Samv2 file url")
    val url: String by option(help="The database server to connect to").prompt("Database server url")
    val username: String by option(help="The Username").prompt("Username")
    val password: String by option(help="The Password").prompt("Password")
    val dbName: String by option(help="The database name").prompt("Database name")
    val update: String by option(help="Force update of existing entries").prompt("Force update")

    override fun run() {
        val httpClient = StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url(url).username(username).password(password).build()
        val dbInstance = StdCouchDbInstance(httpClient)
        val couchdbConfig = StdCouchDbICureConnector(dbName, dbInstance)
        val updateExistingDocs = (update == "true" || update == "yes")
        val reimbursements: MutableMap<Triple<String?, String?, String?>, MutableList<Reimbursement>> = HashMap()

        URI(samv2url).toURL().openStream().let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry;entry != null }) {
                when {
                    entry!!.name.startsWith("AMP") ->
                        (JAXBContext.newInstance(ExportActualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportActualMedicines)?.let { importActualMedicines(it, reimbursements, couchdbConfig, updateExistingDocs) }
                    entry!!.name.startsWith("VMP") ->
                        (JAXBContext.newInstance(ExportVirtualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportVirtualMedicines)?.let { importVirtualMedicines(it, couchdbConfig, updateExistingDocs) }
                    entry!!.name.startsWith("RMB") ->
                        (JAXBContext.newInstance(ExportVirtualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportReimbursements)?.let { importReimbursements(it, reimbursements, couchdbConfig, updateExistingDocs) }
                }
            }
        }
    }

    private fun importReimbursements(export: ExportReimbursements, reimbursements: MutableMap<Triple<String?, String?, String?>, MutableList<Reimbursement>>, couchdbConfig: StdCouchDbICureConnector, force: Boolean) {
        export.reimbursementContexts.forEach { reimb ->
            reimb.datas.forEach { reimbd ->
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
                        reimbursementBasePrice = reimbd.reimbursementBasePrice?.toString(),
                        referenceBasePrice = reimbd.referenceBasePrice?.toString(),
                        reimbursementCriterion = reimbd.reimbursementCriterion?.let { ReimbursementCriterion(it.category, it.code, it.description?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                        copaymentSupplement = reimbd.copaymentSupplement?.toString(),
                        pricingUnit = reimbd.pricingUnit?.let { Pricing(it.quantity?.toString(), it.label?.let { SamText(it.fr, it.nl, it.de, it.en)}) },
                        pricingSlice = reimbd.pricingSlice?.let { Pricing(it.quantity?.toString(), it.label?.let { SamText(it.fr, it.nl, it.de, it.en)}) }
                ))
            }
        }

        val ampDAO = AmpDAOImpl(couchdbConfig , UUIDGenerator())
        HashSet(ampDAO.allIds).chunked(100).forEach { ids ->
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

    private fun importVirtualMedicines(export: ExportVirtualMedicines, couchdbConfig: CouchDbICureConnector, force: Boolean) {
        val vmpGroupDAO = VmpGroupDAOImpl(couchdbConfig , UUIDGenerator())
        val vmpDAO = VmpDAOImpl(couchdbConfig , UUIDGenerator())

        val currentVmpGroups = HashSet(vmpGroupDAO.allIds)
        val currentVmps = HashSet(vmpDAO.allIds)

        val vmpGroupIds = HashMap<Int,String>()

        export.vmpGroups.forEach { vmpg ->
            vmpg.datas.map { d ->
                val code = vmpg.code.toString()
                val from = d.from?.toGregorianCalendar()?.timeInMillis
                val to = d.to?.toGregorianCalendar()?.timeInMillis

                val id = "VMPGROUP:$code:$from".md5()

                VmpGroup(
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
        export.vmps.forEach { vmp ->
            vmp.datas.map { d ->
                val code = vmp.code.toString()
                val from = d.from?.toGregorianCalendar()?.timeInMillis
                val to = d.to?.toGregorianCalendar()?.timeInMillis

                val id = "VMP:$code:$from".md5()

                if (!currentVmps.contains(id) || force) Vmp(
                        from = from,
                        to = to,
                        code = code,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        abbreviation = d.abbreviation?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        vmpGroupId = vmpGroupIds[d.vmpGroup.code],
                        vtm = Vtm(code = d.vtm?.code?.toString(), name = d.vtm?.datas?.last()?.name?.let { SamText(it.fr, it.nl, it.de, it.en) }),
                        commentedClassifications = d.commentedClassifications?.mapNotNull { cc -> commentedClassificationMapper(cc)} ?: listOf(),
                        components = vmp.vmpComponents?.mapNotNull { vmpc ->
                            vmpc?.datas?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { comp ->
                                VmpComponent(
                                        virtualForm = comp.virtualForm?.let { virtualForm ->
                                            VirtualForm(virtualForm.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, virtualForm.standardForms?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        },
                                        routeOfAdministrations = comp.routeOfAdministrations?.map { roa ->
                                            RouteOfAdministration(roa.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, roa.standardRoutes?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        phaseNumber = comp.phaseNumber,
                                        name = comp.name?.let { SamText(it.fr, it.nl, it.de, it.en) }
                                )
                            }
                        } ?: listOf()
                ).apply {
                    this.id = id
                }.let { vmp ->
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

    private fun importActualMedicines(export: ExportActualMedicines, reimbursements: Map<Triple<String?, String?, String?>, MutableList<Reimbursement>>, couchdbConfig: CouchDbICureConnector, force: Boolean) {
        val ampDAO = AmpDAOImpl(couchdbConfig , UUIDGenerator())
        val currentAmps = HashSet(ampDAO.allIds)

        export.amps.forEach { amp ->
            amp.datas.map { d ->

                val code = amp.code.toString()
                val from = d.from?.toGregorianCalendar()?.timeInMillis
                val to = d.to?.toGregorianCalendar()?.timeInMillis

                val id = "AMP:$code:$from".md5()

                if (!currentAmps.contains(id) || force) Amp(
                        from = from,
                        to = to,
                        code = code,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        abbreviatedName = d.abbreviatedName?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        officialName = d.officialName,
                        vmpCode = amp.vmpCode?.toString(),
                        status = d.status?.value()?.let { AmpStatus.valueOf(it) },
                        blackTriangle = d.isBlackTriangle,
                        medicineType = d.medicineType?.value()?.let { MedicineType.valueOf(it) },
                        company = d.company?.datas?.maxBy { c -> c.from?.toGregorianCalendar()?.timeInMillis ?: 0L }?.let {
                            Company(it.from?.toGregorianCalendar()?.timeInMillis, it.to?.toGregorianCalendar()?.timeInMillis, it.authorisationNr,
                                    it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let {v -> mapOf(Pair(cc,v))}}, it.europeanNr, it.denomination, it.legalForm, it.building,
                                    it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
                        },
                        proprietarySuffix = d.proprietarySuffix?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        prescriptionName = d.prescriptionName?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        ampps = amp.ampps?.mapNotNull { ampp ->
                            ampp.datas?.maxBy { d -> d.from?.toGregorianCalendar()?.timeInMillis ?: 0 }?.let { amppd ->
                                Ampp(
                                        from = amppd.from?.toGregorianCalendar()?.timeInMillis,
                                        to = amppd.to?.toGregorianCalendar()?.timeInMillis,
                                        isOrphan = amppd.isOrphan,
                                        leafletLink = amppd.leafletLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        spcLink = amppd.spcLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        rmaPatientLink = amppd.rmaPatientLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        rmaProfessionalLink = amppd.rmaProfessionalLink?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        parallelCircuit = amppd.parallelCircuit,
                                        parallelDistributor = amppd.parallelDistributor,
                                        packMultiplier = amppd.packMultiplier,
                                        packAmount = amppd.packAmount?.let { Quantity(amppd.packAmount.value?.toInt(), amppd.packAmount?.unit) },
                                        packDisplayValue = amppd.packDisplayValue,
                                        status = amppd.status?.value()?.let { AmpStatus.valueOf(it) },
                                        atcs = amppd.atcs?.map { it.description } ?: listOf(),
                                        deliveryModus = amppd.deliveryModus?.description?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        deliveryModusSpecification = amppd.deliveryModusSpecification?.description?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        distributorCompany = amppd.distributorCompany ?.let {
                                            it.datas.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
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
                                        noGenericPrescriptionReasons = amppd.noGenericPrescriptionReasons?.map { SamText(it.description.fr, it.description.nl, it.description.de, it.description.en) } ?: listOf(),
                                        exFactoryPrice = amppd.exFactoryPrice?.toDouble(),
                                        reimbursementCode = amppd.reimbursementCode,
                                        definedDailyDose = Quantity(amppd.definedDailyDose?.value?.intValueExact(), amppd.definedDailyDose?.unit),
                                        officialExFactoryPrice = amppd.officialExFactoryPrice?.toDouble(),
                                        realExFactoryPrice = amppd.realExFactoryPrice?.toDouble(),
                                        pricingInformationDecisionDate = amppd.pricingInformationDecisionDate?.toGregorianCalendar()?.timeInMillis,
                                        components = ampp.amppComponents?.map { component ->
                                            component.datas.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                AmppComponent(
                                                        from = it.from?.toGregorianCalendar()?.timeInMillis, to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                        contentType = it.contentType?.let { ContentType.valueOf(it.value()) },
                                                        deviceType = it.deviceType?.let { DeviceType(code = it.code, edqmCode = it.edqmCode, edqmDefinition = it.edqmDefinition, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                                                        packagingType = it.packagingType?.let { PackagingType(code = it.code, edqmCode = it.edqmCode, edqmDefinition = it.edqmDefinition, name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) }) },
                                                        packSpecification = it.packSpecification,
                                                        contentMultiplier = it.contentMultiplier
                                                )
                                            } } ?: listOf(),
                                        commercializations = ampp.commercializations?.mapNotNull {
                                            it.datas.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                Commercialization(
                                                        from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar()?.timeInMillis
                                         ) } } ?: listOf(),
                                        dmpps = ampp.dmpps?.map { dmpp ->
                                            dmpp.datas.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                            Dmpp( from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                    to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                    deliveryEnvironment = dmpp.deliveryEnvironment?.let { DeliveryEnvironment.valueOf(it.value()) },
                                                    code = dmpp.code,
                                                    codeType = dmpp.codeType?.let { DmppCodeType.valueOf(it.value()) },
                                                    price = it.price?.toString(), cheap = it.isCheap, cheapest = it.isCheapest, reimbursable = it.isReimbursable,
                                                    reimbursements = reimbursements[Triple(dmpp.deliveryEnvironment?.value(), dmpp.codeType?.value(), dmpp.code)])
                                            }
                                        }
                                )
                            }
                        } ?: listOf(),
                        components = amp.ampComponents?.mapNotNull { ampc ->
                            ampc?.datas?.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let { comp ->
                                AmpComponent(
                                        pharmaceuticalForms = comp.pharmaceuticalForms?.map { pharmForm ->
                                            PharmaceuticalForm( pharmForm.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, pharmForm.standardForms?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
                                        } ?: listOf(),
                                        routeOfAdministrations = comp.routeOfAdministrations?.map { roa ->
                                            RouteOfAdministration(roa.name?.let { SamText(it.fr, it.nl, it.de, it.en) }, roa.standardRoutes?.map { Code(it.standard.value(), it.code, "1.0") } ?: listOf())
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
                                        ingredients = ampc.realActualIngredients?.mapNotNull { ingredient ->
                                            ingredient.datas.maxBy { d -> d.from.toGregorianCalendar().timeInMillis }?.let {
                                                Ingredient(
                                                        from = it.from?.toGregorianCalendar()?.timeInMillis,
                                                        to = it.to?.toGregorianCalendar()?.timeInMillis,
                                                        rank = ingredient.rank?.toInt(),
                                                        type = it.type?.let { IngredientType.valueOf(it.value()) },
                                                        knownEffect = it.isKnownEffect,
                                                        strength = it.strength?.let { Quantity(it.value?.toInt(), it.unit) },
                                                        strengthDescription = it.strengthDescription,
                                                        additionalInformation = it.additionalInformation,
                                                        substance = it.substance?.let {
                                                            Substance(
                                                                    code = it.code,
                                                                    chemicalForm = it.chemicalForm,
                                                                    name = it.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                    note = it.note?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                                                    standardSubstances = it.standardSubstances?.mapNotNull {
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
                ).apply {
                    this.id = id
                }.let { amp ->
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
