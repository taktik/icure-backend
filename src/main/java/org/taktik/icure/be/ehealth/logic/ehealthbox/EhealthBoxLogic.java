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

package org.taktik.icure.be.ehealth.logic.ehealthbox;

import java.io.IOException;
import java.util.List;
import javax.security.auth.login.LoginException;

import be.ehealth.businessconnector.ehbox.api.domain.exception.EhboxBusinessConnectorException;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import org.bouncycastle.cms.CMSException;
import org.taktik.icure.be.ehealth.EidSessionCreationFailedException;
import org.taktik.icure.be.ehealth.TokenNotAvailableException;
import org.taktik.icure.be.ehealth.dto.common.BoxInfo;
import org.taktik.icure.be.ehealth.dto.common.DocumentMessage;
import org.taktik.icure.be.ehealth.dto.common.Message;
import org.taktik.icure.be.ehealth.logic.ehealthbox.impl.BusinessConnectorException;
import org.taktik.icure.be.ehealth.logic.ehealthbox.impl.MessageDeletedException;
import org.taktik.icure.exceptions.CreationException;
import org.taktik.icure.exceptions.MissingRequirementsException;

/**
 * Created by aduchate on 01/06/13, 11:25
 */
public interface EhealthBoxLogic {
    BoxInfo getInfos(String token) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException;
    List<Message> getMessagesList(String token, String box) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException;
    Message getFullMessage(String token, String source, String messageId) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException, MessageDeletedException;
    void sendMessage(String token, DocumentMessage message, int notificationMask) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException, IOException, CMSException, BusinessConnectorException;
    List<org.taktik.icure.entities.Message> loadMessages(String token, String userId, String hcpId, String boxId, Integer limit, List<String> spamFromAddresses) throws TechnicalConnectorException, EidSessionCreationFailedException, TokenNotAvailableException, EhboxBusinessConnectorException, LoginException, CreationException, MissingRequirementsException;
    void moveMessages(String token, List<String> messageIds, String source, String destination) throws TokenNotAvailableException, TechnicalConnectorException, EhboxBusinessConnectorException, BusinessConnectorException;
	void deleteMessages(String token, List<String> messageIds, String source) throws TokenNotAvailableException, ConnectorException, BusinessConnectorException;
}
