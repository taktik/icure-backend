package org.taktik.icure.entities.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.taktik.icure.dao.CalendarItemTypeDAO;
import org.taktik.icure.entities.CalendarItemType;
import org.taktik.icure.entities.base.Identifiable;

import java.io.IOException;

public class IdentifiableSerializer extends JsonSerializer<Identifiable<String>> {

    @Override
    public void serialize(Identifiable<String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(value.getId());
    }
}
