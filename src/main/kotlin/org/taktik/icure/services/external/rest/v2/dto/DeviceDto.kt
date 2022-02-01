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
package org.taktik.icure.services.external.rest.v2.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.pozo.KotlinBuilder
import io.swagger.v3.oas.annotations.media.Schema
import org.taktik.icure.services.external.rest.v2.dto.base.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
@Schema(description = """This entity is a root level object. It represents a healthcare party. It is serialized in JSON and saved in the underlying icure-healthdata CouchDB database.""")
data class DeviceDto(
        override val id: String,
        override val rev: String? = null,
        override val deletionDate: Long? = null,

        val externalId: String? = null,

        override val name: String? = null,
        val type: String? = null, // "persphysician" or "medicalHouse" or "perstechnician"
        val brand: String? = null,
        val model: String? = null,
        val serialNumber: String? = null,

        val parentId: String? = null,
        val picture: ByteArray? = null,

        override val properties: Set<PropertyStubDto> = emptySet(),

        // One AES key per HcParty, encrypted using this hcParty public key and the other hcParty public key
        // For a pair of HcParties, this key is called the AES exchange key
        // Each HcParty always has one AES exchange key for himself
        // The map's keys are the delegate id.
        // In the table, we get at the first position: the key encrypted using owner (this)'s public key and in 2nd pos.
        // the key encrypted using delegate's public key.
        override val hcPartyKeys: Map<String, Array<String>> = emptyMap(),
        override val privateKeyShamirPartitions: Map<String, String> = emptyMap(), //Format is hcpId of key that has been partitioned : "threshold|partition in hex"
        override val publicKey: String? = null,
) : StoredDocumentDto, NamedDto, CryptoActorDto, DataOwnerDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
