/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.be.format.logic.impl

import kotlinx.coroutines.flow.flowOf
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.taktik.icure.asynclogic.FormLogic
import org.taktik.icure.asynclogic.HealthcarePartyLogic
import org.taktik.icure.be.format.logic.KetLogic
import org.taktik.icure.dto.result.ResultInfo
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.SAXException
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import javax.xml.parsers.ParserConfigurationException
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory

/**
 * Created by aduchate on 20/06/2017.
 */
@Service
class KetLogicImpl(healthcarePartyLogic: HealthcarePartyLogic, formLogic: FormLogic) : GenericResultFormatLogicImpl(healthcarePartyLogic, formLogic), KetLogic {
    @Throws(IOException::class)
    override fun canHandle(doc: Document, enckeys: List<String>): Boolean {
        return try {
            val xml = getXmlDocument(doc!!, enckeys)
            val xPathfactory = XPathFactory.newInstance()
            val xpath = xPathfactory.newXPath()
            val expr = xpath.compile("/Record/Header/LaboFileFormatVersion")
            xml != null && (expr.evaluate(xml, XPathConstants.NODESET) as NodeList).length > 0
        } catch (e: ParserConfigurationException) {
            false
        } catch (e: SAXException) {
            false
        } catch (e: XPathExpressionException) {
            false
        }
    }

    @Throws(IOException::class)
    override fun getInfos(doc: Document, full: Boolean, language: String, enckeys: List<String>): List<ResultInfo> {
        return try {
            val xml = getXmlDocument(doc!!, enckeys)
            val xPathfactory = XPathFactory.newInstance()
            val xpath = xPathfactory.newXPath()
            val expr = xpath.compile("/Record/Body/Patient/Person")
            val nl = expr.evaluate(xml, XPathConstants.NODESET) as NodeList
            getStream(nl).map { n: Node -> getResultInfo(n) }.collect(Collectors.toList())
        } catch (e: ParserConfigurationException) {
            ArrayList()
        } catch (e: SAXException) {
            ArrayList()
        } catch (e: XPathExpressionException) {
            ArrayList()
        }
    }

    private fun getStream(nl: NodeList): Stream<Node> {
        return StreamSupport.stream((Iterable {
            object : Iterator<Node> {
                var i = 0
                override fun hasNext(): Boolean {
                    return i < nl.length
                }

                override fun next(): Node {
                    return nl.item(i++)
                }
            }
        }).spliterator(), false)
    }

    private fun getResultInfo(n: Node): ResultInfo {
        val resultInfo = ResultInfo()
        resultInfo.lastName = getStream(n.childNodes).filter { nd: Node -> (nd as Element).tagName == "LastName" }.findFirst().map { obj: Node -> obj.textContent }.orElse(null)
        resultInfo.firstName = getStream(n.childNodes).filter { nd: Node -> (nd as Element).tagName == "FirstName" }.findFirst().map { obj: Node -> obj.textContent }.orElse(null)
        return resultInfo
    }

    @Throws(IOException::class)
    override suspend fun doImport(language: String, doc: Document, hcpId: String?, protocolIds: List<String>, formIds: List<String>, planOfActionId: String?, ctc: Contact, enckeys: List<String>): Contact? {
        return null
    }

    override fun doExport(sender: HealthcareParty?, recipient: HealthcareParty?, patient: Patient?, date: LocalDateTime?, ref: String?, text: String?) = flowOf<DataBuffer>()
}
