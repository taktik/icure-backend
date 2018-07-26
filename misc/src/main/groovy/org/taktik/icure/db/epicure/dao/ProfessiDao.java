package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.ProfessiEpi;

import java.util.List;

public class ProfessiDao {

    public ProfessiDao() {
    }

    public List<ProfessiEpi> getProfessiList() {

        RestTemplate restTemplate = new RestTemplate();
        List<ProfessiEpi> lstProfessiEpi = restTemplate.getForObject("http://localhost:8080/professi", List.class);

        return lstProfessiEpi;
    }
}
