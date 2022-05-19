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

package org.taktik.icure.asyncdao.impl

import java.io.IOException
import java.nio.ByteBuffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.taktik.couchdb.annotation.View
import org.taktik.couchdb.entity.ComplexKey
import org.taktik.couchdb.id.IDGenerator
import org.taktik.couchdb.id.UUIDGenerator
import org.taktik.couchdb.queryViewIncludeDocsNoValue
import org.taktik.icure.asyncdao.FormTemplateDAO
import org.taktik.icure.entities.FormTemplate
import org.taktik.icure.properties.CouchDbProperties
import org.taktik.icure.spring.asynccache.AsyncCacheManager
import org.taktik.icure.utils.writeTo

/**
 * Created by aduchate on 02/02/13, 15:24
 */

@Repository("formTemplateDAO")
@View(name = "all", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted) emit(null, doc._id)}")
internal class FormTemplateDAOImpl(
	couchDbProperties: CouchDbProperties,
	private val uuidGenerator: UUIDGenerator,
	@Qualifier("baseCouchDbDispatcher") couchDbDispatcher: CouchDbDispatcher,
	idGenerator: IDGenerator,
	@Qualifier("asyncCacheManager") asyncCacheManager: AsyncCacheManager
) : GenericDAOImpl<FormTemplate>(couchDbProperties, FormTemplate::class.java, couchDbDispatcher, idGenerator), FormTemplateDAO {

	@View(name = "by_userId_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted && doc.author) emit([doc.author,doc.guid], null )}")
	override fun listFormTemplatesByUserGuid(userId: String, guid: String?, loadLayout: Boolean): Flow<FormTemplate> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val from = ComplexKey.of(userId, guid ?: "")
		val to = ComplexKey.of(userId, guid ?: "\ufff0")
		val formTemplates = client.queryViewIncludeDocsNoValue<Array<String>, FormTemplate>(createQuery(client, "by_userId_and_guid").startKey(from).endKey(to).includeDocs(true)).map { it.doc }

		// invoke postLoad()
		emitAll(
			if (loadLayout) {
				formTemplates.map {
					this@FormTemplateDAOImpl.postLoad(it)
				}
			} else formTemplates
		)
	}

	@View(name = "by_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted) emit(doc.guid, null )}")
	override fun listFormsByGuid(guid: String, loadLayout: Boolean): Flow<FormTemplate> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val formTemplates = client.queryViewIncludeDocsNoValue<String, FormTemplate>(createQuery(client, "by_guid").key(guid).includeDocs(true)).map { it.doc }

		emitAll(
			if (loadLayout) {
				formTemplates.map {
					this@FormTemplateDAOImpl.postLoad(it)
				}
			} else formTemplates
		)
	}

	@View(name = "by_specialty_code_and_guid", map = "function(doc) { if (doc.java_type == 'org.taktik.icure.entities.FormTemplate' && !doc.deleted && doc.specialty) emit([doc.specialty.code,doc.guid], null )}")
	override fun listFormsBySpecialtyAndGuid(specialityCode: String, guid: String?, loadLayout: Boolean): Flow<FormTemplate> = flow {
		val client = couchDbDispatcher.getClient(dbInstanceUrl)

		val formTemplates = if (guid != null) {
			val key = ComplexKey.of(specialityCode, guid)
			client.queryViewIncludeDocsNoValue<Array<String>, FormTemplate>(createQuery(client, "by_specialty_code_and_guid").key(key).includeDocs(true)).map { it.doc }
		} else {
			val from = ComplexKey.of(specialityCode, null)
			val to = ComplexKey.of(specialityCode, ComplexKey.emptyObject())
			client.queryViewIncludeDocsNoValue<Array<String>, FormTemplate>(createQuery(client, "by_specialty_code_and_guid").startKey(from).endKey(to).includeDocs(true)).map { it.doc }
		}

		emitAll(
			if (loadLayout) {
				formTemplates.map {
					this@FormTemplateDAOImpl.postLoad(it)
				}
			} else formTemplates
		)
	}

	override suspend fun createFormTemplate(entity: FormTemplate): FormTemplate {
		super.save(true, entity)
		return entity
	}

	override suspend fun beforeSave(entity: FormTemplate) =
		super.beforeSave(entity).let { formTemplate ->
			if (formTemplate.layout != null) {
				val newAttachmentId = DigestUtils.sha256Hex(formTemplate.layout)

				if (newAttachmentId != formTemplate.layoutAttachmentId && formTemplate.rev != null && formTemplate.layoutAttachmentId != null) {
					formTemplate.attachments?.containsKey(formTemplate.layoutAttachmentId)?.takeIf { it }?.let {
						formTemplate.copy(
							rev = deleteAttachment(formTemplate.id, formTemplate.rev, formTemplate.layoutAttachmentId),
							attachments = formTemplate.attachments - formTemplate.layoutAttachmentId,
							layoutAttachmentId = newAttachmentId,
							isAttachmentDirty = true
						)
					} ?: formTemplate.copy(
						layoutAttachmentId = newAttachmentId,
						isAttachmentDirty = true
					)
				} else
					formTemplate
			} else {
				if (formTemplate.layoutAttachmentId != null && formTemplate.rev != null) {
					formTemplate.copy(
						rev = deleteAttachment(formTemplate.id, formTemplate.rev, formTemplate.layoutAttachmentId),
						layoutAttachmentId = null,
						isAttachmentDirty = false
					)
				} else formTemplate
			}
		}

	override suspend fun afterSave(entity: FormTemplate) =
		super.afterSave(entity).let { formTemplate ->
			if (formTemplate.isAttachmentDirty && formTemplate.layoutAttachmentId != null && formTemplate.rev != null && formTemplate.layout != null) {
				createAttachment(formTemplate.id, formTemplate.layoutAttachmentId, formTemplate.rev, "application/json", flowOf(ByteBuffer.wrap(formTemplate.layout))).let {
					formTemplate.copy(
						rev = it,
						isAttachmentDirty = false
					)
				}
			} else formTemplate
		}

	override suspend fun postLoad(entity: FormTemplate) =
		super.postLoad(entity).let { formTemplate ->
			val formTemplateLayout = formTemplate.templateLayoutAttachmentId?.let {laId ->
				val attachmentFlow = getAttachment(formTemplate.id, laId, formTemplate.rev)
				ByteArrayOutputStream().use {
					attachmentFlow.writeTo(it)
					it.toByteArray()
				}
			}

			val formLayout = formTemplate.layoutAttachmentId?.takeIf { formTemplateLayout == null }?.let {laId ->
				val attachmentFlow = getAttachment(formTemplate.id, laId, formTemplate.rev)
				ByteArrayOutputStream().use {
					attachmentFlow.writeTo(it)
					it.toByteArray()
				}
			}

			if (formTemplateLayout != null || formLayout != null) {
				try {
					formTemplate.copy(templateLayout = formTemplateLayout, layout = formLayout)
				} catch (e: IOException) {
					formTemplate //Could not load
				}
			} else formTemplate
		}

	companion object {
		val log: Logger = LoggerFactory.getLogger(FormTemplateDAOImpl::class.java)
	}
}
