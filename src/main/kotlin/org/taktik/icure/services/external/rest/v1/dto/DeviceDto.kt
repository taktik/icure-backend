/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.rest.v1.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.CryptoActorDto
import org.taktik.icure.services.external.rest.v1.dto.base.DataOwnerDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifierDto
import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity is a root level object. It represents a device. It is serialized in JSON and saved in the underlying icure-base CouchDB database.""")
data class DeviceDto(
	override val id: String,
	override val rev: String? = null,
	override val deletionDate: Long? = null,

	val identifiers: List<IdentifierDto> = emptyList(),

	override val created: Long? = null,
	override val modified: Long? = null,
	override val author: String? = null,
	override val responsible: String? = null,
	override val tags: Set<CodeStubDto> = emptySet(),
	override val codes: Set<CodeStubDto> = emptySet(),
	override val endOfLife: Long? = null,
	override val medicalLocationId: String? = null,

	val externalId: String? = null,

	override val name: String? = null,
	val type: String? = null,
	val brand: String? = null,
	val model: String? = null,
	val serialNumber: String? = null,

	val parentId: String? = null,
	val picture: ByteArray? = null,

	override val properties: Set<PropertyStubDto> = emptySet(),
	override val hcPartyKeys: Map<String, Array<String>> = emptyMap(),
	override val aesExchangeKeys: Map<String, Map<String, Map<String, String>>> = emptyMap(),
	override val transferKeys: Map<String, Map<String, String>> = emptyMap(),
	override val privateKeyShamirPartitions: Map<String, String> = emptyMap(), //Format is hcpId of key that has been partitioned : "threshold|partition in hex"
	override val publicKey: String? = null,
) : StoredDocumentDto, ICureDocumentDto<String>, NamedDto, CryptoActorDto, DataOwnerDto {
	override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
	override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
