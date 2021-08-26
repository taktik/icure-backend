package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

class Section(
    val section: String,
    val fields: List<StructureElement>,
    val description: String? = null,
    val keywords: List<String>? = null,
    )
