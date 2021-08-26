package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

interface StructureElement

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

class MeasureField(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`measure-field`, shortLabel, null, null, null, null, tags, codifications, options)

class NumberField(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`number-field`, shortLabel, null, null, null, null, tags, codifications, options)

class DatePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`date-picker`, shortLabel, null, null, null, null, tags, codifications, options)

class TimePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`time-picker`, shortLabel, null, null, null, null, tags, codifications, options)

class DateTimePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`date-time-picker`, shortLabel, null, null, null, null, tags, codifications, options)

class MultipleChoice(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`multiple-choice`, shortLabel, null, null, null, null, tags, codifications, options)

