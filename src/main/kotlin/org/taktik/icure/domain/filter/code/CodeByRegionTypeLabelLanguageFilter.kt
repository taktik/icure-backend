package org.taktik.icure.domain.filter.code

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.base.Code

interface CodeByRegionTypeLabelLanguageFilter : Filter<String, Code> {
    val region: String?
    val type: String?
    val language: String?
    val label: String?
}
