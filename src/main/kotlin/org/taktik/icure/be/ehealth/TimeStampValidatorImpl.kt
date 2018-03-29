/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth

import be.ehealth.technicalconnector.validator.impl.TimeStampValidatorImpl
import be.ehealth.technicalconnector.exception.TechnicalConnectorException
import be.ehealth.technicalconnector.exception.InvalidTimeStampException
import org.bouncycastle.tsp.TimeStampToken
import org.slf4j.LoggerFactory
import sun.security.x509.X500Name


class TimeStampValidatorImpl : TimeStampValidatorImpl() {
	val log = LoggerFactory.getLogger(this.javaClass)

	@Throws(InvalidTimeStampException::class, TechnicalConnectorException::class)
	override fun validateTimeStampToken(tsToken: TimeStampToken) {
		val timeStampInfo = tsToken.timeStampInfo
		if (timeStampInfo != null) {
			log.info("Validating TimeStampToken with SerialNumber [" + timeStampInfo.serialNumber + "]")
			if (timeStampInfo.tsa != null) {
				val name = timeStampInfo.tsa.name
				log.info("Validating Timestamp against TrustStore Looking for [$name].")
			}
		}
		super.validateTimeStampToken(tsToken)
	}
}
