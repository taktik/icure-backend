package org.taktik.icure.entities.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.taktik.icure.dao.CalendarItemTypeDAO;
import org.taktik.icure.entities.CalendarItemType;
import org.taktik.icure.entities.serializer.annotations.ConfigurableSerializer;

import java.io.IOException;

@ConfigurableSerializer(using = CalendarItemTypeDAO.class)
public class CalendarItemTypeDeserializer extends JsonDeserializer<CalendarItemType> {


    private static CalendarItemTypeDAO calendarItemTypeDAO;

    public static void initialize(CalendarItemTypeDAO _calendarItemTypeDAO){
        calendarItemTypeDAO = _calendarItemTypeDAO;
    }

    @Override
    public CalendarItemType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String id = p.getValueAsString();
        return calendarItemTypeDAO.get(id);
    }
}
