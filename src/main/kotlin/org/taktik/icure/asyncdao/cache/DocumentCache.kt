package org.taktik.icure.asyncdao.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import org.taktik.icure.properties.DocumentStorageProperties
import java.io.File
import java.nio.file.Paths


@Service
class DocumentCache(private val documentStorageProperties: DocumentStorageProperties) {

    suspend fun store(id: String, attachment: ByteArray) {
        val directory = toFolderFile(documentStorageProperties.cacheLocation, id)
        directory.mkdirs()
        withContext(Dispatchers.IO) {
            FileUtils.writeByteArrayToFile(File(directory, id), attachment)
        }
    }

    suspend fun read(id: String): ByteArray? = withContext(Dispatchers.IO) {
        try {
            FileUtils.readFileToByteArray(File(toFolderFile(documentStorageProperties.cacheLocation, id), id))
                    .takeUnless { it.isEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    private fun toFolderFile(cacheLocation: String, id: String) = Paths.get(cacheLocation, id.substring(0, 2), id.substring(2, 4), id.substring(4, 6)).toFile()

}
