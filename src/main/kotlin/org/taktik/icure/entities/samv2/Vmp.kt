package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.StoredDocumentWithPeriod
import org.taktik.icure.entities.samv2.embed.Vtm
import org.taktik.icure.entities.samv2.embed.Wada
import java.io.Serializable

class Vmp(
        from: Long? = null,
        to: Long? = null,
        var code: String? = null,
        var name: SamText? = null,
        var abbreviation: SamText? = null,
        var vmpGroupId: String? = null,
        var vtm: Vtm? = null,
        var wadas: List<Wada>? = null
) : StoredDocumentWithPeriod(from, to), Serializable
