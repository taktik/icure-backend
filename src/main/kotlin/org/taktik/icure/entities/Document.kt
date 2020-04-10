/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.DocumentLocation
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.security.CryptoUtils
import java.io.Serializable
import java.nio.ByteBuffer
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.HashSet
import java.util.UUID
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Document(id: String,
               rev: String? = null,
               revisionsInfo: Array<RevisionInfo> = arrayOf(),
               conflicts: Array<String> = arrayOf(),
               revHistory: Map<String, String> = mapOf()) : StoredICureDocument(id, rev, revisionsInfo, conflicts, revHistory), Serializable {
    var attachmentId: String? = null

    @JsonIgnore
    var attachment: ByteArray? = null

    @JsonIgnore
    var isAttachmentDirty = false
    var documentLocation: DocumentLocation? = null
    var documentType: DocumentType? = null
    var documentStatus: DocumentStatus? = null
    var externalUri: String? = null
    var mainUti: String? = null
    var name: String? = null
    protected var otherUtis: MutableSet<String> = HashSet()

    //The ICureDocument (Form, Contact, ...) that has been used to generate the document
    var storedICureDocumentId: String? = null
    fun solveConflictWith(other: Document): Document {
        super.solveConflictsWith(other)
        mergeFrom(other)
        return this
    }

    fun mergeFrom(other: Document) {
        otherUtis.addAll(other.otherUtis)
        if (documentLocation == null && other.documentLocation != null) {
            documentLocation = other.documentLocation
        }
        if (documentType == null && other.documentType != null) {
            documentType = other.documentType
        }
        if (documentStatus == null && other.documentStatus != null) {
            documentStatus = other.documentStatus
        }
        if (externalUri == null && other.externalUri != null) {
            externalUri = other.externalUri
        }
        if (mainUti == null && other.mainUti != null) {
            mainUti = other.mainUti
        }
        if (name == null && other.name != null) {
            name = other.name
        }
        if (storedICureDocumentId == null && other.storedICureDocumentId != null) {
            storedICureDocumentId = other.storedICureDocumentId
        }
        if (attachment == null) {
            attachment = other.attachment
        } else if (other.attachment != null && attachment!!.size < other.attachment!!.size) {
            attachment = other.attachment
            attachmentId = null
        }
    }

    fun decryptAttachment(enckeys: List<String?>?): ByteArray? {
        if (enckeys != null && enckeys.size > 0) {
            for (sfk in enckeys) {
                val bb = ByteBuffer.wrap(ByteArray(16))
                val uuid = UUID.fromString(sfk)
                bb.putLong(uuid.mostSignificantBits)
                bb.putLong(uuid.leastSignificantBits)
                try {
                    return CryptoUtils.decryptAES(attachment, bb.array())
                } catch (ignored: NoSuchPaddingException) {
                } catch (ignored: NoSuchAlgorithmException) {
                } catch (ignored: IllegalArgumentException) {
                } catch (ignored: BadPaddingException) {
                } catch (ignored: InvalidKeyException) {
                } catch (ignored: IllegalBlockSizeException) {
                } catch (ignored: InvalidAlgorithmParameterException) {
                }
            }
        }
        return attachment
    }
}
