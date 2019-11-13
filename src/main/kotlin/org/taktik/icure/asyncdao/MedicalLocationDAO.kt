package org.taktik.icure.asyncdao

import kotlinx.coroutines.flow.Flow
import org.taktik.icure.dao.GenericDAO
import org.taktik.icure.entities.MedicalLocation
import java.net.URI

interface MedicalLocationDAO {
    fun byPostCode(dbInstanceUrl: URI, groupId: String, postCode: String): Flow<MedicalLocation>
}
