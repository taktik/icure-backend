package org.taktik.icure.be.ehealth.logic.kmehr

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import java.nio.ByteBuffer

fun validSsinOrNull(ssin : String?): String? {
    val res : String
    return if(ssin == null) {
        null
    } else {
        res = ssin.replace(" ", "").replace("-", "").replace(".", "").replace("/", "")
        if(res.length == 11) {
            res
        } else {
            null
        }
    }
}

fun validNihiiOrNull(nihii : String?): String? {
    val res : String
    return if(nihii == null) {
        null
    } else {
        res = nihii.replace(" ", "").replace("-", "").replace(".", "").replace("/", "")
        if(res.length == 11) {
            res
        } else {
            null
        }
    }
}

suspend fun byteBufferArrayToInputStream(bytes : Flow<ByteBuffer>): ByteBufferBackedInputStream {
    val toList = bytes.toList()
    var sum = 0
    toList.forEach { l -> sum += l.limit()}
    val fullB = ByteBuffer.allocate(sum)
    toList.forEach { l -> fullB.put(l) }
    return ByteBufferBackedInputStream(fullB)
}
