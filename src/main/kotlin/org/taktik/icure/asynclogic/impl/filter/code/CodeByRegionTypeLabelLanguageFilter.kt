package org.taktik.icure.asynclogic.impl.filter.code

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asynclogic.impl.filter.Filter
import org.taktik.icure.asynclogic.impl.filter.Filters
import org.taktik.icure.domain.filter.code.CodeByRegionTypeLabelLanguageFilter
import org.taktik.icure.entities.base.Code

class CodeByRegionTypeLabelLanguageFilter(private val codeLogic: CodeLogic) : Filter<String, Code, CodeByRegionTypeLabelLanguageFilter> {
    override fun resolve(filter: CodeByRegionTypeLabelLanguageFilter, context: Filters): Flow<String> {
        return codeLogic.listCodeIdsByLabel(filter.region, filter.language, filter.type, filter.label)
    }
}
