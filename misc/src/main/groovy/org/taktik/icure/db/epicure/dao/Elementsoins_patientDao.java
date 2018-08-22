package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.Elementsoins_patientEpi;

import java.util.List;

public class Elementsoins_patientDao {

    public Elementsoins_patientDao() {
    }

    public List<Elementsoins_patientEpi> getElementsoins_patientList(String sFichepat) {

        RestTemplate restTemplate = new RestTemplate();
        List<Elementsoins_patientEpi> lstElementsoins_patient = restTemplate.getForObject("http://localhost:8080/elementsoins_patient?fichepat="+sFichepat, List.class);

        return lstElementsoins_patient;
    }
}
