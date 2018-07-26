package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.ElementsoinsEpi;

import java.util.List;

public class ElementsoinsDao {


    public ElementsoinsDao() {
    }

    public List<ElementsoinsEpi> getElementsoinsList() {
        RestTemplate restTemplate = new RestTemplate();
        List<ElementsoinsEpi> lstElementsoinsEpi = restTemplate.getForObject("http://localhost:8080/elementsoins", List.class);

        return lstElementsoinsEpi;
    }
}
