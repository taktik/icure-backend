package org.taktik.icure.logic;

import org.taktik.icure.entities.MedicalLocation;
import org.taktik.icure.exceptions.DeletionException;

import java.util.List;

public interface MedicalLocationLogic extends EntityPersister<MedicalLocation, String> {

    MedicalLocation createMedicalLocation(MedicalLocation medicalLocation);

    List<String> deleteMedicalLocation(List<String> ids) throws DeletionException;

    MedicalLocation getMedicalLocation(String medicalLocation);

    MedicalLocation modifyMedicalLocation(MedicalLocation medicalLocation);

    List<MedicalLocation> findByPostCode(String postCode);
}
