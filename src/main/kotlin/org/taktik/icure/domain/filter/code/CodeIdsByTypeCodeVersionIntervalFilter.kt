package org.taktik.icure.domain.filter.code

import org.taktik.icure.domain.filter.Filter
import org.taktik.icure.entities.base.Code

interface CodeIdsByTypeCodeVersionIntervalFilter : Filter<String, Code> {
	val startType: String?
	val startCode: String?
	val startVersion: String?
	val endType: String?
	val endCode: String?
	val endVersion: String?
}
