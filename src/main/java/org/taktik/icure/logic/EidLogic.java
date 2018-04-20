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

package org.taktik.icure.logic;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.ws.rs.container.AsyncResponse;

import be.fedict.commons.eid.client.FileType;
import org.taktik.icure.logic.impl.EidLogicImpl;

public interface EidLogic {
	void registerAsyncPoll(String userId, String conId, EidLogicImpl.AsyncResponseWithRef asyncResponse);
	void cancelAsyncPoll(String userId, EidLogicImpl.AsyncResponseWithRef asyncResponse);

	void pushResult(String token, Object result) throws InterruptedException;

	byte[] sign(String userId, byte[] digestValue, String digestAlgo, FileType certificateFileType, boolean requireSecureReader) throws InterruptedException;

	void logoff(String userId);

	X509Certificate[] getSigningCertificateChain(String userId)  throws InterruptedException, CertificateException;

	X509Certificate[] getAuthenticationCertificateChain(String userId)  throws InterruptedException, CertificateException;

	X509Certificate getSigningCertificate(String userId) throws InterruptedException, CertificateException;

	X509Certificate getAuthenticationCertificate(String userId) throws InterruptedException, CertificateException;

	X509Certificate getCitizenCaCertificate(String userId) throws InterruptedException, CertificateException;

	X509Certificate getRootCaCertificate(String userId) throws InterruptedException, CertificateException;

	public static class AsyncResponseWithRef {
		private final String conId;
		private final AsyncResponse asyncResponse;

		public AsyncResponseWithRef(String conId, AsyncResponse asyncResponse) {
			this.conId = conId;
			this.asyncResponse = asyncResponse;
		}

		public String getConId() {
			return conId;
		}

		public AsyncResponse getAsyncResponse() {
			return asyncResponse;
		}
	}
}
