package org.taktik.icure.entities.base

import org.taktik.icure.entities.utils.MergeUtil

interface DataOwner {
	val properties: Set<PropertyStub>

	fun solveConflictsWith(other: DataOwner): Map<String, Any?> {
		return mapOf(
			"properties" to MergeUtil.mergeSets(this.properties, other.properties),
		)
	}
}
