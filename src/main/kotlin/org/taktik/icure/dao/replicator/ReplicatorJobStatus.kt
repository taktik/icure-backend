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
        return ReplicatorJobStatus(timestamp, seq)
    }

    fun update(timestamp: Long, seq: String?, updates: List<String>): ReplicatorJobStatus {
        return ReplicatorJobStatus(timestamp, seq ?: this.seq, (this.updates + updates).takeLast(10))
    }

    fun seq(seq: String?): ReplicatorJobStatus {
        return ReplicatorJobStatus(timestamp, seq ?: this.seq)
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
