package org.taktik.icure.entities

import org.junit.Assert
import org.junit.Test
import org.mapstruct.factory.Mappers
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.services.external.rest.v1.dto.PropertyDto
import org.taktik.icure.services.external.rest.v1.mapper.PropertyMapper

class PropertyTest {
    @Test
    fun map() {
        val result: PropertyDto = Mappers.getMapper(PropertyMapper::class.java).map(Property(id= "123", type = PropertyType(id = "123"), typedValue = TypedValue.withValue(0)))
        Assert.assertNotNull(result)
    }
}
