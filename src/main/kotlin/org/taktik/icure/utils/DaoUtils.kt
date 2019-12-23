package org.taktik.icure.utils

import org.ektorp.ComplexKey
import org.taktik.icure.db.PaginationOffset
import java.net.URI

internal val ALL_ENTITIES_CACHE_KEY = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"

inline fun <reified E> PaginationOffset<List<E>>.toComplexKeyPaginationOffset(): PaginationOffset<ComplexKey> =
        PaginationOffset(this.startKey?.toComplexKey(), this.startDocumentId, this.offset, this.limit)

inline fun <reified E> List<E>.toComplexKey(): ComplexKey = ComplexKey.of(*this.toTypedArray())

fun getFullId(dbInstanceUrl: URI, groupId: String?, id: String) = "${dbInstanceUrl}:${groupId ?: "FALLBACK"}:$id"
