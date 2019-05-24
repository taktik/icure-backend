package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.NoGenericPrescriptionReason
import org.taktik.icure.entities.samv2.embed.NoSwitchReason
import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.StoredDocumentWithPeriod
import java.io.Serializable

class VmpGroup(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamText? = null,
        var noGenericPrescriptionReason: NoGenericPrescriptionReason? = null,
        var noSwitchReason: NoSwitchReason? = null
) : StoredDocumentWithPeriod(from, to), Serializable
