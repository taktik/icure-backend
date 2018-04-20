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

package org.taktik.icure.be.ehealth.logic.messages;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections4.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 11/11/13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class ErrorWarningMessages {
	public static List<AbstractMessage> parse(InputStream is, String context, Predicate<AbstractMessage> p) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IllegalStateException(e);
		}

		ArrayList<AbstractMessage> result = new ArrayList<>();

		NodeList errors = doc.getElementsByTagName("error");
		for (int i = 0; i < errors.getLength(); i++) {
			Node e = errors.item(i);
			if (e.getNodeType() == Node.ELEMENT_NODE) {
				NodeList nl = ((Element) e).getElementsByTagName("context");
				String ctx = nl.getLength()>0 ? nl.item(0).getTextContent() : null;

				if (context == null || context.equals(ctx)) {
					ErrorMessage msg = new ErrorMessage();
					fillMessage((Element) e, ctx, msg);

					if (p==null || p.evaluate(msg)) { result.add(msg); }
				}
			}
		}

		NodeList warnings = doc.getElementsByTagName("warning");
		for (int i = 0; i < warnings.getLength(); i++) {
			Node e = errors.item(i);
			if (e.getNodeType() == Node.ELEMENT_NODE) {
				NodeList nl = ((Element) e).getElementsByTagName("context");
				String ctx = nl.getLength()>0 ? nl.item(0).getTextContent() : null;
				if (context == null || context.equals(ctx)) {
					WarningMessage msg = new WarningMessage();
					fillMessage((Element) e, ctx, msg);

					if (p==null || p.evaluate(msg)) { result.add(msg); }
				}
			}
		}

		return result;
	}

	private static void fillMessage(Element e, String ctx, AbstractMessage msg) {
		NodeList nl;
		nl = e.getElementsByTagName("subcontext");
		String sctx = nl.getLength()>0 ? nl.item(0).getTextContent() : null;
		nl = e.getElementsByTagName("code");
		String code = nl.getLength() > 0 ? nl.item(0).getTextContent() : null;
		nl = e.getElementsByTagName("zone");
		String zone = nl.getLength() > 0 ? nl.item(0).getTextContent() : null;
		nl = e.getElementsByTagName("pattern");
		String pattern = nl.getLength() > 0 ? nl.item(0).getTextContent() : null;
		nl = e.getElementsByTagName("skip");
		String skip = nl.getLength() > 0 ? nl.item(0).getTextContent() : null;

		nl = e.getElementsByTagName("message");


		msg.setContext(ctx);
		msg.setSubContext(sctx);
		msg.setCode(code);
		msg.setZone(zone);
		msg.setPattern(pattern);
		msg.setSkip(skip != null && skip.equals("true"));

		msg.setMessage(new HashMap<>());

		for (int j = 0; j < nl.getLength(); j++) {
			Node m = nl.item(j);
			if (m.getNodeType() == Node.ELEMENT_NODE) {
				msg.getMessage().put(((Element) m).getAttribute("lang"), m.getTextContent());
			}
		}
	}

	public static List<AbstractMessage> parse(InputStream is, String context) { return ErrorWarningMessages.parse(is, context, null); }

	public static List<AbstractMessage> parse(InputStream is) {
		return ErrorWarningMessages.parse(is, null, null);
	}
}
