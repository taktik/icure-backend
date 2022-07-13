package org.taktik.icure.services.external.rest.shared.controllers.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.apache.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import org.taktik.icure.asynclogic.objectstorage.testutils.key1
import org.taktik.icure.asynclogic.objectstorage.testutils.key2
import org.taktik.icure.asynclogic.objectstorage.testutils.key3
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleUtis
import org.taktik.icure.services.external.rest.shared.controllers.core.DocumentControllerEndToEndTestContext.DataFactory.*
import org.taktik.icure.testutils.authorizationString
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.testutils.shouldRespondErrorStatus
import org.taktik.icure.utils.toByteArray

val client = WebClient.builder()
	.defaultHeader(HttpHeaders.AUTHORIZATION, authorizationString)
	.build()

fun <DTO : Any, METADTO : Any> StringSpec.documentControllerSharedEndToEndTests(
	context: DocumentControllerEndToEndTestContext<DTO, METADTO>,
	legacy: Boolean
): Unit = with (context) {
	"Creating a new document without attachment-related information should work and allow to retrieve it later" {
		val newDto = dataFactory.newDocumentNoAttachment()
		val createdDto = createDocument(newDto)
		newDto shouldBe createdDto.withoutDbUpdatedInfo
		val retrievedDto = getDocument(newDto.document.id)
		createdDto shouldBe retrievedDto
	}

	"Attempting to create a document with an id equivalent to an existing document should generate a CONFLICT error" {
		val dto = dataFactory.newDocumentNoAttachment()
		createDocument(dto)
		shouldRespondErrorStatus(HttpStatus.CONFLICT) { createDocument(dto) }
	}

	"Attempting to retrieve a non existing document should generate a NOT_FOUND error" {
		shouldRespondErrorStatus(HttpStatus.NOT_FOUND) { getDocument("not-real") }
	}

	"Adding a new attachment to a document should work and allow to retrieve it later" {
		listOf(randomSmallAttachment() to true, randomBigAttachment() to false).forEach { (attachmentBytes, inCouch) ->
			listOf(null, key1, key2).forEach { attachmentKey ->
				val dto = createDocument(dataFactory.newDocumentNoAttachment())
				val dtoWithAttachment = updateAttachment(dto.document.id, attachmentKey, dto.document.rev, attachmentBytes, sampleUtis)
				dtoWithAttachment.document.dataAttachment(attachmentKey).shouldNotBeNull().let {
					if (inCouch) it.shouldBeInCouch() else it.shouldBeInObjectStore()
					println(it.utis)
					it.utis shouldBe sampleUtis
				}
				getAttachment(dto.document.id, attachmentKey).toByteArray(true) shouldContainExactly attachmentBytes
			}
		}
	}

	"Updating an attachment should work and allow to retrieve it later" {
		val dto = createDocument(dataFactory.newDocumentNoAttachment())
		listOf(null, key1, key2).foldIndexed(dto.document.rev) { i, latestRev, attachmentKey ->
			val firstAttachment = randomSmallAttachment()
			val dtoWithFirstAttachment = updateAttachment(dto.document.id, attachmentKey, latestRev, firstAttachment, sampleUtis)
			dtoWithFirstAttachment.document.dataAttachment(attachmentKey).shouldNotBeNull().shouldBeInCouch()
			getAttachment(dto.document.id, attachmentKey).toByteArray(true) shouldContainExactly firstAttachment
			val secondAttachment = randomBigAttachment()
			val dtoWithSecondAttachment = updateAttachment(dto.document.id, attachmentKey, dtoWithFirstAttachment.document.rev, secondAttachment, sampleUtis)
			dtoWithSecondAttachment.document.dataAttachment(attachmentKey).shouldNotBeNull().shouldBeInObjectStore()
			getAttachment(dto.document.id, attachmentKey).toByteArray(true) shouldContainExactly secondAttachment
			dtoWithSecondAttachment.document.deletedAttachments shouldHaveSize (i + 1)
			ensureDeleted(dtoWithFirstAttachment.document, attachmentKey)
			dtoWithSecondAttachment.document.rev
		}
	}

	"Deleting an attachment should update document accordingly and delete it from the storage service" {
		val dto = createDocument(dataFactory.newDocumentNoAttachment())
		listOf(null, key1, key2).foldIndexed(dto.document.rev) { i, latestRev, attachmentKey ->
			val isSmall = i % 2 == 0
			val dtoWithAttachment = updateAttachment(
				dto.document.id,
				attachmentKey,
				latestRev,
				if (isSmall) randomSmallAttachment() else randomBigAttachment(),
				sampleUtis
			)
			dtoWithAttachment.document.deletedAttachments.shouldHaveSize(i)
			val minimumDeletionTime = System.currentTimeMillis()
			val dtoWithDeletedAttachment = deleteAttachment(dto.document.id, attachmentKey, dtoWithAttachment.document.rev)
			val attachmentId = (
				if (attachmentKey != null)
					dtoWithAttachment.document.secondaryAttachments.getValue(attachmentKey)
				else
					dtoWithAttachment.document.mainAttachment!!
				).let { if (isSmall) it.couchDbAttachmentId else it.objectStoreAttachmentId }
			dtoWithDeletedAttachment.document.deletedAttachments.shouldHaveSize(i + 1)
				.firstOrNull { if (isSmall) it.couchDbAttachmentId == attachmentId else it.objectStoreAttachmentId == attachmentId }
				.shouldNotBeNull()
				.apply {
					if (isSmall) {
						objectStoreAttachmentId shouldBe null
					} else {
						couchDbAttachmentId shouldBe null
					}
					if (attachmentKey == null) key shouldBe dto.document.mainAttachmentKey else key shouldBe attachmentKey
					deletionTime.shouldNotBeNull() shouldBeGreaterThanOrEqual minimumDeletionTime
				}
			shouldRespondErrorStatus(HttpStatus.NOT_FOUND) {
				getAttachment(dto.document.id, attachmentKey).toByteArray(true)
			}
			ensureDeleted(dtoWithAttachment.document, attachmentKey)
			dtoWithDeletedAttachment.document.rev
		}
	}

	"Bulk attachment changes should allow to create, update or delete multiple attachments at the same time, without changing unrelated attachments" {
		val key2Attachment = randomBigAttachment()
		val doc = createDocument(dataFactory.newDocumentNoAttachment()).document.let {
			updateAttachment(it.id, key1, it.rev, randomSmallAttachment(), sampleUtis)
		}.document.let {
			updateAttachment(it.id, key2, it.rev, key2Attachment, sampleUtis)
		}.document.let {
			updateMainAttachment(it.id, it.rev, randomBigAttachment(), emptyList())
		}.document
		val mainContent = randomSmallAttachment()
		val key3Content = randomBigAttachment()
		val updatedDoc = updateAttachments(
			doc.id,
			doc.rev,
			options = dataFactory.bulkAttachmentUpdateOptions(
				deleteAttachments = setOf(key1),
				updateAttachmentsMetadata = mapOf(
					doc.mainAttachmentKey to UpdateAttachmentMetadata(null, sampleUtis)
				)
			),
			attachments = mapOf(
				doc.mainAttachmentKey to mainContent,
				key3 to key3Content
			)
		).document
		updatedDoc.mainAttachment.shouldNotBeNull().shouldBeInCouch().utis shouldBe sampleUtis
		updatedDoc.secondaryAttachments[key1] shouldBe null
		ensureDeleted(doc, key1)
		updatedDoc.deletedAttachments.shouldHaveSize(2)
		updatedDoc.secondaryAttachments[key2].shouldNotBeNull().shouldBeInObjectStore()
		updatedDoc.secondaryAttachments[key3].shouldNotBeNull().shouldBeInObjectStore()
		getMainAttachment(doc.id).toByteArray(true) shouldContainExactly mainContent
		getSecondaryAttachment(doc.id, key2).toByteArray(true) shouldContainExactly key2Attachment
		getSecondaryAttachment(doc.id, key3).toByteArray(true) shouldContainExactly key3Content
	}

	"Updating or deleting an attachment should fail with CONFLICT if the provided revision is not the latest" {
		listOf(null, key1, key2).forEach { attachmentKey ->
			val doc = createDocument(dataFactory.newDocumentNoAttachment()).document
			val docWithAttachment = updateAttachment(doc.id, attachmentKey, doc.rev, randomSmallAttachment(), sampleUtis).document
			shouldRespondErrorStatus(HttpStatus.CONFLICT) {
				updateAttachment(doc.id, attachmentKey, doc.rev, randomSmallAttachment(), sampleUtis)
			}
			shouldRespondErrorStatus(HttpStatus.CONFLICT) {
				deleteAttachment(doc.id, attachmentKey, doc.rev)
			}
			getDocument(doc.id).document.rev shouldBe docWithAttachment.rev
		}
	}

	"Updating or deleting an attachment should fail with BAD REQUEST if no revision was provided (except for main attachment non-bulk change if legacy)" {
		listOf(null, key1, key2).forEach { attachmentKey ->
			val doc = createDocumentWithAttachment(dataFactory.newDocumentNoAttachment(), randomSmallAttachment(), attachmentKey).document
			if (!legacy || attachmentKey != null) {
				shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
					updateAttachment(doc.id, attachmentKey, null, randomSmallAttachment(), sampleUtis)
				}
				shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
					deleteAttachment(doc.id, attachmentKey, null)
				}
			}
			shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
				updateAttachments(
					doc.id,
					null,
					options = dataFactory.bulkAttachmentUpdateOptions(
						deleteAttachments = setOf(attachmentKey ?: doc.mainAttachmentKey),
						updateAttachmentsMetadata = emptyMap()
					),
					attachments = emptyMap()
				)
			}
			getDocument(doc.id).document.rev shouldBe doc.rev
		}
	}

	"Updating an attachment should fail with BAD REQUEST if the size was not provided (except for main attachment non-bulk update if legacy)" {
		listOf(null, key1, key2).forEach { attachmentKey ->
			val doc = createDocument(dataFactory.newDocumentNoAttachment()).document
			// Both netty http client and spring web client automatically set content length and can't be removed: for now only do test on bulk
			/*
			if (!legacy || attachmentKey != null) {
				shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) { updateAttachment(doc.id, attachmentKey, doc.rev, randomSmallAttachment(), sampleUtis) }
			}
			 */
			shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
				updateAttachments(
					doc.id,
					doc.rev,
					options = dataFactory.bulkAttachmentUpdateOptions(
						deleteAttachments = emptySet(),
						updateAttachmentsMetadata = emptyMap()
					),
					attachments = mapOf((attachmentKey ?: doc.mainAttachmentKey) to randomSmallAttachment()),
					includeSize = false
				)
			}
			getDocument(doc.id).document.rev shouldBe doc.rev
		}
	}

	"Deleting an attachment should fail with BAD REQUEST if the attachment does not actually exist (except for main attachment non-bulk if legacy)" {
		listOf(null, key1, key2).forEach { attachmentKey ->
			val doc = createDocument(dataFactory.newDocumentNoAttachment()).document
			if (!legacy || attachmentKey != null) {
				shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
					deleteAttachment(doc.id, attachmentKey, doc.rev)
				}
			}
			shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
				updateAttachments(
					doc.id,
					doc.rev,
					options = dataFactory.bulkAttachmentUpdateOptions(
						deleteAttachments = setOf(attachmentKey ?: doc.mainAttachmentKey),
						updateAttachmentsMetadata = emptyMap()
					),
					attachments = emptyMap()
				)
			}
			getDocument(doc.id).document.rev shouldBe doc.rev
		}
	}

	"Modification of document should allow changes to non-attachment information and attachment utis" {
		val dtoWithAttachment = createDocumentWithAttachment(dataFactory.newDocumentNoAttachment(), randomSmallAttachment(), null)
		val desiredDto = dtoWithAttachment.changeNonAttachmentInfo().changeMainAttachmentUtis()
		val updatedDto = updateDocument(desiredDto)
		desiredDto.withoutDbUpdatedInfo shouldBe updatedDto.withoutDbUpdatedInfo
	}

	"Bulk modification of document should allow changes to non-attachment information and attachment utis" {
		val dtosWithAttachment = (1..10).map { i ->
			createDocumentWithAttachment(dataFactory.newDocumentNoAttachment(i), randomSmallAttachment(), null)
		}
		val desiredDtos = dtosWithAttachment.map { it.changeNonAttachmentInfo().changeMainAttachmentUtis() }
		val updatedDtos = bulkModify(desiredDtos)
		updatedDtos.map { it.withoutDbUpdatedInfo }.toList() shouldContainExactlyInAnyOrder desiredDtos.map { it.withoutDbUpdatedInfo }
	}

	"Bulk modification of document should also allow creation of new documents" {
		val existingDtos = (1..5).map { i ->
			createDocumentWithAttachment(dataFactory.newDocumentNoAttachment(i), randomSmallAttachment(), null)
		}
		val updatedDtos = existingDtos.map { it.changeNonAttachmentInfo().changeMainAttachmentUtis() }
		val newDtos = (6..10).map { i -> dataFactory.newDocumentNoAttachment(i) }
		val newAndUpdated = bulkModify(updatedDtos + newDtos)
		newAndUpdated.map { it.withoutDbUpdatedInfo }.toList() shouldContainExactlyInAnyOrder (updatedDtos + newDtos).map { it.withoutDbUpdatedInfo }
	}

	"Creation of a new document with initialized secondary attachments should not be allowed (BAD REQUEST)" {
		shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
			createDocument(dataFactory.newDocumentNoAttachment().addSecondaryAttachment())
		}
	}

	"Creation of a new document with initialized deleted attachments should not be allowed (BAD REQUEST)" {
		shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
			createDocument(dataFactory.newDocumentNoAttachment().addDeletedAttachment())
		}
	}

	"Direct modification of secondary attachments id information should not be allowed (BAD REQUEST)" {
		val initial = createDocumentWithAttachment(dataFactory.newDocumentNoAttachment(), randomBigAttachment(), key1)
		shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
			updateDocument(initial.changeAttachmentId(key1))
		}
	}

	"Direct modification of deleted attachments should not be allowed (BAD REQUEST)" {
		val initial = createDocument(dataFactory.newDocumentNoAttachment())
		shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
			updateDocument(initial.addDeletedAttachment())
		}
	}
}
