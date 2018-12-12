package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.io.Serializable;

public class RenewalDto implements Serializable {

    Integer decimal;
    DurationDto duration;

    public Integer getDecimal() { return decimal; }

    public void setDecimal(Integer decimal) { this.decimal = decimal; }

    public DurationDto getDuration() { return duration; }

    public void setDuration(DurationDto duration) { this.duration = duration; }
}
