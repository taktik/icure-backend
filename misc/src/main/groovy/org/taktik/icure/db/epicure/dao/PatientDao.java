package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.PatientEpi;

import java.util.List;

public class PatientDao {


    public PatientDao( ) {
    }

    public List<PatientEpi> getPatientList(String sNom, String sPrenom) {

        RestTemplate restTemplate = new RestTemplate();
        List<PatientEpi> lstPatientEpi = restTemplate.getForObject("http://localhost:8080/patient?nom="+sNom+"&prenom="+sPrenom, List.class);

        return lstPatientEpi;
    }
}
