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

package org.taktik.icure.be.format.logic.impl;

import org.jetbrains.annotations.NotNull;
import org.taktik.icure.be.format.logic.KetLogic;
import org.taktik.icure.dto.result.ResultInfo;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by aduchate on 20/06/2017.
 */
@org.springframework.stereotype.Service
public class KetLogicImpl extends GenericResultFormatLogicImpl implements KetLogic {
	@Override
	public boolean canHandle(Document doc) throws IOException {
		try {
			org.w3c.dom.Document xml = getXmlDocument(doc);

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/Record/Header/LaboFileFormatVersion");

			return xml != null && ((NodeList)expr.evaluate(xml, XPathConstants.NODESET)).getLength() > 0;
		} catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
			return false;
		}
	}

	@Override
	public List<ResultInfo> getInfos(Document doc) throws IOException {
		try {
			org.w3c.dom.Document xml = getXmlDocument(doc);

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile("/Record/Body/Patient/Person");

			NodeList nl = (NodeList)expr.evaluate(xml, XPathConstants.NODESET);
			return getStream(nl).map(this::getResultInfo).collect(Collectors.toList());
		} catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
			return new ArrayList<>();
		}
	}

	@NotNull
	private Stream<Node> getStream(NodeList nl) {
		return StreamSupport.stream(((Iterable<Node>) () -> new Iterator<Node>() {
			int i = 0;
			@Override public boolean hasNext() { return i<nl.getLength(); }
			@Override public Node next() { return nl.item(i++); }
		}).spliterator(), false);
	}

	@NotNull
	private ResultInfo getResultInfo(Node n) {
		ResultInfo resultInfo = new ResultInfo();

		resultInfo.setLastName(getStream(n.getChildNodes()).filter(nd -> ((Element) nd).getTagName().equals("LastName")).findFirst().map(Node::getTextContent).orElse(null));
		resultInfo.setFirstName(getStream(n.getChildNodes()).filter(nd -> ((Element) nd).getTagName().equals("FirstName")).findFirst().map(Node::getTextContent).orElse(null));

		return resultInfo;
	}

	@Override
	public Contact doImport(String language, Document doc, String hcpId, List<String> protocolIds, List<String> formIds, String planOfActionId, Contact ctc) throws IOException {
		return null;
	}

	@Override
	public void doExport(HealthcareParty sender, HealthcareParty recipient, Patient patient, LocalDateTime date, String ref, String text, OutputStream output) {

	}
}
