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
 * Copyright (C) 2012-2013 FedICT.
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
import java.util.Locale;

import javax.net.ssl.ManagerFactoryParameters;

import be.fedict.commons.eid.jca.*;

/**
 * eID specific implementation of {@link ManagerFactoryParameters}. Can be used
 * to tweak the behavior of the eID handling in the context of mutual SSL.
 * <p/>
 * Usage:
 * 
 * <pre>
 * import javax.net.ssl.KeyManagerFactory;
 * import javax.net.ssl.SSLContext;
 * ...
 * KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(&quot;BeID&quot;);
 * BeIDManagerFactoryParameters specs = new BeIDManagerFactoryParameters();
 * specs.set...
 * keyMannagerFactory.init(specs);
 * SSLContext sslContext = SSLContext.getInstance(&quot;TLS&quot;);
 * sslContext.init(keyManagerFactory.getKeyManagers(), ..., ...);
 * </pre>
 * 
 * @see be.fedict.commons.eid.jca.BeIDKeyManagerFactory
 * @author Frank Cornelis
 * 
 */
public class RemoteBeIDManagerFactoryParameters implements ManagerFactoryParameters {

	private Component parentComponent;

	private Locale locale;

	private boolean autoRecovery;

	private boolean cardReaderStickiness;

	/**
	 * Default constructor.
	 */
	public RemoteBeIDManagerFactoryParameters() {
		super();
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

	public boolean getAutoRecovery() {
		return this.autoRecovery;
	}

	public boolean getCardReaderStickiness() {
		return this.cardReaderStickiness;
	}

	/**
	 * Sets whether the auto recovery should use card reader stickiness.
	 * If set to true, the auto recovery will try to recover using the same card reader.
	 *
	 * @param cardReaderStickiness
	 */
	public void setCardReaderStickiness(boolean cardReaderStickiness) {
		this.cardReaderStickiness = cardReaderStickiness;
	}
}
