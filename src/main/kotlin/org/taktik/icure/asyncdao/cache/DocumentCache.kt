package org.taktik.icure.asyncdao.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.taktik.icure.properties.DocumentStorageProperties
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths


@Service
class DocumentCache(private val documentStorageProperties: DocumentStorageProperties) {

    suspend fun store(id: String, attachment: ByteArray) {
        val directory = toFolderPath(documentStorageProperties.cacheLocation, id).toFile()
        directory.mkdirs()
        withContext(Dispatchers.IO) {
            FileUtils.writeByteArrayToFile(File(directory, id), attachment)
        }
    }

    suspend fun store(id: String, attachment: Flow<DataBuffer>) {
        val directory = toFolderPath(documentStorageProperties.cacheLocation, id).toFile()
        directory.mkdirs()
        withContext(Dispatchers.IO) {
            DataBufferUtils.write(attachment.asPublisher(), FileOutputStream(File(directory, id)))
        }
    }

    suspend fun read(id: String): Flow<DataBuffer>? = try {
        DataBufferUtils.read(toFolderPath(documentStorageProperties.cacheLocation, id), DefaultDataBufferFactory(), 10000).asFlow()
    } catch (e: Exception) {
        null
    }

    private fun toFolderPath(cacheLocation: String, id: String) = Paths.get(cacheLocation, id.substring(0, 2), id.substring(2, 4), id.substring(4, 6))

}
