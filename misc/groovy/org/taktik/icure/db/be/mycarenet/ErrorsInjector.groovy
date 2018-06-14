package org.taktik.icure.db.be.mycarenet

import com.google.gson.Gson
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

class ErrorsInjector {
	static void main(String[] args) {
		new ErrorsInjector().injectErrors()
	}

	XPathFactory xPathfactory = XPathFactory.newInstance()

	void injectErrors() {
		List<Map<String,String>> errors = new Gson().fromJson(this.getClass().getResourceAsStream("/eAttestErrors.json").newReader("UTF8"), List.class)
		def model = this.getClass().getResourceAsStream("/model.eattest.empty.xml").newReader("UTF8").text
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder()
		Document doc = builder.parse(new ByteArrayInputStream(model.getBytes("UTF8")));
		errors.eachWithIndex { it, idx ->
			XPath xpath = xPathfactory.newXPath()
			def xpathExpression = '/'+it.xpath
			XPathExpression expr = xpath.compile(xpathExpression)

			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET)
			if (nl.length == 0) {
				println "No node for: ${xpathExpression}"
			}
			nl.each { Node node ->
				def error = doc.createElement("error")
				error.setAttribute("code", it.code)
				error.setAttribute("uid", it.uid)
				node.appendChild(error)

				def base = "/${nodeDescr(node)}"
				while(node.parentNode) {
					base = "/${nodeDescr(node.parentNode)}${base}"
					node = node.parentNode
				}
				println "${base} ${it.code} ${it.uid}"
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance()
		Transformer transformer = transformerFactory.newTransformer()
		DOMSource source = new DOMSource(doc)
		def writer = new StringWriter()
		StreamResult result = new StreamResult(writer)
		transformer.transform(source,result)

		print(writer.toString())
	}

	String nodeDescr(Node node) {
		def localName = node.nodeName;
		if (localName == "transaction") {
			XPath xpath = xPathfactory.newXPath();
			return "transaction[${xpath.evaluate('cd[@S="CD-TRANSACTION-MYCARENET"]',node)}]";
		}
		if (localName == "item") {
			XPath xpath = xPathfactory.newXPath();
			return "item[${xpath.evaluate('cd[@S="CD-ITEM-MYCARENET" or @S="CD-ITEM"]',node)}]";
		}
		if (localName == "cd") {
			return "cd[${((Element) node).getAttribute('S')?:((Element) node).getAttribute('SL')}]";
		}
		return localName;
	}
}

