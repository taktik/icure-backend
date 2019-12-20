package org.taktik.icure.dao;

import org.taktik.icure.entities.MedicalLocation;

import java.util.List;

public interface MedicalLocationDAO extends GenericDAO<MedicalLocation> {
    List<MedicalLocation> byPostCode(String postCode);
}
