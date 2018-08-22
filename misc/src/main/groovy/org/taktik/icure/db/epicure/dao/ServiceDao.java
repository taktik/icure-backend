package org.taktik.icure.db.epicure.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.ServiceEpi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServiceDao {

    public ServiceDao() {
    }

    private String sFichepat;
    private List<ServiceEpi> lstServiceEpi;

    public List<ServiceEpi> getServiceList(String sFichepat, String fichecontact) {

        RestTemplate restTemplate = new RestTemplate();

        if (!sFichepat.equals(this.sFichepat)) {
            this.lstServiceEpi = restTemplate.getForObject("http://localhost:8080/service?fichepat=" + sFichepat, List.class);
            this.sFichepat = sFichepat;
        }

        List<ServiceEpi> lstServiceEpi = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        Iterator itrServiceEpi = this.lstServiceEpi.iterator();
        while (itrServiceEpi.hasNext()) {
            Object obj = itrServiceEpi.next();
            ServiceEpi serviceEpi = objectMapper.convertValue(obj, ServiceEpi.class);
            if (fichecontact.equals(serviceEpi.getFichecontact()))
                lstServiceEpi.add(serviceEpi);
        }

        // System.out.println("ServiceDao " + this.lstServiceEpi.size() + " -> " + lstServiceEpi.size());
        return lstServiceEpi;
    }
}
