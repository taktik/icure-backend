package org.taktik.icure.services.external.rest.v2.controllers.core

import java.io.File
import java.util.UUID
import io.kotest.core.spec.style.StringSpec
import io.kotest.spring.SpringListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactive.asPublisher
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToFlow
import org.taktik.icure.asyncdao.DocumentDAO
import org.taktik.icure.asynclogic.objectstorage.DocumentObjectStorageClient
import org.taktik.icure.asynclogic.objectstorage.testutils.htmlUti
import org.taktik.icure.asynclogic.objectstorage.testutils.javascriptUti
import org.taktik.icure.asynclogic.objectstorage.testutils.jsonUti
import org.taktik.icure.asynclogic.objectstorage.testutils.xmlUti
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.services.external.rest.shared.controllers.core.DocumentControllerEndToEndTestContext
import org.taktik.icure.services.external.rest.shared.controllers.core.documentControllerSharedEndToEndTests
import org.taktik.icure.services.external.rest.v2.dto.DocumentDto
import org.taktik.icure.services.external.rest.v2.dto.embed.DocumentTypeDto
import org.taktik.icure.services.external.rest.v2.mapper.DocumentV2Mapper
import org.taktik.icure.test.ICureTestApplication
import org.taktik.icure.testutils.shouldRespondErrorStatus
import reactor.core.publisher.Mono

private const val CONTROLLER_ROOT = "rest/v2/document"
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
	documentMapper: DocumentV2Mapper,
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

		documentControllerSharedEndToEndTests(context, false)

		v2EndToEndTests(context)
	}
}

// Test legacy behaviour to ensure retro-compatibility
private fun StringSpec.v2EndToEndTests(
	context: DocumentControllerEndToEndTestContext<DocumentDto, DocumentController.BulkAttachmentUpdateOptions>
): Unit = with (context) {
	/*
	"Creation of a document with initialized main attachment data should fail with BAD REQUEST" {

	}

	"Update of a document with changes to main attachment data should fail with BAD REQUEST" {

	}
	
	 */

	"Update of a non-existing document should fail with NOT FOUND" {
		shouldRespondErrorStatus(HttpStatus.NOT_FOUND) { updateDocument(dataFactory.newDocumentNoAttachment()) }
	}
}
