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

package org.taktik.icure.security;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.taktik.icure.entities.HealthcareParty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by emad7105 on 13/04/2015.
 */
public class Certificates {

	/**
	 * Creates a Master certificate for ICure.
	 */
	public static X509Certificate createMasterCertificateV3(PublicKey publicKey, PrivateKey privateKey) throws Exception {
		X500Name 	issuer = new X500Name("C=BE, O=Taktik, OU=ICureCloud, CN=ICureCloud");
		X500Name 	subject = new X500Name("C=BE, O=Taktik, OU=ICureCloud, CN=ICureCloud"); // self signed
		BigInteger 	serial = BigInteger.valueOf(RSAKeysUtils.random.nextLong());
		Date 		notBefore = new Date(System.currentTimeMillis() - 10000);
		Date		notAfter = new Date(System.currentTimeMillis() + 24L * 3600 * 1000);
		
		SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		
		X509v3CertificateBuilder x509v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, spki);
		x509v3CertBuilder.addExtension(X509Extension.basicConstraints, true, new BasicConstraints(true)); // icure is CA

		// Create a content signer
		AlgorithmIdentifier signatureAlgorithmId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA");
		AlgorithmIdentifier digestAlgorithmId = new DefaultDigestAlgorithmIdentifierFinder().find(signatureAlgorithmId);
		AsymmetricKeyParameter akp = PrivateKeyFactory.createKey(privateKey.getEncoded());
		ContentSigner contentSigner =  new BcRSAContentSignerBuilder(signatureAlgorithmId, digestAlgorithmId).build(akp);

		X509CertificateHolder holder = x509v3CertBuilder.build(contentSigner);
		Certificate certificateStructure = holder.toASN1Structure();
		X509Certificate certificate = convertToJavaCertificate(certificateStructure);
		
		certificate.verify(publicKey);

		return certificate;
	}

	/**
	 * Creates a certificate for a healthcare party.
	 */
	public static X509Certificate createCertificateV3(PublicKey hcpartyPublicKey, HealthcareParty hcparty, String hcPartyEmail, PublicKey icurePublicKey, PrivateKey icurePrivateKey) throws Exception {
		//
		// Signers
		//
		Hashtable<org.bouncycastle.asn1.ASN1ObjectIdentifier, String> sAttrs = new Hashtable<>();
		Vector<org.bouncycastle.asn1.ASN1ObjectIdentifier> sOrder = new Vector<>();

		sAttrs.put(X509Principal.C, "BE");
		sAttrs.put(X509Principal.O, "Taktik");
		sAttrs.put(X509Principal.OU, "ICureCloud");
		sAttrs.put(X509Principal.EmailAddress, "ad@taktik.be");
		sOrder.addElement(X509Principal.C);
		sOrder.addElement(X509Principal.O);
		sOrder.addElement(X509Principal.OU);
		sOrder.addElement(X509Principal.EmailAddress);

		X509Principal issuerX509Principal = new X509Principal(sOrder, sAttrs);
		X500Name issuer = new X500Name(issuerX509Principal.getName());

		//
		// Subjects
		//
		Hashtable<org.bouncycastle.asn1.ASN1ObjectIdentifier, String> attrs = new Hashtable<>();
		Vector<org.bouncycastle.asn1.ASN1ObjectIdentifier> order = new Vector<>();

		attrs.put(X509Principal.C, "BE");
		attrs.put(X509Principal.O, "organization-" + hcparty.getCompanyName());
		attrs.put(X509Principal.L, "location-" + hcparty.getId());
		attrs.put(X509Principal.CN, "cn-" + hcparty.getId());
		attrs.put(X509Principal.EmailAddress, hcPartyEmail);
		order.addElement(X509Principal.C);
		order.addElement(X509Principal.O);
		order.addElement(X509Principal.L);
		order.addElement(X509Principal.CN);
		order.addElement(X509Principal.EmailAddress);

		X509Principal subjectX509Principal = new X509Principal(order, attrs);
		X500Name subject = new X500Name(subjectX509Principal.getName());

		//
		// Other attrs
		//
		BigInteger 	serial = BigInteger.valueOf(RSAKeysUtils.random.nextLong());
		Date 		notBefore = new Date(System.currentTimeMillis() - 10000);
		Date		notAfter = new Date(System.currentTimeMillis() + 24L * 3600 * 1000);
		SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(hcpartyPublicKey.getEncoded());
		

		X509v3CertificateBuilder x509v3CertBuilder = new X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, spki);
		x509v3CertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false)); // hcparty is not CA
		x509v3CertBuilder.addExtension(Extension.subjectKeyIdentifier, true, new SubjectKeyIdentifier(hcpartyPublicKey.getEncoded()));
		x509v3CertBuilder.addExtension(Extension.authorityKeyIdentifier, true, new AuthorityKeyIdentifierStructure(icurePublicKey));

		//
		// Create a content signer
		//
		AlgorithmIdentifier signatureAlgorithmId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA");
		AlgorithmIdentifier digestAlgorithmId = new DefaultDigestAlgorithmIdentifierFinder().find(signatureAlgorithmId);
		AsymmetricKeyParameter akp = PrivateKeyFactory.createKey(icurePrivateKey.getEncoded());
		ContentSigner contentSigner =  new BcRSAContentSignerBuilder(signatureAlgorithmId, digestAlgorithmId).build(akp);

		//
		// Build the certificate
		//
		X509CertificateHolder holder = x509v3CertBuilder.build(contentSigner);
		Certificate certificateStructure = holder.toASN1Structure();
		X509Certificate certificate = convertToJavaCertificate(certificateStructure);
		
		certificate.verify(icurePublicKey);

		return certificate;
	}
	
	public static X509Certificate createMasterCertificateV3(KeyPair keyPair) throws Exception {
		return createMasterCertificateV3(keyPair.getPublic(), keyPair.getPrivate());
	}

	public static X509Certificate createCertificateV3(PublicKey hcpartyPublicKey, HealthcareParty hcparty, String hcpartyEmail, KeyPair icureKeyPair) throws Exception {
		return createCertificateV3(hcpartyPublicKey, hcparty, hcpartyEmail, icureKeyPair.getPublic(), icureKeyPair.getPrivate());
	}

	private static X509Certificate convertToJavaCertificate(Certificate certificate) throws CertificateException, IOException {
		try (InputStream is = new ByteArrayInputStream(certificate.getEncoded())) {
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
		}
	}
}
