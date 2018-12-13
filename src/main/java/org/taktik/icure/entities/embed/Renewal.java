package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Renewal implements Serializable {

    Integer decimal;
    Duration duration;

    public Integer getDecimal() { return decimal; }

    public void setDecimal(Integer decimal) { this.decimal = decimal; }

    public Duration getDuration() { return duration; }

    public void setDuration(Duration duration) { this.duration = duration; }
}
