package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.icure.asyncdao.ClassificationTemplateDAO
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.embed.Delegation
import java.util.*

interface ClassificationTemplateLogic : EntityPersister<ClassificationTemplate, String> {
    fun getGenericDAO(): ClassificationTemplateDAO

    suspend fun createClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate?

    suspend fun getClassificationTemplate(classificationTemplateId: String): ClassificationTemplate?
    fun deleteClassificationTemplates(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyClassificationTemplate(classificationTemplate: ClassificationTemplate): ClassificationTemplate

    suspend fun addDelegation(classificationTemplateId: String, healthcarePartyId: String, delegation: Delegation): ClassificationTemplate?

    suspend fun addDelegations(classificationTemplateId: String, delegations: List<Delegation>): ClassificationTemplate?
    fun getClassificationTemplateByIds(ids: List<String>): Flow<ClassificationTemplate>
    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: ArrayList<String>): Flow<ClassificationTemplate>

    fun listClassificationTemplates(paginationOffset: PaginationOffset<String>): Flow<ViewQueryResultEvent>
}
