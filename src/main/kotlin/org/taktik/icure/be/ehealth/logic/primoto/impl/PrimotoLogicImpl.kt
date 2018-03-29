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

package org.taktik.icure.be.ehealth.logic.primoto.impl

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.taktik.icure.be.ehealth.logic.primoto.PrimotoLogic
import org.taktik.icure.be.ehealth.logic.primoto.domain.be.fgov.riziv.primoto.request.PrimotoRequest
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.math.BigInteger

import java.util.*
import javax.xml.datatype.DatatypeFactory
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.datatype.DatatypeConstants

class PrimotoLogicImpl : PrimotoLogic {
	override fun generateFile(version : String, serial : String, name : String, nihii : String, year : Long, from : Long, to : Long, outputStream : OutputStream) {
		val pdf = ByteArrayOutputStream()
		try {
			PDDocument().apply {
				val doc = this
				addPage(PDPage().apply {
					PDPageContentStream(doc, this).apply {
						beginText()
						setFont(PDType1Font.HELVETICA_BOLD, 10f)
						moveTextPositionByAmount(50f, 700f)
						"""Aanvraag tot "Financiële tegemoetkoming verleend voor het gebruik van telematica
en het elektronisch beheer van medische dossiers" (KB 6 februari 2003)

Demande d' "Intervention financière pour l'utilisation de la télématique et
pour la gestion électronique des dossiers médicaux" (AR 6 février 2003)

Par la présente, le Docteur ${name} ( numéro Inami ${nihii}) déclare sur l'honneur
avoir utilisé effectivement au cours de l'année civile ${year} le logiciel
cité ci-après pour la gestion du dossier médical global de ses patients.

Par la présente, la firme Taktik sa confirme que le médecin sus-mentionné est effectivement
en possession du logiciel iCure, version ${version}, numéro de série ${serial}.""".split("\n").forEach {
							drawString(it); moveTextPositionByAmount(0f, -13f)
						}
						endText()
						close()
					}
				})
				doc.save(pdf)
				doc.close()
			}
		} catch (ignored:Exception) {
		}

		val dtf = DatatypeFactory.newInstance()

		val xml = PrimotoRequest().apply {
			healthcareProfessional = nihii.replace("[^0-9]".toRegex(), "").substring(0,8)
			yearConcerned = BigInteger.valueOf(year)
			creationDate = dtf.newXMLGregorianCalendar(GregorianCalendar().apply { time = Date() }).apply { timezone = DatatypeConstants.FIELD_UNDEFINED }
			softwareName = "iCure"
			softwareVersion = version
			softwareSerialNumber = serial
			softwareVendorDeclaration = pdf.toByteArray()
		}

		val jaxbContext = JAXBContext.newInstance(PrimotoRequest::class.java)
		val jaxbMarshaller = jaxbContext.createMarshaller()
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

		jaxbMarshaller.marshal(xml, outputStream)
	}
}
