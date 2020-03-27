package org.taktik.icure.entities;

import org.junit.Test;
import org.taktik.icure.services.external.rest.handlers.GsonMessageBodyHandler;
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto;
import org.taktik.icure.services.external.rest.v1.transformationhandlers.V1MapperFactory;

import static org.junit.Assert.*;

public class PropertyTest {
    @Test
    public void map() {
        PropertyDto result = new V1MapperFactory(new GsonMessageBodyHandler().getGson()).getMapper().map(new Property(new PropertyType(), 0), PropertyDto.class);
        assertNotNull(result);
    }
}
