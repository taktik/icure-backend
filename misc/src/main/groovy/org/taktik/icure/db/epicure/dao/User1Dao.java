package org.taktik.icure.db.epicure.dao;

import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.User1Epi;

import java.util.List;

public class User1Dao {


    public User1Dao() {
    }

    public List<User1Epi> getUser1List() {
        RestTemplate restTemplate = new RestTemplate();
        List<User1Epi> lstUser1Epi = restTemplate.getForObject("http://localhost:8080/user1", List.class);

        return lstUser1Epi;
    }
}
