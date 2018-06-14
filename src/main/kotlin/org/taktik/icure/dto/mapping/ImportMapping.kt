package org.taktik.icure.dto.mapping

class ImportMapping(var lifecycle: String? = null,
                    var content: String? = null,
                    var type: String? = null,
                    var cdItem: String? = null,
                    var label: Map<String, String> = HashMap()
                   )