package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.Contact_itemEpi;

import java.util.List;

public class Contact_itemDao {

    public Contact_itemDao() {
    }

    public List<Contact_itemEpi> getContact_itemList() {

        RestTemplate restTemplate = new RestTemplate();
        List<Contact_itemEpi> lstContact_itemEpi = restTemplate.getForObject("http://localhost:8080/contact_item", List.class);

        return lstContact_itemEpi;
    }
}
