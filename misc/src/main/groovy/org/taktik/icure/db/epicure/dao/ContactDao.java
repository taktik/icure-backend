package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.ContactEpi;

import java.util.List;

public class ContactDao {

    public ContactDao() {
    }

    public List<ContactEpi> getContactList(String sFichecontact) {

        RestTemplate restTemplate = new RestTemplate();
        List<ContactEpi> lstContact = restTemplate.getForObject("http://localhost:8080/contact?fichecontact="+sFichecontact, List.class);

        return lstContact;
    }
}
