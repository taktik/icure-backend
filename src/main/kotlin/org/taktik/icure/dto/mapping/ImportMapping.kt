package org.taktik.icure.dto.mapping

class ImportMapping(var lifecycle: String?,
                    var content: String?,
                    var type: String?,
                    var cdItem: String?,
                    var label: Map<String, String> = HashMap()
                   )