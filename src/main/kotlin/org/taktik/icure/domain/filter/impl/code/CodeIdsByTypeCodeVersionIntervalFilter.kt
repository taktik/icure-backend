package org.taktik.icure.domain.filter.impl.code

import com.github.pozo.KotlinBuilder
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.base.Code

@KotlinBuilder
data class CodeIdsByTypeCodeVersionIntervalFilter (
	override val desc: String?,
	override val startType: String?,
	override val startCode: String?,
	override val startVersion: String?,
	override val endType: String?,
	override val endCode: String?,
	override val endVersion: String?
) : AbstractFilter<Code>, org.taktik.icure.domain.filter.code.CodeIdsByTypeCodeVersionIntervalFilter {

	override fun matches(item: Code): Boolean {
		val typeCondition = item.type != null &&
			(startType == null || item.type >= startType) &&
			(endType == null || item.type <= endType)
		val codeCondition = item.code != null &&
			(startCode == null || item.code >= startCode) &&
			(endCode == null || item.code <= endCode)
		val versionCondition = item.version != null &&
			(startVersion == null || item.version >= startVersion) &&
			(endVersion == null || item.version <= endVersion)
		return typeCondition && codeCondition && versionCondition;
	}
}
