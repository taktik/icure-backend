/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import org.springframework.security.core.GrantedAuthority
import org.taktik.icure.entities.security.Permission
import org.taktik.icure.security.PermissionSetIdentifier
import java.io.Serializable
import java.util.Collections

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PermissionSetWithAuthorities(
    val permissionSetIdentifier: PermissionSetIdentifier,
    val permissions: Set<Permission>,
    val grantedAuthorities: Set<GrantedAuthority>
) : Serializable
