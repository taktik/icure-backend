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

package org.taktik.icure.dto.gui.type.primitive;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@XStreamAlias("TKBoolean")
public class PrimitiveBoolean implements Primitive {
    static List<String> positiveValues = Arrays.asList("yes", "ja", "oui", "si", "yo", "ok");
    @XStreamAsAttribute
    java.lang.Boolean value;

    public java.lang.Boolean getValue() {
        return value;
    }

    public void setValue(java.lang.Boolean value) {
        this.value = value;
    }

    @Override
    public void initWithString(String value) {
        if (value==null) { setValue(null); } else {
            setValue(positiveValues.contains(value.toLowerCase()));
        }
    }

    @Override
    public Serializable getPrimitiveValue() {
        return getValue();
    }

    @Override
    public void setPrimitiveValue(Serializable value) {
        setValue((java.lang.Boolean) value);
    }
}