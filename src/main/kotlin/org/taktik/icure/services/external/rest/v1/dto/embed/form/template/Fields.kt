package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.taktik.icure.handlers.JsonDiscriminated
import org.taktik.icure.handlers.JsonPolymorphismRoot

interface StructureElement

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("textfield")
class TextField(
        field: String,
        shortLabel: String? = null,
        rows: Int? = null,
        grows: Boolean? = null,
        schema: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.textfield, shortLabel, rows, null, grows, schema, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("measure-field")
class MeasureField(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`measure-field`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("number-field")
class NumberField(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`number-field`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("date-picker")
class DatePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`date-picker`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("time-picker")
class TimePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`time-picker`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("date-time-picker")
class DateTimePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`date-time-picker`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)
@JsonDeserialize(using = JsonDeserializer.None::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDiscriminated("multiple-choice")
class MultipleChoice(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`multiple-choice`, shortLabel, null, null, null, null, tags, codifications, options)

