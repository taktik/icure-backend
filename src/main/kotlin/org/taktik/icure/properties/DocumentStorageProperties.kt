package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.documentstorage")
class DocumentStorageProperties {
    var cacheLocation :String = "/Users/michalbork/Tools/cache/icure"
    var icureCloudUrl: String = "http://localhost:5984"
    var useObjectStorage: Boolean = true
    var backlogToObjectStorage: Boolean = true
    var sizeLimit: Long = 0 //2_000_000
}
