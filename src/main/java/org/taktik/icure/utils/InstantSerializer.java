/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

public class InstantSerializer extends JsonSerializer<Instant> {
    private static final BigDecimal _1000000 = BigDecimal.valueOf(1000000);
    @Override
    public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeNumber(getBigDecimal(value));
    }

    protected BigDecimal getBigDecimal(Instant value) {
        return BigDecimal.valueOf(1000l * value.getEpochSecond()).add(BigDecimal.valueOf(value.getNano()).divide(_1000000));
    }

    @Override
    public boolean isEmpty(Instant value) {
        return value == null;
    }
}
