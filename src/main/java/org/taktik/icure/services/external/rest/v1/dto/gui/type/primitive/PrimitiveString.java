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

package org.taktik.icure.services.external.rest.v1.dto.gui.type.primitive;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.taktik.icure.services.external.rest.v1.dto.gui.type.Data;

import java.io.Serializable;

@XStreamAlias("TKString")
//@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
public class PrimitiveString extends Data implements Primitive {
    String value;

    public PrimitiveString() {
    }

    public PrimitiveString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void initWithString(String value) {
        setValue(value);
    }

    @Override
    public Serializable getPrimitiveValue() {
        return getValue();
    }

    @Override
    public void setPrimitiveValue(Serializable value) {
        setValue((String) value);
    }
}

