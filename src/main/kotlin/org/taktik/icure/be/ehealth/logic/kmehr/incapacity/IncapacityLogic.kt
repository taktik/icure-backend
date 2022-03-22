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

package org.taktik.icure.be.ehealth.logic.kmehr.incapacity

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.taktik.icure.domain.mapping.ImportMapping
import org.taktik.icure.domain.result.ImportResult
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Service
import org.taktik.icure.services.external.api.AsyncDecrypt
import org.taktik.icure.services.external.http.websocket.AsyncProgress
import java.nio.ByteBuffer

interface IncapacityLogic {

    fun createIncapacityExport(
            patient: Patient,
            sfks: List<String>,
            sender: HealthcareParty,
            language: String,
            incapacityId: String,
            decryptor: AsyncDecrypt?,
            progressor: AsyncProgress?
    ): Flow<DataBuffer>
    fun createIncapacityExport(
            patient: Patient,
            sender: HealthcareParty,
            language: String,
            incapacityId: String,
            services: List<Service>,
            serviceAuthors: List<HealthcareParty>?,
            timeZone: String?,
            progressor: AsyncProgress?
    ): Flow<DataBuffer>
}
