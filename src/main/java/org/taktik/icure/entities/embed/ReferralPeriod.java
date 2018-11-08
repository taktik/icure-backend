/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.utils.InstantDeserializer;
import org.taktik.icure.utils.InstantSerializer;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferralPeriod implements Serializable,Comparable<ReferralPeriod> {
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant startDate;

    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant endDate;

    private String comment;

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(ReferralPeriod other) {
        if (Objects.equals(this,other)) { return 0; }
        if (!Objects.equals(this.startDate,other.startDate)) {
            return this.startDate==null?1:other.startDate==null?0:this.startDate.compareTo(other.startDate);
        }
        if (!Objects.equals(this.endDate,other.endDate)) {
            return this.endDate==null?1:other.endDate==null?0:this.endDate.compareTo(other.endDate);
        }
        return 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ReferralPeriod other = (ReferralPeriod) obj;
        return Objects.equals(this.startDate, other.startDate) && Objects.equals(this.endDate, other.endDate);
    }

    public ReferralPeriod(Instant startDate, Instant endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ReferralPeriod() {
    }
}
