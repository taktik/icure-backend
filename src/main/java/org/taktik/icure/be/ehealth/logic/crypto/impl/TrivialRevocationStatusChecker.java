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

package org.taktik.icure.be.ehealth.logic.crypto.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import be.ehealth.technicalconnector.service.etee.RevocationStatusChecker;
import org.joda.time.DateTime;

public class TrivialRevocationStatusChecker implements RevocationStatusChecker {

	@Override
	public boolean isRevoked(X509Certificate x509Certificate) throws CertificateException {
		return false;
	}

	@Override
	public boolean isRevoked(X509Certificate x509Certificate, DateTime dateTime) throws CertificateException {
		return false;
	}

}
