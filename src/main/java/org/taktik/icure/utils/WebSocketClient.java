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

package org.taktik.icure.utils;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

public class WebSocketClient implements WebSocketListener {
	Session session;

	public static void main(String[] args)
	{
		URI uri = URI.create("ws://127.0.0.1:16043/ws/be_kmehr/generateSumehr");

		org.eclipse.jetty.websocket.client.WebSocketClient client = new org.eclipse.jetty.websocket.client.WebSocketClient();
		try
		{
			try
			{
				client.start();
				// The socket that receives events
				WebSocketClient socket = new WebSocketClient();
				// Attempt Connect
				ClientUpgradeRequest request = new ClientUpgradeRequest();
				request.setSubProtocols("xsCrossfire");
				String basicAuthHeader = org.apache.commons.codec.binary.Base64.encodeBase64String("abaudoux:lambda".getBytes("UTF8"));
				request.setHeader("Authorization", "Basic " + basicAuthHeader);

				Future<Session> fut = client.connect(socket,uri,request);
				// Wait for Connect
				Session session = fut.get();
				// Send a message

				System.out.println(">>> Sending message: Hello");
				session.getRemote().sendString("{\"parameters\":{\"language\":\"fr\",\"info\":{\"comment\":\"\",\"recipient\":{\"name\":\"Abrumet\",\"nihii\":\"1990000728\",\"specialityCodes\":[{\"type\":\"CD-HCPARTY\",\"code\":\"hub\"}]},\"secretForeignKeys\":[\"44eccc69-11d5-4c89-accc-6911d5dc8942\"]},\"patientId\":\"29e6fafd-05be-4f2b-a6fa-fd05beef2bbc\"}}");
				Thread.sleep(1000);
				session.getRemote().sendString("{\"parameters\":{\"language\":\"fr\",\"info\":{\"comment\":\"\",\"recipient\":{\"name\":\"Abrumet\",\"nihii\":\"1990000728\",\"specialityCodes\":[{\"type\":\"CD-HCPARTY\",\"code\":\"hub\"}]},\"secretForeignKeys\":[\"44eccc69-11d5-4c89-accc-6911d5dc8942\"]},\"patientId\":\"29e6fafd-05be-4f2b-a6fa-fd05beef2bbc\"}}");
				Thread.sleep(1000);
				session.getRemote().sendString("{\"parameters\":{\"language\":\"fr\",\"info\":{\"comment\":\"\",\"recipient\":{\"name\":\"Abrumet\",\"nihii\":\"1990000728\",\"specialityCodes\":[{\"type\":\"CD-HCPARTY\",\"code\":\"hub\"}]},\"secretForeignKeys\":[\"44eccc69-11d5-4c89-accc-6911d5dc8942\"]},\"patientId\":\"29e6fafd-05be-4f2b-a6fa-fd05beef2bbc\"}}");
				Thread.sleep(1000);
				session.getRemote().sendString("{\"parameters\":{\"language\":\"fr\",\"info\":{\"comment\":\"\",\"recipient\":{\"name\":\"Abrumet\",\"nihii\":\"1990000728\",\"specialityCodes\":[{\"type\":\"CD-HCPARTY\",\"code\":\"hub\"}]},\"secretForeignKeys\":[\"44eccc69-11d5-4c89-accc-6911d5dc8942\"]},\"patientId\":\"29e6fafd-05be-4f2b-a6fa-fd05beef2bbc\"}}");
				Thread.sleep(1000);
				session.getRemote().sendString("{\"parameters\":{\"language\":\"fr\",\"info\":{\"comment\":\"\",\"recipient\":{\"name\":\"Abrumet\",\"nihii\":\"1990000728\",\"specialityCodes\":[{\"type\":\"CD-HCPARTY\",\"code\":\"hub\"}]},\"secretForeignKeys\":[\"44eccc69-11d5-4c89-accc-6911d5dc8942\"]},\"patientId\":\"29e6fafd-05be-4f2b-a6fa-fd05beef2bbc\"}}");
				Thread.sleep(1000);
				session.getRemote().sendString("{\"parameters\":{\"language\":\"fr\",\"info\":{\"comment\":\"\",\"recipient\":{\"name\":\"Abrumet\",\"nihii\":\"1990000728\",\"specialityCodes\":[{\"type\":\"CD-HCPARTY\",\"code\":\"hub\"}]},\"secretForeignKeys\":[\"44eccc69-11d5-4c89-accc-6911d5dc8942\"]},\"patientId\":\"29e6fafd-05be-4f2b-a6fa-fd05beef2bbc\"}}");
				Thread.sleep(1000);

				Thread.sleep(10000);

				session.close();

			}
			finally
			{
				client.stop();
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace(System.err);
		}
	}

	@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {

	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
	}

	@Override
	public void onWebSocketConnect(Session session) {
		this.session = session;
	}

	@Override
	public void onWebSocketError(Throwable cause) {

	}

	@Override
	public void onWebSocketText(String message) {
		System.out.println("<<< Received message: " + message);
		try {
			session.getRemote().sendString(message.replaceFirst("decrypt","decryptResponse"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
