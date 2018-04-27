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

package org.taktik.icure.be.ehealth.logic.efact;

import be.ehealth.technicalconnector.exception.ConnectorException;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.logic.efact.impl.EfactInvoiceResponse;
import org.taktik.icure.be.ehealth.logic.efact.impl.EfactMessage;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoiceSender;
import org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.InvoicesBatch;
import org.taktik.icure.be.ehealth.logic.efact.impl.SentMessageBatch;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.LoginException;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 09:51
 * To change this template use File | Settings | File Templates.
 */
public interface EfactLogic {
	List<EfactMessage> loadPendingMessages(String token) throws ConnectorException, TokenNotAvailableException, LoginException, MissingRequirementsException, CreationException;
	EfactInvoiceResponse sendInvoicesBatch(String token, InvoicesBatch i, long uniqueSendNumber, InvoiceSender sender, String parentMessageId, Long numericalRef, boolean ignorePrescriptionDate) throws ConnectorException, TokenNotAvailableException, EidSessionCreationFailedException, IOException, LoginException, CreationException;
	SentMessageBatch createBatchAndSend(String token, String batchRef, Long numericalRef, HealthcareParty hcp, Insurance insurance, boolean ignorePrescriptionDate, Map<String, List<Invoice>> invoices) throws TokenNotAvailableException, ConnectorException, LoginException, IOException, CreationException;
}
