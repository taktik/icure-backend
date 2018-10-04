package org.taktik.icure.dao.replicator

import java.io.Serializable
import java.util.Objects

class ReplicatorJobStatus(
    val timestamp: Long = 0,
    val seq: String? = null,
    updates: List<String> = listOf()
                         ) : Serializable {

    val updates = updates.takeLast(10)

    fun timestamp(timestamp: Long): ReplicatorJobStatus {
        return update(timestamp)
    }

    fun seq(seq: String?): ReplicatorJobStatus {
        return update(timestamp, seq)
    }

    fun update(timestamp: Long, seq: String? = null, updates: List<String> = listOf()): ReplicatorJobStatus {
        return ReplicatorJobStatus(timestamp, seq ?: this.seq, (this.updates + updates).takeLast(10))
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ReplicatorJobStatus?
        return timestamp == that!!.timestamp && seq == that.seq
    }

    override fun hashCode(): Int {
        return Objects.hash(timestamp, seq)
    }
}
