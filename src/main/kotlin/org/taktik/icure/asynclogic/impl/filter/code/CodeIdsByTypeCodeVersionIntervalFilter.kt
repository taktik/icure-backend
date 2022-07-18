package org.taktik.icure.asynclogic.impl.filter.code

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.entities.base.Code
import org.taktik.icure.domain.filter.code.CodeIdsByTypeCodeVersionIntervalFilter

@Service
class CodeIdsByTypeCodeVersionIntervalFilter(private val codeLogic: CodeLogic) : Filter<String, Code, CodeIdsByTypeCodeVersionIntervalFilter> {

	override fun resolve(filter: CodeIdsByTypeCodeVersionIntervalFilter, context: Filters): Flow<String> {
		return codeLogic.listCodeIdsByTypeCodeVersionInterval(
			filter.startType,
			filter.startCode,
			filter.startVersion,
			filter.endType,
			filter.endCode,
			filter.endVersion
		)
	}

}
