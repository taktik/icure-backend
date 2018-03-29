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

package org.taktik.icure.be.ehealth.logic.chapter4

import org.taktik.icure.be.drugs.civics.AddedDocumentPreview
import org.taktik.icure.be.drugs.civics.ParagraphPreview
import org.taktik.icure.be.ehealth.dto.chapter4.AgreementResponse
import org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.FolderType
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 06/06/13
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */
interface Chapter4Logic {
    fun findParagraphs(searchString: String, language: String): List<ParagraphPreview>
    fun getAddedDocuments(chapterName: String, paragraphName: String): List<AddedDocumentPreview>
    fun findParagraphsWithCnk(cnk: Long?, language: String): List<ParagraphPreview>
    fun requestAgreement(token: String, patient: Patient, requestType: RequestType, civicsVersion: String, paragraph: String, appendices: List<Appendix>, verses: List<String>?, incomplete: Boolean, start: Long, end: Long?, decisionReference: String?, ioRequestReference: String?): AgreementResponse
    fun agreementRequestsConsultation(token: String, patient: Patient, civicsVersion: String, paragraph: String?, start: Long, end: Long?, reference: String?): AgreementResponse
    fun cancelAgreement(token: String, patient: Patient, decisionReference: String?, iorequestReference: String?): AgreementResponse
    fun closeAgreement(token: String, patient: Patient, decisionReference: String): AgreementResponse
    fun getDemandKmehrMessage(sender: HealthcareParty, patient: Patient, requestType: RequestType, commonInput: String, civicsVersion: String, incomplete: Boolean?, start: Long?, end: Long?, verses: List<String>?, appendices: List<Appendix>?, reference: String?, decisionReference: String?, ioRequestReference: String?, paragraph: String?): org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
    fun getConsultationTransaction(sender: HealthcareParty, patient: Patient, commonInput: String, start: Long?, end: Long?, civicsVersion: String, paragraph: String?, reference: String?): org.taktik.icure.be.ehealth.dto.kmehr.v20121001.be.fgov.ehealth.standards.kmehr.schema.v1.Kmehrmessage
    fun getCancelTransaction(sender: HealthcareParty, patient: Patient, decisionReference: String?, ioRequestReference: String?, date : Date? = null): FolderType
    fun getCloseTransaction(sender: HealthcareParty, patient: Patient, decisionReference: String, date: Date? = null): FolderType
}
