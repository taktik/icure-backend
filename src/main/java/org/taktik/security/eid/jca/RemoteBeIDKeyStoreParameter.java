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

/*
 * Commons eID Project.
 * Copyright (C) 2008-2013 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package org.taktik.security.eid.jca;

import java.awt.Component;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.util.Locale;

import be.fedict.commons.eid.client.spi.Logger;

/**
 * An eID specific {@link KeyStore} parameter. Used to influence how the eID
 * card should be handled. If no {@link RemoteBeIDKeyStoreParameter} is used for
 * loading the keystore, a default behavior will be used.
 * <p/>
 * Usage:
 * <p/>
 * 
 * <pre>
 * import java.security.KeyStore;
 * ...
 * KeyStore keyStore = KeyStore.getInstance("BeID");
 * BeIDKeyStoreParameter keyStoreParameter = new BeIDKeyStoreParameter();
 * keyStoreParameter.set...
 * keyStore.load(keyStoreParameter);
 * </pre>
 * 
 * @author Frank Cornelis
 * @see KeyStore
 * @see be.fedict.commons.eid.jca.BeIDKeyStore
 */
public class RemoteBeIDKeyStoreParameter implements KeyStore.LoadStoreParameter {

	private boolean logoff;

	private Component parentComponent;

	private Locale locale;

	private boolean autoRecovery;

	private boolean cardReaderStickiness;

	private Logger logger;

	@Override
	public ProtectionParameter getProtectionParameter() {
		return null;
	}

	/**
	 * Set to <code>true</code> if you want an eID logoff to be issued after
	 * each PIN entry.
	 * 
	 * @param logoff
	 */
	public void setLogoff(final boolean logoff) {
		this.logoff = logoff;
	}

	public boolean getLogoff() {
		return this.logoff;
	}

	/**
	 * Sets the parent component used to position the default eID dialogs.
	 * 
	 * @param parentComponent
	 */
	public void setParentComponent(final Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	public Component getParentComponent() {
		return this.parentComponent;
	}

	/**
	 * Sets the locale used for the default eID dialogs.
	 * 
	 * @param locale
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public boolean getAutoRecovery() {
		return this.autoRecovery;
	}

	/**
	 * Sets whether the private keys retrieved from the key store should feature
	 * auto-recovery. This means that they can survive eID card
	 * removal/re-insert events.
	 * 
	 * @param autoRecovery
	 */
	public void setAutoRecovery(boolean autoRecovery) {
		this.autoRecovery = autoRecovery;
	}

	public boolean getCardReaderStickiness() {
		return this.cardReaderStickiness;
	}

	/**
	 * Sets whether the auto recovery should use card reader stickiness. If set
	 * to true, the auto recovery will try to recover using the same card
	 * reader.
	 * 
	 * @param cardReaderStickiness
	 */
	public void setCardReaderStickiness(boolean cardReaderStickiness) {
		this.cardReaderStickiness = cardReaderStickiness;
	}

	/**
	 * Sets the logger to be used within the BeIDCard sub-system.
	 * 
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Logger getLogger() {
		return this.logger;
	}
}
