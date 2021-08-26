package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

open class Field(
    val field: String,
    val type: FieldType,
    val shortLabel: String? = null,
    val rows: Int? = null,
    val columns: Int? = null,
    val grows: Boolean? = null,
    val schema: String? = null,
    val tags: List<String>? = null,
    val codifications: List<String>? = null,
    val options: Map<String, *>? = null,
) : StructureElement
