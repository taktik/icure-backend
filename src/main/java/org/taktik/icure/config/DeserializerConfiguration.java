package org.taktik.icure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.CalendarItemTypeDAO;
import org.taktik.icure.entities.serializer.CalendarItemTypeDeserializer;

import javax.annotation.PostConstruct;

@Configuration
public class DeserializerConfiguration {

    @Autowired
    private CalendarItemTypeDAO calendarItemTypeDAO;

    @PostConstruct
    public void initializeDeserializer(){
        CalendarItemTypeDeserializer.initialize(calendarItemTypeDAO);
    }

}
