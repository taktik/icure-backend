/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import org.springframework.security.core.userdetails.UserDetails

interface UserDetails : UserDetails {
	val permissionSetIdentifier: PermissionSetIdentifier
	val isRealAuth: Boolean
	val locale: String?
	val logoutURL: String?
}
