package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.documentstorage")
data class ObjectStorageProperties(
    var cacheLocation: String = "",
    var icureCloudUrl: String = "",
    var backlogToObjectStorage: Boolean = true,
    var sizeLimit: Long = 2_000_000,
	var migrationDelayMs: Long = 15 * 60 * 1000,
)
