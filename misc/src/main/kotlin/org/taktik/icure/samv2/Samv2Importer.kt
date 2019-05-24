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
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper
import com.sun.xml.internal.ws.util.NoCloseInputStream


fun main(args: Array<String>) = Samv2Import().main(args)

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

class Samv2Import : CliktCommand() {
    val samv2url: String by option(help="The url of the zip file").prompt("Samv2 file url")
    val url: String by option(help="The database server to connect to").prompt("Database server url")
    val username: String by option(help="The Username").prompt("Username")
    val password: String by option(help="The Password").prompt("Password")
    val dbName: String by option(help="The database name").prompt("Database name")

    override fun run() {
        val httpClient = StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url(url).username(username).password(password).build()
        val dbInstance = StdCouchDbInstance(httpClient)
        val couchdbConfig = StdCouchDbICureConnector(dbName, dbInstance)

        URI(samv2url).toURL().openStream().let { zis ->
            val zip = ZipInputStream(zis)
            var entry: ZipEntry?
            while (zip.let { entry = it.nextEntry;entry != null }) {
                when {
                    entry!!.name.startsWith("AMP") ->
                        (JAXBContext.newInstance(ExportActualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportActualMedicines)?.let { importActualMedicines(it, couchdbConfig) }
                    entry!!.name.startsWith("VMP") ->
                        (JAXBContext.newInstance(ExportVirtualMedicines::class.java).createUnmarshaller().unmarshal(NoCloseInputStream(zip)) as? ExportVirtualMedicines)?.let { importVirtualMedicines(it, couchdbConfig) }
                }
            }
        }
    }

    private fun importVirtualMedicines(export: ExportVirtualMedicines, couchdbConfig: CouchDbICureConnector) {
        val vmpGroupDAO = VmpGroupDAOImpl(couchdbConfig , UUIDGenerator())
        val vmpDAO = VmpDAOImpl(couchdbConfig , UUIDGenerator())

        val vmpGroupIds = HashMap<Int,String>()
        val vmpIds = HashMap<Int,String>()

        export.vmpGroups.forEach { vmpg ->
            vmpg.datas.map { d ->
                vmpGroupDAO.create(VmpGroup(
                        from = d.from?.toGregorianCalendar()?.timeInMillis,
                        to = d.to?.toGregorianCalendar()?.timeInMillis,
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en) },
                        code = vmpg.code.toString(),
                        noGenericPrescriptionReason = d.noGenericPrescriptionReason?.let { reason ->
                            NoGenericPrescriptionReason(reason.code, reason.description?.let { SamText(it.fr, it.nl, it.de, it.en) })
                        },
                        noSwitchReason = d.noSwitchReason?.let { reason ->
                            NoSwitchReason(reason.code, reason.description?.let { SamText(it.fr, it.nl, it.de, it.en) })
                        }
                ).apply {
                    id = "VMPGROUP:$code:$from:$to".md5()
                })
            }.sortedBy { it.to }.lastOrNull()?.let {
                latestVmpGroup -> vmpGroupIds[vmpg.code] = latestVmpGroup.id
            }
        }
        export.vmps.forEach { vmp ->
            vmp.datas.map { d ->
                vmpDAO.create(Vmp(
                        from = d.from?.toGregorianCalendar()?.timeInMillis,
                        to = d.to?.toGregorianCalendar()?.timeInMillis,
                        code = vmp.code.toString(),
                        name = d.name?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        abbreviation = d.abbreviation?.let { SamText(it.fr, it.nl, it.de, it.en)},
                        vmpGroupId = vmpGroupIds[d.vmpGroup.code],
                        vtm = Vtm(code = d.vtm?.code?.toString(), name = d.vtm?.datas?.last()?.name?.let { SamText(it.fr, it.nl, it.de, it.en) })
                ).apply {
                    id = "VMP:$code:$from:$to".md5()
                })
            }.sortedBy { it.to }.lastOrNull()?.let {
                latestVmp -> vmpIds[vmp.code] = latestVmp.id
            }
        }
    }

    private fun importActualMedicines(export: ExportActualMedicines, couchdbConfig: CouchDbICureConnector) {
        val ampDAO = AmpDAOImpl(couchdbConfig , UUIDGenerator())
        export.amps.forEach { amp ->
            amp.datas.map { d ->
                ampDAO.create(Amp(
                        from = d.from?.toGregorianCalendar()?.timeInMillis,
                        to = d.to?.toGregorianCalendar()?.timeInMillis,
                        code = amp.code.toString(),
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
                                        packAmount = Quantity(amppd.packAmount.value?.intValueExact(), amppd.packAmount?.unit),
                                        packDisplayValue = amppd.packDisplayValue,
                                        status = amppd.status?.value()?.let { AmpStatus.valueOf(it) },
                                        atcs = amppd.atcs?.map { it.description } ?: listOf(),
                                        deliveryModus = amppd.deliveryModus?.description?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        deliveryModusSpecification = amppd.deliveryModusSpecification?.description?.let { SamText(it.fr, it.nl, it.de, it.en) },
                                        distributorCompany = amppd.distributorCompany.datas.sortedBy { d -> d.from.toGregorianCalendar().timeInMillis }.lastOrNull()?.let {
                                            Company(it.from?.toGregorianCalendar()?.timeInMillis, it.to?.toGregorianCalendar()?.timeInMillis, it.authorisationNr,
                                                    it.vatNr?.countryCode?.let { cc -> it.vatNr.value?.let {v -> mapOf(Pair(cc,v))}}, it.europeanNr, it.denomination, it.legalForm, it.building,
                                                    it.streetName, it.streetNum, it.postbox, it.postcode, it.city, it.countryCode, it.phone, it.language?.value(), it.website)
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
                    id = "AMP:$code:$from:$to".md5()
                })
            }
        }
    }
}
