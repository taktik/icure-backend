/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class InstantDeserializer extends JsonDeserializer<Instant> {
    private static final BigDecimal _1000000 = BigDecimal.valueOf(1000000);
    private static final BigDecimal _1000 = BigDecimal.valueOf(1000);
    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        BigDecimal val = jp.getDecimalValue();
        return getInstant(val);
    }

    protected Instant getInstant(BigDecimal val) {
        return Instant.ofEpochSecond(val.divide(_1000).longValue(), val.remainder(_1000).multiply(_1000000).longValue());
    }
}
