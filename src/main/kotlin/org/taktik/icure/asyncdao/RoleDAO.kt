package org.taktik.icure.asyncdao

import org.taktik.icure.entities.Role
import java.net.URI

interface RoleDAO : GenericDAO<Role> {
    suspend fun getByName(dbInstanceUrl: URI, groupId: String, name: String): Role?
}