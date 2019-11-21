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

package org.taktik.icure.asyncdao.impl

import com.google.gson.GsonBuilder
import org.ektorp.CouchDbConnector
import org.ektorp.CouchDbInstance
import org.springframework.stereotype.Repository
import org.taktik.icure.asyncdao.ICureDAO
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.util.*

@Repository("iCureDAO")
class ICureDAOImpl(private val couchdbConfig: CouchDbConnector, private val couchdbInstance: CouchDbInstance) : ICureDAO {
    private val gson = GsonBuilder().create()

    override fun getIndexingStatus(groupId: String?): Map<String, Number>? {
        val active_tasks = couchdbConfig.connection.getUncached("/_active_tasks")
        val inputStreamReader: InputStreamReader
        try {
            inputStreamReader = InputStreamReader(active_tasks.content, "UTF8")
            val json = gson.fromJson(inputStreamReader, List::class.java) as List<Map<String, Object>>
            val statusesMap = HashMap<String, MutableList<Number>>()
            for (status in json) {
                val designDoc = status["design_document"] as String?
                val progress = status["progress"] as Number?
                val database = status["database"] as String?

                if (groupId != null && database != null && !database.contains(groupId)) {
                    continue
                }

                if (designDoc != null && progress != null) {
                    val statuses: MutableList<Number>? = statusesMap[designDoc]
                    if (statuses == null) {
                        statusesMap[designDoc] = LinkedList()
                    } else {
                        statuses.add(progress)
                    }
                }
            }

            val results = HashMap<String, Number>()
            for ((key, value) in statusesMap) {
                results[key] = if (value.size == 0) 0 else value.sumBy { it.toInt() } / value.size
            }
            return results

        } catch (e: UnsupportedEncodingException) {
            //
        }

        return null
    }
}
