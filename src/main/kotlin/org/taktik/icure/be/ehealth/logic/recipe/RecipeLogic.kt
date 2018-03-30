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

package org.taktik.icure.be.ehealth.logic.recipe

import be.ehealth.technicalconnector.exception.ConnectorException
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException
import org.taktik.icure.be.ehealth.TokenNotAvailableException
import org.taktik.icure.be.ehealth.logic.recipe.impl.Feedback
import org.taktik.icure.be.ehealth.logic.recipe.impl.Prescription
import org.taktik.icure.be.ehealth.logic.recipe.impl.PrescriptionFullWithFeedback
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.embed.Medication
import java.security.KeyStoreException
import java.security.cert.CertificateExpiredException
import java.util.*
import java.util.zip.DataFormatException
import javax.xml.bind.JAXBException

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 16/06/13
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
interface RecipeLogic {
    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class)
    fun createPrescription(token: String?, patient: Patient, hcp: HealthcareParty, feedback: Boolean, medications: List<Medication>, prescriptionType: String?, notification: String?, executorId: String?, deliveryDate: Date?, expirationDate: Date?): Prescription

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    fun listOpenPrescriptions(token: String): List<Prescription>

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, DataFormatException::class, KeyStoreException::class, CertificateExpiredException::class)
    fun sendNotification(token: String, patientId: String, executorId: String, rid: String, text: String)

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    fun revokePrescription(token: String, rid: String, reason: String)

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    fun updateFeedbackFlag(token: String, rid: String, feedbackFlag: Boolean)

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, DataFormatException::class, KeyStoreException::class, CertificateExpiredException::class)
    fun listFeedbacks(token: String): List<Feedback>

    @Throws(ConnectorException::class, EidSessionCreationFailedException::class, TokenNotAvailableException::class, KeyStoreException::class, CertificateExpiredException::class)
    fun listOpenPrescriptions(token: String, patientId: String): List<Prescription>

    @Throws(JAXBException::class)
    fun getPrescription(rid: String): PrescriptionFullWithFeedback?
}