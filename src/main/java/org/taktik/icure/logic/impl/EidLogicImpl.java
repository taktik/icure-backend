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

package org.taktik.icure.logic.impl;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import be.fedict.commons.eid.client.FileType;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.logic.EidLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;

public class EidLogicImpl implements EidLogic {
	private SessionLogic sessionLogic;
	private CertificateFactory certificateFactory;

	public EidLogicImpl() {
		try {
			certificateFactory = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			throw new IllegalStateException(e);
		}
	}

	ConcurrentHashMap<String,ConcurrentLinkedQueue<AsyncResponseWithRef>> longPollsMap = new ConcurrentHashMap<>();
	ConcurrentHashMap<String,BlockingQueue<Object>> pendingTokens = new ConcurrentHashMap<>();

	@Override
	public void cancelAsyncPoll(String userId, AsyncResponseWithRef asyncResponse) {
		if (longPollsMap.containsKey(userId)) {
			longPollsMap.get(userId).remove(asyncResponse);
			asyncResponse.getAsyncResponse().cancel();
		}
	}

	@Override
	public void registerAsyncPoll(String userId, String conId, AsyncResponseWithRef asyncResponse) {
		if (!longPollsMap.containsKey(userId)) {
			longPollsMap.put(userId, new ConcurrentLinkedQueue<>(Collections.singleton(asyncResponse)));
		} else {
			longPollsMap.get(userId).add(asyncResponse);
		}
	}

	@Override
	public void pushResult(String token, Object result) throws InterruptedException {
		if (pendingTokens.containsKey(token)) {
			pendingTokens.get(token).put(result);
		}
	}

	@Override
	public byte[] sign(String userId, byte[] digestValue, String digestAlgo, FileType certificateFileType, boolean requireSecureReader) throws InterruptedException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		String action = "sign";
		String param = digestAlgo + ":" + Base64.encodeBase64String(digestValue);

		byte[] signature = remoteExecute(userId, action, param);
		return signature;
	}

	private byte[] remoteExecute(String userId, String action, String param) throws InterruptedException {
		byte[] result = null;
		if (longPollsMap.containsKey(userId)) {
			for (AsyncResponseWithRef ar : longPollsMap.get(userId)) {
				if (!ar.getAsyncResponse().isCancelled() && !ar.getAsyncResponse().isDone()) {
					String token = UUID.randomUUID().toString();

					LinkedBlockingQueue queue = new LinkedBlockingQueue();
					pendingTokens.put(token, queue);

					new Thread(() -> {
						//Just push back the command
						ar.getAsyncResponse().resume(action + ":" + token + (param != null ?(":" + param):""));
					}).run();

					result = (byte[])queue.take();
					return result;
				}
			}
		}
		return result;
	}


	@Override
	public void logoff(String userId) {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		//Do nothing
	}

	@Override
	public X509Certificate[] getSigningCertificateChain(String userId)  throws InterruptedException, CertificateException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		return new X509Certificate[] {getSigningCertificate(userId), getCitizenCaCertificate(userId), getRootCaCertificate(userId)};
	}

	@Override
	public X509Certificate[] getAuthenticationCertificateChain(String userId) throws InterruptedException, CertificateException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		return new X509Certificate[] {getAuthenticationCertificate(userId), getCitizenCaCertificate(userId), getRootCaCertificate(userId)};
	}

	@Override
	public X509Certificate getSigningCertificate(String userId) throws InterruptedException, CertificateException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		String action = "cert";
		String param = "Signature";

		byte[] buf = remoteExecute(userId, action, param);
		if (buf==null) { return null; }

		return (X509Certificate) this.certificateFactory.generateCertificate(new ByteArrayInputStream(buf));
	}

	@Override
	public X509Certificate getAuthenticationCertificate(String userId) throws InterruptedException, CertificateException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		String action = "cert";
		String param = "Authentication";

		byte[] buf = remoteExecute(userId, action, param);
		if (buf==null) { return null; }

		return (X509Certificate) this.certificateFactory.generateCertificate(new ByteArrayInputStream(buf));
	}

	@Override
	public X509Certificate getCitizenCaCertificate(String userId) throws InterruptedException, CertificateException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		String action = "cert";
		String param = "CA";

		byte[] buf = remoteExecute(userId, action, param);
		if (buf==null) { return null; }

		return (X509Certificate) this.certificateFactory.generateCertificate(new ByteArrayInputStream(buf));
	}

	@Override
	public X509Certificate getRootCaCertificate(String userId) throws InterruptedException, CertificateException {
		if (userId==null) {
			userId = sessionLogic.getCurrentSessionContext().getUser().getId();
		}

		String action = "cert";
		String param = "Root";

		byte[] buf = remoteExecute(userId, action, param);
		if (buf==null) { return null; }

		return (X509Certificate) this.certificateFactory.generateCertificate(new ByteArrayInputStream(buf));
	}

	@Autowired
	public void setSessionLogic(SessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

}
