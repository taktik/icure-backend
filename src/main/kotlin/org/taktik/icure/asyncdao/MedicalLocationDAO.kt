package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.entities.MedicalLocation
import java.net.URI

interface MedicalLocationDAO: GenericDAO<MedicalLocation> {
    fun byPostCode(dbInstanceUrl: URI, groupId: String, postCode: String): Flow<MedicalLocation>
}
