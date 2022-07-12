package org.taktik.icure.services.external.rest.v1.controllers.core

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.spring.SpringListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asPublisher
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToFlow
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.testutils.htmlUti
import org.taktik.icure.asynclogic.objectstorage.testutils.javascriptUti
import org.taktik.icure.asynclogic.objectstorage.testutils.jsonUti
import org.taktik.icure.asynclogic.objectstorage.testutils.sampleUtis
import org.taktik.icure.asynclogic.objectstorage.testutils.xmlUti
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.services.external.rest.shared.controllers.core.DocumentControllerEndToEndTestContext
import org.taktik.icure.services.external.rest.shared.controllers.core.client
import org.taktik.icure.services.external.rest.shared.controllers.core.documentControllerSharedEndToEndTests
import org.taktik.icure.services.external.rest.v1.dto.DocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentTypeDto
import org.taktik.icure.services.external.rest.v1.mapper.DocumentMapper
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.testutils.bytesContent
import org.taktik.icure.testutils.multipartContent
import org.taktik.icure.testutils.shouldContainExactly
import org.taktik.icure.testutils.shouldNotContainExactly
import org.taktik.icure.testutils.shouldRespondErrorStatus
import org.taktik.icure.testutils.uriWithVars
import org.taktik.icure.utils.toByteArray
import reactor.core.publisher.Mono

private const val CONTROLLER_ROOT = "rest/v1/document"
private const val TEST_CACHE = "build/tests/icureCache"

@SpringBootTest(
	classes = [ICureTestApplication::class],
	properties = [
		"spring.main.allow-bean-definition-overriding=true",
		"icure.documentstorage.cacheLocation=$TEST_CACHE",
		"icure.documentstorage.backlogToObjectStorage=true",
		"icure.documentstorage.sizeLimit=1000",
		"icure.documentstorage.migrationDelayMs=1000",
	],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("app")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentControllerEndToEndTest(
	properties: ObjectStorageProperties,
	documentMapper: DocumentMapper,
	dao: DocumentDAO,
	objectStorageClient: DocumentObjectStorageClient,
	@LocalServerPort port: Int
) : StringSpec() {
	init {
		listeners(SpringListener)

		afterSpec {
			runCatching { File(TEST_CACHE).deleteRecursively() }
		}

		val context = object : DocumentControllerEndToEndTestContext<DocumentDto, DocumentController.BulkAttachmentUpdateOptions>() {
			override val port: Int = port
			override val controllerRoot: String = CONTROLLER_ROOT
			override val properties: ObjectStorageProperties = properties
			override val dao: DocumentDAO = dao
			override val objectStorageClient: DocumentObjectStorageClient = objectStorageClient

			override fun WebClient.RequestBodySpec.dtoBody(dto: DocumentDto): WebClient.RequestHeadersSpec<*> =
				body<DocumentDto>(Mono.just(dto))

			override fun WebClient.RequestBodySpec.dtosBody(dtos: List<DocumentDto>): WebClient.RequestHeadersSpec<*> =
				body<DocumentDto>(dtos.asFlow().asPublisher())

			override suspend fun WebClient.ResponseSpec.awaitDto(): DocumentDto =
				awaitBody()

			override suspend fun WebClient.ResponseSpec.dtoFlow(): Flow<DocumentDto> =
				bodyToFlow()

			override val DocumentDto.document get() = documentMapper.map(this)

			override val DocumentDto.withoutDbUpdatedInfo get() = copy(
				rev = null,
				created = null,
				modified = null,
				author = null,
				responsible = null
			)

			override fun DocumentDto.changeNonAttachmentInfo(): DocumentDto =
				copy(name = "Document name ${random.nextInt()}", externalUri = "Some uri ${random.nextInt()}")

			override fun DocumentDto.changeMainAttachmentUtis(): DocumentDto =
				copy(mainUti = listOf(jsonUti, htmlUti, xmlUti, javascriptUti).random(random), otherUtis = emptySet())

			override val dataFactory = object : DataFactory<DocumentDto, DocumentController.BulkAttachmentUpdateOptions> {
				override fun newDocumentNoAttachment(index: Int?) = DocumentDto(
					UUID.randomUUID().toString(),
					name = index?.let { "Document $it" } ?: "New document",
					documentType = DocumentTypeDto.admission
				)

				override fun bulkAttachmentUpdateOptions(
					deleteAttachments: Set<String>,
					updateAttachmentsMetadata: Map<String, DataFactory.UpdateAttachmentMetadata>
				) = DocumentController.BulkAttachmentUpdateOptions(
					deleteAttachments = deleteAttachments,
					updateAttachmentsMetadata = updateAttachmentsMetadata.mapValues {
						DocumentController.BulkAttachmentUpdateOptions.AttachmentMetadata(it.value.size, it.value.utis)
					}
				)
			}
		}

		documentControllerSharedEndToEndTests(context, true)

		v1EndToEndTests(context)
	}
}

// Test legacy behaviour to ensure retro-compatibility
private fun StringSpec.v1EndToEndTests(
	context: DocumentControllerEndToEndTestContext<DocumentDto, DocumentController.BulkAttachmentUpdateOptions>
): Unit = with (context) {
	val encKey = UUID.randomUUID().toString()

	"Updating or deleting the main attachment should not require to specify a revision" {
		val doc = createDocument(dataFactory.newDocumentNoAttachment())
		val newAttachment = randomBigAttachment()
		updateMainAttachment(doc.id, null, newAttachment, emptyList()).document.mainAttachment.shouldNotBeNull().shouldBeInObjectStore()
		getMainAttachment(doc.id).toByteArray(true) shouldContainExactly newAttachment
		deleteMainAttachment(doc.id, null).document.mainAttachment shouldBe null
		shouldRespondErrorStatus(HttpStatus.NOT_FOUND) { getMainAttachment(doc.id).toByteArray(true) }
	}

	"Providing encryption keys when setting a new attachment should encrypt it" {
		val doc = createDocument(dataFactory.newDocumentNoAttachment())
		val unencrypted = randomBigAttachment()
		updateMainAttachment(
			doc.id,
			doc.rev,
			unencrypted,
			sampleUtis,
			mapOf("enckeys" to encKey)
		).document.mainAttachment.shouldNotBeNull()
		getMainAttachment(doc.id).toByteArray(true) shouldNotContainExactly unencrypted
	}

	"Attempting to create a new attachment providing invalid encryption keys should fail" {
		val doc = createDocument(dataFactory.newDocumentNoAttachment())
		shouldRespondErrorStatus(HttpStatus.BAD_REQUEST) {
			updateMainAttachment(
				doc.id,
				doc.rev,
				randomBigAttachment(),
				sampleUtis,
				mapOf("enckeys" to encKey + "a")
			)
		}
	}

	"Getting an attachment with encryption keys should decrypt the attachment" {
		val doc = createDocument(dataFactory.newDocumentNoAttachment())
		val unencrypted = randomBigAttachment()
		updateMainAttachment(
			doc.id,
			doc.rev,
			unencrypted,
			sampleUtis,
			mapOf("enckeys" to encKey)
		).document.mainAttachment.shouldNotBeNull()
		getMainAttachment(doc.id, mapOf("enckeys" to encKey)).toByteArray(true) shouldContainExactly unencrypted
	}

	"Updating main attachment with multipart should be allowed" {
		val doc = createDocument(dataFactory.newDocumentNoAttachment())
		val data = randomSmallAttachment()
		val multipartBody =
			MultipartBodyBuilder().apply {
				asyncPart(
					"attachment",
					flowOf(DefaultDataBufferFactory.sharedInstance.wrap(data)).asPublisher(),
					DataBuffer::class.java
				).header(HttpHeaders.CONTENT_LENGTH, data.size.toString())
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
			}.build()
		val updated = client.put()
			.uriWithVars(
				"http://127.0.0.1:$port/$controllerRoot/${doc.id}/attachment/multipart",
				mapOf(
					"rev" to doc.rev,
					"utis" to sampleUtis
				)
			)
			.multipartContent()
			.body(BodyInserters.fromMultipartData(multipartBody))
			.retrieve()
			.awaitDto()
		updated.document.mainAttachment.shouldNotBeNull().apply {
			shouldBeInCouch()
			utis shouldBe sampleUtis
		}
		getMainAttachment(doc.id).toByteArray(true) shouldContainExactly data
	}

	"Attempting to modify a non-existing document should trigger creation of a new document" {
		val dto = dataFactory.newDocumentNoAttachment()
		val created = updateDocument(dto)
		created.withoutDbUpdatedInfo shouldBe dto.withoutDbUpdatedInfo
		val retrieved = getDocument(dto.id)
		retrieved shouldBe created
	}

	"Changing a document attachment id in an update should trigger deletion of the attachment" {
		val doc = createDocument(dataFactory.newDocumentNoAttachment()).document.let {
			updateMainAttachment(it.id, it.rev, randomSmallAttachment(), emptyList())
		}
		val updated = updateDocument(doc.copy(attachmentId = null)).document
		updated.mainAttachment shouldBe null
		ensureDeleted(doc.document, null)
	}

	"Changing a document attachment id in a bulk update should trigger deletion of the attachment" {
		val docs = (1..10).map { i ->
			createDocument(dataFactory.newDocumentNoAttachment(i)).document.let {
				updateMainAttachment(it.id, it.rev, randomSmallAttachment(), emptyList())
			}
		}
		val deleting = docs.take(5)
		val updating = docs.drop(5)
		val updatedById = bulkModify((deleting.map { it.copy(attachmentId = null) } + updating.map { it.changeNonAttachmentInfo() }).shuffled()).toList().associateBy { it.id }
		deleting.forEach { original ->
			updatedById[original.id].shouldNotBeNull().document.mainAttachment shouldBe null
			ensureDeleted(original.document, null)
		}
		updating.forEach { original ->
			updatedById[original.id].shouldNotBeNull().document.mainAttachment.shouldNotBeNull()
		}
	}
}
