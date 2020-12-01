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

package org.taktik.icure.services.external.rest.v1.mapper.mikrono

import org.mapstruct.Mapper
import org.taktik.icure.dto.be.mikrono.EmailOrSmsMessage
import org.taktik.icure.services.external.rest.v1.dto.be.mikrono.EmailOrSmsMessageDto

@Mapper(componentModel = "spring")
interface EmailOrSmsMessageMapper {
    fun map(emailOrSmsMessageDto: EmailOrSmsMessageDto): EmailOrSmsMessage
    fun map(emailOrSmsMessage: EmailOrSmsMessage): EmailOrSmsMessageDto
}
