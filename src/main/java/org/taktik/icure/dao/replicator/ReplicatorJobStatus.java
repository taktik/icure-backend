package org.taktik.icure.dao.replicator;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Bernard Paulus - 14/03/2017
 */
public final class ReplicatorJobStatus implements Serializable {
    private final long timestamp;
    private final String seq;

    public ReplicatorJobStatus() {
        this(0, null);
    }

    public ReplicatorJobStatus(long timestamp, String seq) {
        this.timestamp = timestamp;
        this.seq = seq;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSeq() {
        return seq;
    }

    public ReplicatorJobStatus timestamp(long timestamp) {
        return new ReplicatorJobStatus(timestamp, seq);
    }

    public ReplicatorJobStatus seq(String seq) {
        return new ReplicatorJobStatus(timestamp, seq);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicatorJobStatus that = (ReplicatorJobStatus) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(seq, that.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, seq);
    }
}
