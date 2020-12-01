package org.taktik.icure.asynclogic

import kotlinx.coroutines.flow.Flow
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncdao.ClassificationDAO
import org.taktik.icure.entities.Classification
import org.taktik.icure.entities.embed.Delegation

interface ClassificationLogic : EntityPersister<Classification, String> {
    fun getGenericDAO(): ClassificationDAO

    suspend fun createClassification(classification: Classification): Classification?

    suspend fun getClassification(classificationId: String): Classification?
    fun findByHCPartySecretPatientKeys(hcPartyId: String, secretPatientKeys: List<String>): Flow<Classification>
    fun deleteClassifications(ids: Set<String>): Flow<DocIdentifier>

    suspend fun modifyClassification(classification: Classification): Classification

    suspend fun addDelegation(classificationId: String, healthcarePartyId: String, delegation: Delegation): Classification?

    suspend fun addDelegations(classificationId: String, delegations: List<Delegation>): Classification?
    fun getClassificationByIds(ids: List<String>): Flow<Classification>
}
