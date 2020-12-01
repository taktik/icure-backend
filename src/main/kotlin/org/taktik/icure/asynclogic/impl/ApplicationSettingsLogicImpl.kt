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

package org.taktik.icure.asynclogic.impl

import org.springframework.stereotype.Service
import org.taktik.icure.asyncdao.ApplicationSettingsDAO
import org.taktik.icure.asynclogic.ApplicationSettingsLogic
import org.taktik.icure.asynclogic.AsyncSessionLogic
import org.taktik.icure.entities.ApplicationSettings

@Service
class ApplicationSettingsLogicImpl(private val applicationSettingsDAO: ApplicationSettingsDAO, sessionLogic: AsyncSessionLogic) : GenericLogicImpl<ApplicationSettings, ApplicationSettingsDAO>(sessionLogic), ApplicationSettingsLogic {
    override fun getGenericDAO(): ApplicationSettingsDAO {
        return applicationSettingsDAO
    }
}
