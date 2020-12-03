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

package org.taktik.couchdb.dao.impl.idgenerators

import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.taktik.icure.security.CryptoUtils
import java.util.UUID

class UUIDGenerator : IDGenerator {

    @Synchronized
    override fun incrementAndGet(sequenceName: String): Int {
        throw IllegalStateException("Not supported")
    }

    override fun newGUID(): UUID {
        return Generators.randomBasedGenerator(CryptoUtils.getRandom()).generate()
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(UUIDGenerator::class.java)
    }
}
