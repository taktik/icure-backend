/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.domain.filter.impl.predicate

import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.predicate.Predicate

class AlwaysPredicate : Predicate {
	override fun apply(input: Identifiable<String>) = true
}
