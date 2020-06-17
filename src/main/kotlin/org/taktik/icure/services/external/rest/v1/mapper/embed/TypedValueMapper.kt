package org.taktik.icure.services.external.rest.v1.mapper.embed

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.icure.constants.TypedValuesType
import org.taktik.icure.entities.embed.TypedValue
import org.taktik.icure.services.external.rest.v1.dto.embed.TypedValueDto
import org.taktik.icure.services.external.rest.v1.mapper.utils.InstantMapper
import java.util.*

@Mapper(componentModel = "spring", uses = [InstantMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class TypedValueMapper {
    fun map(typedValueDto: TypedValueDto<*>): TypedValue<*> {
        return when(typedValueDto.type) {
            TypedValuesType.STRING -> TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<String>())
            TypedValuesType.DATE -> TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<Date>())
            TypedValuesType.INTEGER ->  TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<Int>())
            TypedValuesType.DOUBLE ->  TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<Double>())
            TypedValuesType.BOOLEAN ->  TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<Boolean>())
            TypedValuesType.CLOB ->  TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<String>())
            TypedValuesType.JSON ->  TypedValue.withTypeAndValue(typedValueDto.type, typedValueDto.getValue<String>())
            null -> TypedValue<String>(
                    booleanValue = typedValueDto.booleanValue,
                    integerValue = typedValueDto.integerValue,
                    doubleValue = typedValueDto.doubleValue,
                    stringValue = typedValueDto.stringValue,
                    dateValue = typedValueDto.dateValue
            )
        }
    }
    fun map(typedValue: TypedValue<*>): TypedValueDto<*> {
        return when(typedValue.type) {
            TypedValuesType.STRING -> TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<String>())
            TypedValuesType.DATE -> TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<Date>())
            TypedValuesType.INTEGER ->  TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<Int>())
            TypedValuesType.DOUBLE ->  TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<Double>())
            TypedValuesType.BOOLEAN ->  TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<Boolean>())
            TypedValuesType.CLOB ->  TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<String>())
            TypedValuesType.JSON ->  TypedValueDto.withTypeAndValue(typedValue.type, typedValue.getValue<String>())
            null -> TypedValueDto<String>(
                    booleanValue = typedValue.booleanValue,
                    integerValue = typedValue.integerValue,
                    doubleValue = typedValue.doubleValue,
                    stringValue = typedValue.stringValue,
                    dateValue = typedValue.dateValue
            )
        }
    }
}
