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
package org.taktik.icure.asynclogic.impl

import org.springframework.stereotype.Service
import org.taktik.icure.applications.utils.JarUtils
import org.taktik.icure.constants.PropertyTypes
import org.taktik.icure.asyncdao.GenericDAO
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.asynclogic.GroupLogic
import org.taktik.icure.asynclogic.ICureLogic
import org.taktik.icure.logic.PropertyLogic

@Service
class ICureLogicImpl(private val iCureDAO: ICureDAO,
                     private val groupLogic: GroupLogic,
                     private val propertyLogic: PropertyLogic,
                     private val allDaos: List<GenericDAO<*>>) : ICureLogic {

    override fun getIndexingStatus(groupId: String): Map<String, Number>? {
        return iCureDAO.getIndexingStatus(groupId)
    }

    override suspend fun updateDesignDoc(groupId: String, daoEntityName: String) {
        val group = groupLogic.findGroup(groupId) ?: throw IllegalArgumentException("Cannot load group $groupId")
        allDaos
                .firstOrNull { dao: GenericDAO<*> -> dao.javaClass.simpleName.startsWith(daoEntityName + "DAO") }
                //?.let { dao: GenericDAO<*> -> dao.forceInitStandardDesignDocument(group) } // TODO SH AD: missing function from GenericLogic
    }

    override fun getVersion(): String {
        val manifest = JarUtils.getManifest()
        return if (manifest != null) {
            val version = manifest.mainAttributes.getValue("Build-revision")
            version?.trim { it <= ' ' } ?: ""
        } else {
            propertyLogic.getSystemPropertyValue<Any>(PropertyTypes.System.VERSION.identifier).toString().trim { it <= ' ' }
        }
    }
}
