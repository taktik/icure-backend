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
import org.taktik.icure.be.samv2.entities.CommentedClassificationFullDataType


fun main(args: Array<String>) = Samv2Import().main(args)

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun commentedClassificationMapper(cc:CommentedClassificationFullDataType) : CommentedClassification? = cc.datas?.sortedBy { d -> d.from.toGregorianCalendar().timeInMillis }?.lastOrNull()?.let { lcc ->
    CommentedClassification(
            lcc.title?.let { SamText(it.fr, it.nl, it.de, it.en) },
            lcc.url?.let { SamText(it.fr, it.nl, it.de, it.en) },
            cc.commentedClassifications?.mapNotNull { cc -> commentedClassificationMapper(cc) } ?: listOf()
    )
}

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

        URI(samv2url).toURL().openStream().let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry;entry != null }) {
                when {
                    entry!!.name.startsWith("AMP") ->
                        (JAXBContext.newInstance(ExportActualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportActualMedicines)?.let { importActualMedicines(it, couchdbConfig, updateExistingDocs) }
                    entry!!.name.startsWith("VMP") ->
                        (JAXBContext.newInstance(ExportVirtualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportVirtualMedicines)?.let { importVirtualMedicines(it, couchdbConfig, updateExistingDocs) }
                }
            }
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
                        if(prev != vmpg) {
                            vmpGroupDAO.update(vmpg.apply {this.rev=prev.rev})
                        }
                        vmpg
                    } else vmpg
                }
            }.sortedBy { it.to }.lastOrNull()?.let {
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
                            vmpc?.datas?.sortedBy { d -> d.from.toGregorianCalendar().timeInMillis }?.lastOrNull()?.let { comp ->
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

    private fun importActualMedicines(export: ExportActualMedicines, couchdbConfig: CouchDbICureConnector, force: Boolean) {
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
                        company = d.company?.datas?.sortedBy { c -> c.from?.toGregorianCalendar()?.timeInMillis ?: 0L }?.lastOrNull()?.let {
                            Company(it.from?.toGregorianCalendar()?.timeInMillis, it.to?.toGregorianCalendar()?.timeInMillis, it.authorisationNr,
                                    it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let {v -> mapOf(Pair(cc,v))}}, it.europeanNr, it.denomination, it.legalForm, it.building,
                                    it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
                        },
                        proprietarySuffix = d.proprietarySuffix?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        prescriptionName = d.prescriptionName?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        ampps = amp.ampps?.mapNotNull { ampp ->
                            ampp.datas?.sortedBy { d -> d.from?.toGregorianCalendar()?.timeInMillis }?.lastOrNull()?.let { amppd ->
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
                                            amppd.distributorCompany.datas.sortedBy { d -> d.from.toGregorianCalendar().timeInMillis }.lastOrNull()?.let {
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
                                        pricingInformationDecisionDate = amppd.pricingInformationDecisionDate?.toGregorianCalendar()?.timeInMillis
                                )
                            }
                        } ?: listOf(),
                        components = amp.ampComponents?.mapNotNull { ampc ->
                            ampc?.datas?.sortedBy { d -> d.from.toGregorianCalendar().timeInMillis }?.lastOrNull()?.let { comp ->
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
                                        note = comp.note?.let { SamText(it.fr, it.nl, it.de, it.en) }
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
