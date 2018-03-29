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

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.taktik.icure.logic.EidLogic;
import org.taktik.icure.logic.SessionLogic;
import org.taktik.icure.logic.UserLogic;

/**
 * The JCA security provider. Provides an eID based {@link KeyStore},
 * {@link Signature}, {@link KeyManagerFactory}, and {@link SecureRandom}.
 * <p/>
 * Usage:
 * 
 * <pre>
 * import java.security.Security;
 * import be.fedict.commons.eid.jca.BeIDProvider;
 * 
 * ...
 * Security.addProvider(new BeIDProvider());
 * </pre>
 * 
 * @see be.fedict.commons.eid.jca.BeIDKeyStore
 * @see RemoteBeIDSignature
 * @see be.fedict.commons.eid.jca.BeIDKeyManagerFactory
 * @author Frank Cornelis
 * 
 */
public class RemoteBeIDProvider extends Provider {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "BeIDProvider";

	private static final Log LOG = LogFactory.getLog(RemoteBeIDProvider.class);


	private static RemoteBeIDProvider remoteBeIDProvider = null;

	private EidLogic eidLogic;

	public static RemoteBeIDProvider getInstance() {
		return remoteBeIDProvider;
	}

	public RemoteBeIDProvider(EidLogic eidLogic) {
		super(NAME, 1.0, "Remote BeID Provider");
		this.eidLogic = eidLogic;

		if (remoteBeIDProvider!=null) {
			throw new IllegalStateException("Duplicate initialization of remoteBeIDProvider");
		}


		putService(new RemoteBeIDService(this, "KeyStore", "RemoteBeID", RemoteBeIDKeyStore.class.getName()));

		final Map<String, String> signatureServiceAttributes = new HashMap<String, String>();
		signatureServiceAttributes.put("SupportedKeyClasses",
				RemoteBeIDPrivateKey.class.getName());
		putService(new RemoteBeIDService(this, "Signature", "SHA1withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "SHA256withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "SHA384withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "SHA512withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "NONEwithRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "RIPEMD128withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "RIPEMD160withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "RIPEMD256withRSA",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "SHA1withRSAandMGF1",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));
		putService(new RemoteBeIDService(this, "Signature", "SHA256withRSAandMGF1",
				RemoteBeIDSignature.class.getName(), signatureServiceAttributes));

		putService(new RemoteBeIDService(this, "KeyManagerFactory", "BeID",
				RemoteBeIDKeyManagerFactory.class.getName()));

		remoteBeIDProvider = this;
	}

	/**
	 * Inner class used by {@link RemoteBeIDProvider}.
	 * 
	 * @author Frank Cornelis
	 * 
	 */
	private final class RemoteBeIDService extends Service {

		public RemoteBeIDService(final Provider provider, final String type,
								 final String algorithm, final String className) {
			super(provider, type, algorithm, className, null, null);
		}

		public RemoteBeIDService(final Provider provider, final String type,
								 final String algorithm, final String className,
								 final Map<String, String> attributes) {
			super(provider, type, algorithm, className, null, attributes);
		}

		@Override
		public Object newInstance(final Object constructorParameter)
				throws NoSuchAlgorithmException {
			LOG.debug("newInstance: " + super.getType());
			if (super.getType().equals("Signature")) {
				return new RemoteBeIDSignature(this.getAlgorithm());
			} else if (super.getType().equals("KeyStore")) {
				return new RemoteBeIDKeyStore(eidLogic);
			}
			return super.newInstance(constructorParameter);
		}

		@Override
		public boolean supportsParameter(final Object parameter) {
			LOG.debug("supportedParameter: " + parameter);
			return super.supportsParameter(parameter);
		}
	}
}
