/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
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
