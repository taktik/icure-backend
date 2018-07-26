package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.AdresseEpi;

import java.util.List;

public class AdresseDao {

    public AdresseDao() {
    }

    public List<AdresseEpi> getAdresseList(String sFichecontact) {

        RestTemplate restTemplate = new RestTemplate();
        List<AdresseEpi> lstAdresseEpi = restTemplate.getForObject("http://localhost:8080/adresse?fichecontact="+sFichecontact, List.class);

        return lstAdresseEpi;
    }
}
