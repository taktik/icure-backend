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

package org.taktik.icure.client;

import com.google.common.base.Joiner;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.dto.filter.Filters;
import org.taktik.icure.exceptions.EncryptionException;
import org.taktik.icure.exceptions.ICureException;
import org.taktik.icure.services.external.rest.v1.dto.ContactDto;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto;
import org.taktik.icure.services.external.rest.v1.dto.embed.ServiceDto;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by emad7105 on 27/06/2014.
 */
public class ContactHelper {
    private static final Logger log = LoggerFactory.getLogger(ContactHelper.class);

    private ICureHelper client;

    public ContactHelper(ICureHelper iCureHelper) {
        this.client = iCureHelper;
    }

    public List<ContactDto> list() {
        //TODO
        return null;
    }

    public ContactDto create(ContactDto contact, String patientId, String ownerHealthcarePartyId) throws IOException, ExecutionException, EncryptionException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        // fetching the patient
        PatientDto fetchedPatient = client.getPatientHelper().get(patientId);
        return create(contact, fetchedPatient, ownerHealthcarePartyId);
    }

	/**
	 *  Creation of a contact with Initial delegations, cryptedForeignKeys and secretForeignKeys to owner.
	 *
	 * @param contact
	 * @param patient
	 * @param ownerHealthcarePartyId
	 * @return
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws EncryptionException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 */
    public ContactDto create(ContactDto contact, PatientDto patient, String ownerHealthcarePartyId) throws IOException, ExecutionException, EncryptionException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        String responseCreatedContact = client.doRestPOST("contact", contact);
        ContactDto createdContact = client.getGson().fromJson(responseCreatedContact, ContactDto.class);

        if (createdContact == null || createdContact.getId() == null || patient == null || patient.getId() == null) {
            log.error("Contact creation failed, illegal arguments.");
            return null;
        }

        // create a initial delegation
        return client.initObjectDelegations(createdContact, patient, ownerHealthcarePartyId, "contact/delegate/{id}", "contact");
    }

	/**
	 *
	 * @param id contact id
	 * @return
	 * @throws IOException
	 */
    public ContactDto get(String id) throws IOException {
        String response = client.doRestGET("contact/" + id);
        return client.getGson().fromJson(response, ContactDto.class);
    }

    public org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ServiceDto> findServicesBy(ArrayList startKeyList, String startKeyDocId, String limit, Filter filter) throws IOException {
        String startKeys = null;
        if (startKeyList != null) {
            startKeys = Joiner.on(",").join(startKeyList);
        }

        String postMethod = "contact/service/filter";
        if (startKeyList != null) { postMethod +=  (postMethod.equals("contact/service/filter")?"?":"&") + "startKey=" + URLEncoder.encode(startKeys, "UTF-8"); }
        if (startKeyDocId != null) { postMethod += (postMethod.equals("contact/service/filter")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
        if (limit != null) { postMethod += (postMethod.equals("contact/service/filter")?"?":"&") + "limit=" + URLEncoder.encode(limit, "UTF-8"); }

        String response = client.doRestPOST(postMethod, filter);

        Type serviceListType = new TypeToken<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ServiceDto>>() {}.getType();
        return client.getGson().fromJson(response, serviceListType);
    }

    /**
     *
     * @param healthcarePartyId, will be used as delegate HcParty.
     * @param patient
     *
     * @return List of contacts belong to this healthcare party and the patient
     * @throws IOException
     */
    public List<ContactDto> findBy(String healthcarePartyId, PatientDto patient) throws IOException, EncryptionException {
		/* keys which are located in the patient with healthcarePartyId as
		 delegates. These keys are normally encrypted by owner-delegate
		 AES exchange key. But, here we need a list of  unencrypted ones. (plain) */
        List<String> secretForeignKeys = client.getHealthcarePartyHelper().getSecretForeignKeys(patient, healthcarePartyId);
        // covert to a String list delimited by comma
        String secretForeignKeysDelimitedByComma = Joiner.on(",").join(secretForeignKeys);

        String response = client.doRestGET("contact/find?hcPartyId=" + healthcarePartyId + "&secretFKeys=" + secretForeignKeysDelimitedByComma);
        // The way to obtain the class type of Contact List fot gson conversion
        Type contactListType = new TypeToken<ArrayList<ContactDto>>() {
        }.getType();
        return client.getGson().fromJson(response, contactListType);
    }

    /**
	 *
	 * @param healthcarePartyId, will be used as delegate HcParty.
	 * @param patientId
	 *
	 * @return List of contacts belong to this healthcare party and the patient
	 * @throws IOException
	 */
	public List<ContactDto> findBy(String healthcarePartyId, String patientId) throws IOException, EncryptionException {
		PatientDto patient = client.getPatientHelper().get(patientId);

		return findBy(healthcarePartyId, patient);
	}

    public ContactDto modify(ContactDto contact) throws IOException {
        String response = client.doRestPOST("contact/modify", contact);
        return client.getGson().fromJson(response, ContactDto.class);
    }

    public Set<String> delete(Set<String> ids) throws IOException {
        String response = client.doRestPOST("contact/delete", ids);
        return client.getGson().<Set<String>>fromJson(response, Set.class);
    }

    public ContactDto newDelegation(String contactId, String patientId, String ownerId, String delegateId) throws IOException, ExecutionException, ICureException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        // fetch the patient
        PatientDto p = client.getPatientHelper().get(patientId);

        // fetch the contact
        ContactDto c = client.getContactHelper().get(contactId);

        return newDelegation(c, p, ownerId, delegateId);
    }

    public ContactDto newDelegation(ContactDto contact, String patientId, String ownerId, String delegateId) throws ICureException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        // fetch the patient
        PatientDto p = client.getPatientHelper().get(patientId);

        return newDelegation(contact, p, ownerId, delegateId);
    }

    public ContactDto newDelegation(String contactId, PatientDto patient, String ownerId, String delegateId) throws ICureException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        // fetch the contact
        ContactDto c = client.getContactHelper().get(contactId);

        return newDelegation(c, patient, ownerId, delegateId);
    }

    public ContactDto newDelegation(ContactDto contact, PatientDto patient, String ownerId, String delegateId) throws ICureException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (contact == null || patient == null || contact.getId() == null || patient.getId() == null) {
            log.error("New delegation failed.");
            return null;
        }

		// Fetching the SKD (i.e. Secret Key Document, the key in Delegation which is encrypted by
		// exchange key of owner and delegate) of the owner to pass in appendObjectDelegations method
		// It needs the SKD in order to decrypt and obtain the previously generated key for creation of new
		// delegation.
		/*List<DelegationDto> ownerDelegations = contact.getDelegations().get(ownerId);
		String ownerCryptedDelegationSkd = null;
		if (ownerDelegations.size() > 0) {
			ownerCryptedDelegationSkd = ownerDelegations.get(0).getKey();
		}*/

		return (ContactDto) client.appendObjectDelegations(contact, patient, ownerId, delegateId, "contact/delegate/", "contact/modify", "patient/delegate/");
    }


	public org.taktik.icure.services.external.rest.v1.dto.PaginatedList filterBy(ArrayList startKeyList, String startKeyDocId, String limit, Filter filter) throws IOException {
		String startKeys = null;
		if (startKeyList != null) {
			startKeys = Joiner.on(",").join(startKeyList);
		}

		String postMethod = "contact/filter";
		if (startKeyList != null) { postMethod +=  (postMethod.equals("contact/filter")?"?":"&") + "startKey=" + URLEncoder.encode(startKeys, "UTF-8"); }
		if (startKeyDocId != null) { postMethod += (postMethod.equals("contact/filter")?"?":"&") + "startKeyDocId=" + URLEncoder.encode(startKeyDocId, "UTF-8"); }
		if (limit != null) { postMethod += (postMethod.equals("contact/filter")?"?":"&") + "limit=" + URLEncoder.encode(limit, "UTF-8"); }

		String response = client.doRestPOST(postMethod, filter);

		Type contactListType = new TypeToken<org.taktik.icure.services.external.rest.v1.dto.PaginatedList<ContactDto>>() {}.getType();
		return client.getGson().fromJson(response, contactListType);
	}
}
