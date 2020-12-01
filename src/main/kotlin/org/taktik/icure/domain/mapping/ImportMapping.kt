package org.taktik.icure.domain.mapping

import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

class ImportMapping(
        val lifecycle: String? = null,
        val content: String? = null,
        val cdLocal: String? = null,
        val label: Map<String, String> = HashMap(),
        val tags: Set<CodeStub> = setOf()
) : Serializable
