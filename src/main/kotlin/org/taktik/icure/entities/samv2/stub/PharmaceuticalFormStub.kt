/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.samv2.stub

import java.io.Serializable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.samv2.embed.SamText

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PharmaceuticalFormStub(
	@JsonProperty("_id") val id: String? = null,
	val code: String? = null,
	val name: SamText? = null,
	val standardForms: Set<CodeStub> = emptySet(),
) : Serializable
