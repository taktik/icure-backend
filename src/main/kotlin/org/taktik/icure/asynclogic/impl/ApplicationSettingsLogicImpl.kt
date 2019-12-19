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
