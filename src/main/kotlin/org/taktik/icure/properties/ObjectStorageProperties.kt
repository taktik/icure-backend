package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.objectstorage")
data class ObjectStorageProperties(
    var cacheLocation: String? = null,
    var icureCloudUrl: String? = null,
    var backlogToObjectStorage: Boolean = true,
	/**
	 * Minimum size for new attachments to be stored to object storage.
	 */
    var sizeLimit: Long = 2_000_000,
	/**
	 * Minimum size for existing attachments to be migrated to object storage.
	 * Must be greater than [sizeLimit].
	 */
	var migrationSizeLimit: Long = 4_000_000,
	var migrationDelayMs: Long = 15 * 60 * 1000,
)
