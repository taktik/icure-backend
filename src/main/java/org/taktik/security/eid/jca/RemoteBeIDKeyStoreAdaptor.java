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

package org.taktik.security.eid.jca;

import java.security.KeyStore;
import java.security.KeyStoreException;

import be.ehealth.technicalconnector.config.ConfigFactory;
import be.ehealth.technicalconnector.config.ConfigValidator;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.sts.security.KeyStoreAdaptor;

public class RemoteBeIDKeyStoreAdaptor implements KeyStoreAdaptor {
	private KeyStore keyStore;
	private static ConfigValidator conf = ConfigFactory.getConfigValidator();

	private void init() throws KeyStoreException, TechnicalConnectorException {
		this.keyStore = KeyStore.getInstance("RemoteBeID");

		RemoteBeIDKeyStoreParameter keyStoreParameter = new RemoteBeIDKeyStoreParameter();
		keyStoreParameter.setAutoRecovery(conf.getBooleanProperty("be.ehealth.technicalconnector.service.sts.security.impl.beid.autorecovery", true));
		keyStoreParameter.setLogoff(conf.getBooleanProperty("be.ehealth.technicalconnector.service.sts.security.impl.beid.logoff", false));
		keyStoreParameter.setCardReaderStickiness(conf.getBooleanProperty("be.ehealth.technicalconnector.service.sts.security.impl.beid.cardreaderstickiness", false));

		try {
			this.keyStore.load(keyStoreParameter);
		} catch (Exception e) {
			throw new KeyStoreException(e);
		}
	}

	public KeyStore getKeyStore() throws KeyStoreException, TechnicalConnectorException {
		if (this.keyStore == null) {
			this.init();
		}
		return this.keyStore;
	}


}
