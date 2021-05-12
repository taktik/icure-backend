package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.documentstorage")
class DocumentStorageProperties {
    var cacheLocation: String = ""
    var icureCloudUrl: String = ""
    var useObjectStorage: Boolean = true
    var backlogToObjectStorage: Boolean = true
    var sizeLimit: Long = 2_000_000
}
