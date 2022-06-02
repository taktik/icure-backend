/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security

import java.io.ByteArrayInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Date
import java.util.Hashtable
import java.util.Vector
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Certificate
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.asn1.x509.X509Extension
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.crypto.util.PrivateKeyFactory
import org.bouncycastle.jce.X509Principal
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure
import org.taktik.icure.entities.HealthcareParty

/**
 * Created by emad7105 on 13/04/2015.
 */
object Certificates {
	/**
	 * Creates a Master certificate for ICure.
	 */
	@Throws(Exception::class)
	fun createMasterCertificateV3(publicKey: PublicKey, privateKey: PrivateKey): X509Certificate {
		val issuer = X500Name("C=BE, O=Taktik, OU=ICureCloud, CN=ICureCloud")
		val subject = X500Name("C=BE, O=Taktik, OU=ICureCloud, CN=ICureCloud") // self signed
		val serial = BigInteger.valueOf(CryptoUtils.random.nextLong())
		val notBefore = Date(System.currentTimeMillis() - 10000)
		val notAfter = Date(System.currentTimeMillis() + 24L * 3600 * 1000)
		val spki = SubjectPublicKeyInfo.getInstance(publicKey.encoded)
		val x509v3CertBuilder = X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, spki)
		x509v3CertBuilder.addExtension(X509Extension.basicConstraints, true, BasicConstraints(true)) // icure is CA

		// Create a content signer
		val signatureAlgorithmId = DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA")
		val digestAlgorithmId = DefaultDigestAlgorithmIdentifierFinder().find(signatureAlgorithmId)
		val akp = PrivateKeyFactory.createKey(privateKey.encoded)
		val contentSigner = BcRSAContentSignerBuilder(signatureAlgorithmId, digestAlgorithmId).build(akp)
		val holder = x509v3CertBuilder.build(contentSigner)
		val certificateStructure = holder.toASN1Structure()
		val certificate = convertToJavaCertificate(certificateStructure)
		certificate.verify(publicKey)
		return certificate
	}

	/**
	 * Creates a certificate for a healthcare party.
	 */
	@Throws(Exception::class)
	fun createCertificateV3(
		hcpartyPublicKey: PublicKey,
		hcparty: HealthcareParty,
		hcPartyEmail: String,
		icurePublicKey: PublicKey?,
		icurePrivateKey: PrivateKey
	): X509Certificate {
		//
		// Signers
		//
		val sAttrs = Hashtable<ASN1ObjectIdentifier, String>()
		val sOrder = Vector<ASN1ObjectIdentifier>()
		sAttrs[X509Principal.C] = "BE"
		sAttrs[X509Principal.O] = "Taktik"
		sAttrs[X509Principal.OU] = "ICureCloud"
		sAttrs[X509Principal.EmailAddress] = "ad@taktik.be"
		sOrder.addElement(X509Principal.C)
		sOrder.addElement(X509Principal.O)
		sOrder.addElement(X509Principal.OU)
		sOrder.addElement(X509Principal.EmailAddress)
		val issuerX509Principal = X509Principal(sOrder, sAttrs)
		val issuer = X500Name(issuerX509Principal.name)

		//
		// Subjects
		//
		val attrs = Hashtable<ASN1ObjectIdentifier, String>()
		val order = Vector<ASN1ObjectIdentifier>()
		attrs[X509Principal.C] = "BE"
		attrs[X509Principal.O] = "organization-" + hcparty.companyName
		attrs[X509Principal.L] = "location-" + hcparty.id
		attrs[X509Principal.CN] = "cn-" + hcparty.id
		attrs[X509Principal.EmailAddress] = hcPartyEmail
		order.addElement(X509Principal.C)
		order.addElement(X509Principal.O)
		order.addElement(X509Principal.L)
		order.addElement(X509Principal.CN)
		order.addElement(X509Principal.EmailAddress)
		val subjectX509Principal = X509Principal(order, attrs)
		val subject = X500Name(subjectX509Principal.name)

		//
		// Other attrs
		//
		val serial = BigInteger.valueOf(CryptoUtils.random.nextLong())
		val notBefore = Date(System.currentTimeMillis() - 10000)
		val notAfter = Date(System.currentTimeMillis() + 24L * 3600 * 1000)
		val spki = SubjectPublicKeyInfo.getInstance(hcpartyPublicKey.encoded)
		val x509v3CertBuilder = X509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, spki)
		x509v3CertBuilder.addExtension(Extension.basicConstraints, true, BasicConstraints(false)) // hcparty is not CA
		x509v3CertBuilder.addExtension(
			Extension.subjectKeyIdentifier,
			true,
			SubjectKeyIdentifier(hcpartyPublicKey.encoded)
		)
		x509v3CertBuilder.addExtension(
			Extension.authorityKeyIdentifier,
			true,
			AuthorityKeyIdentifierStructure(icurePublicKey)
		)

		//
		// Create a content signer
		//
		val signatureAlgorithmId = DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA")
		val digestAlgorithmId = DefaultDigestAlgorithmIdentifierFinder().find(signatureAlgorithmId)
		val akp = PrivateKeyFactory.createKey(icurePrivateKey.encoded)
		val contentSigner = BcRSAContentSignerBuilder(signatureAlgorithmId, digestAlgorithmId).build(akp)

		//
		// Build the certificate
		//
		val holder = x509v3CertBuilder.build(contentSigner)
		val certificateStructure = holder.toASN1Structure()
		val certificate = convertToJavaCertificate(certificateStructure)
		certificate.verify(icurePublicKey)
		return certificate
	}

	@Throws(Exception::class)
	fun createMasterCertificateV3(keyPair: KeyPair): X509Certificate {
		return createMasterCertificateV3(keyPair.public, keyPair.private)
	}

	@Throws(Exception::class)
	fun createCertificateV3(
		hcpartyPublicKey: PublicKey,
		hcparty: HealthcareParty,
		hcpartyEmail: String,
		icureKeyPair: KeyPair
	): X509Certificate {
		return createCertificateV3(hcpartyPublicKey, hcparty, hcpartyEmail, icureKeyPair.public, icureKeyPair.private)
	}

	@Throws(CertificateException::class, IOException::class)
	private fun convertToJavaCertificate(certificate: Certificate): X509Certificate {
		ByteArrayInputStream(certificate.encoded).use { `is` ->
			return CertificateFactory.getInstance("X.509").generateCertificate(`is`) as X509Certificate
		}
	}
}
