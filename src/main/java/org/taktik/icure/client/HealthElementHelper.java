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
import org.taktik.icure.exceptions.EncryptionException;
import org.taktik.icure.services.external.rest.v1.dto.HealthElementDto;
import org.taktik.icure.services.external.rest.v1.dto.PatientDto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Type;
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
public class HealthElementHelper {
    private static final Logger log = LoggerFactory.getLogger(HealthElementHelper.class);

    private ICureHelper client;

    public HealthElementHelper(ICureHelper iCureHelper) {
        this.client = iCureHelper;
    }

    public List<HealthElementDto> list() {
        //TODO
        return null;
    }

    public HealthElementDto create(HealthElementDto healthElement, String patientId, String ownerHealthcarePartyId) throws IOException, ExecutionException, EncryptionException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        // fetching the patient
        PatientDto fetchedPatient = client.getPatientHelper().get(patientId);
        return create(healthElement, fetchedPatient, ownerHealthcarePartyId);
    }

	/**
	 *  Creation of a healthElement with Initial delegations, cryptedForeignKeys and secretForeignKeys to owner.
	 *
	 * @param healthElement
	 * @param patient
	 * @param ownerHealthcarePartyId
	 * @return
	 * @throws java.io.IOException
	 * @throws java.util.concurrent.ExecutionException
	 * @throws org.taktik.icure.exceptions.EncryptionException
	 * @throws javax.crypto.BadPaddingException
	 * @throws java.security.NoSuchAlgorithmException
	 * @throws javax.crypto.NoSuchPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws java.security.NoSuchProviderException
	 * @throws java.security.InvalidKeyException
	 */
    public HealthElementDto create(HealthElementDto healthElement, PatientDto patient, String ownerHealthcarePartyId) throws IOException, ExecutionException, EncryptionException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
        String responseCreatedHProblem = client.doRestPOST("helement", healthElement);
        HealthElementDto createdHProblem = client.getGson().fromJson(responseCreatedHProblem, HealthElementDto.class);

        if (createdHProblem == null || createdHProblem.getId() == null || patient == null || patient.getId() == null) {
            log.error("Health problem creation failed, illegal arguments.");
            return null;
        }

        // create a initial delegation
        return client.initObjectDelegations(createdHProblem, patient, ownerHealthcarePartyId, "helement/{id}/delegate", "helement/modify");
    }

    public HealthElementDto get(String id) throws IOException {
        String response = client.doRestGET("helement/" + id);
        return client.getGson().fromJson(response, HealthElementDto.class);
    }

	/**
	 *
	 * @param healthcarePartyId, will be used as delegate HcParty.
	 * @param patient
	 *
	 * @return List of health problems belong to this healthcare party and the patient
	 * @throws java.io.IOException
	 */
	public List<HealthElementDto> findBy(String healthcarePartyId, PatientDto patient) throws IOException, EncryptionException {
		/* keys which are located in the patient with healthcarePartyId as
		 delegates. These keys are normally encrypted by owner-delegate
		 AES exchange key. But, here we need a list of unencrypted ones. (plain) */
		List<String> secretForeignKeys = client.getHealthcarePartyHelper().getSecretForeignKeys(patient, healthcarePartyId);
		// covert to a String list delimited by comma
		String secretForeignKeysDelimitedByComma = Joiner.on(",").join(secretForeignKeys);

		String response = client.doRestGET("helement/find?hcPartyId=" + healthcarePartyId + "&secretFKeys=" + secretForeignKeysDelimitedByComma);
		// The way to obtain the class type of Health Problem List fot gson conversion
		Type hProblemListType = new TypeToken<ArrayList<HealthElementDto>>() {
		}.getType();
		return client.getGson().fromJson(response, hProblemListType);
	}

	/**
	 *
	 * @param healthcarePartyId, will be used as delegate HcParty.
	 * @param patientId
	 *
	 * @return List of health problems belong to this healthcare party and the patient
	 * @throws java.io.IOException
	 */
	public List<HealthElementDto> findBy(String healthcarePartyId, String patientId) throws IOException, EncryptionException {
		PatientDto patient = client.getPatientHelper().get(patientId);

		return findBy(healthcarePartyId, patient);
	}

    public HealthElementDto modify(HealthElementDto healthElement) throws IOException {
        String response = client.doRestPOST("helement/modify", healthElement);
        return client.getGson().fromJson(response, HealthElementDto.class);
    }

    public Set<String> delete(Set<String> ids) throws IOException {
        String response = client.doRestPOST("helement/delete", ids);
        return client.getGson().<Set<String>>fromJson(response, Set.class);
    }

//    public HealthElementDto newDelegation(String healthElementId, String patientId, String ownerId, String delegateId) throws IOException, ExecutionException, EncryptionException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
//        // fetch the patient
//        PatientDto p = client.getPatientHelper().get(patientId);
//
//        // fetch the healthElement
//        HealthElementDto c = client.getHealthElementHelper().get(healthElementId);
//
//        return newDelegation(c, p, ownerId, delegateId);
//    }
//
//    public HealthElementDto newDelegation(HealthElementDto healthElement, String patientId, String ownerId, String delegateId) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
//        // fetch the patient
//        PatientDto p = client.getPatientHelper().get(patientId);
//
//        return newDelegation(healthElement, p, ownerId, delegateId);
//    }
//
//    public HealthElementDto newDelegation(String healthElementId, PatientDto patient, String ownerId, String delegateId) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
//        // fetch the healthElement
//        HealthElementDto c = client.getHealthElementHelper().get(healthElementId);
//
//        return newDelegation(c, patient, ownerId, delegateId);
//    }
//
//    public HealthElementDto newDelegation(HealthElementDto healthElement, PatientDto patient, String ownerId, String delegateId) throws EncryptionException, ExecutionException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeyException {
//        if (healthElement == null || patient == null || healthElement.getId() == null || patient.getId() == null) {
//            log.error("New delegation failed.");
//            return null;
//        }
//
//		// Fetching the SKD (i.e. Secret Key Document, the key in Delegation which is encrypted by
//		// exchange key of owner and delegate) of the owner to pass in appendObjectDelegations method
//		// It needs the SKD in order to decrypt and obtain the previously generated key for creation of new
//		// delegation.
//		List<DelegationDto> ownerDelegations = healthElement.getDelegations().get(ownerId);
//		String ownerCryptedDelegationSkd = null;
//		if (ownerDelegations.size() > 0) {
//			ownerCryptedDelegationSkd = ownerDelegations.get(0).getKey();
//		}
//
//		return client.appendObjectDelegations(healthElement, patient, ownerId, delegateId, ownerCryptedDelegationSkd, "healthElement/delegate/", "healthElement/modify");
//    }
}
