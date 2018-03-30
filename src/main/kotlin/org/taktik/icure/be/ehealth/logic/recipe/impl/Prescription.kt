/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.logic.recipe.impl

import java.io.Serializable
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 18/06/13
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
open class Prescription(
        var creationDate: Date,
        var encryptionKeyId: String,
        var rid: String,
        var isFeedbackAllowed: Boolean = false,
        var patientId: Long? = null,
        var notificationWasSent: Boolean? = null) : Serializable {}
