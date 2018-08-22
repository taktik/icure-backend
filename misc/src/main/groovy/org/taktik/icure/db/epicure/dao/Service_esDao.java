package org.taktik.icure.db.epicure.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.taktik.icure.db.epicure.entity.Service_esEpi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Service_esDao {

    private String sFichepat;
    private List<Service_esEpi> lstService_esEpi;

    public List<Service_esEpi> getService_esList(String sFichepat, String id_contact) {

        RestTemplate restTemplate = new RestTemplate();

        if (!sFichepat.equals(this.sFichepat)) {
            this.lstService_esEpi = restTemplate.getForObject("http://localhost:8080/service_es?fichepat=" + sFichepat, List.class);
            this.sFichepat = sFichepat;
        }

        List<Service_esEpi> lstService_esEpi = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        Iterator itrService_esEpi = this.lstService_esEpi.iterator();
        while (itrService_esEpi.hasNext()) {
            Object obj = itrService_esEpi.next();
            Service_esEpi service_esEpi = objectMapper.convertValue(obj, Service_esEpi.class);
            if (id_contact.equals(service_esEpi.getId_contact()))
                lstService_esEpi.add(service_esEpi);
        }

        // System.out.println("Service_esDao " + this.lstService_esEpi.size() + " -> " + lstService_esEpi.size());
        return lstService_esEpi;
    }
}
