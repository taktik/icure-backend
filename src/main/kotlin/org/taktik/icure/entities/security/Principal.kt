/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.entities.security

import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.entities.base.Named
import org.taktik.icure.entities.base.PropertyStub

interface Principal : Identifiable<String>, Named {
	val permissions: Set<Permission>
	val properties: Set<PropertyStub>
	fun getParents(): Set<String>
}
