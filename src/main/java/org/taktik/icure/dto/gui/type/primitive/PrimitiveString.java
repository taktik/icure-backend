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

package org.taktik.icure.dto.gui.type.primitive;



import java.io.Serializable;

//
public class PrimitiveString implements Primitive {
    java.lang.String value;

    public PrimitiveString() {
    }

    public PrimitiveString(java.lang.String value) {
        this.value = value;
    }

    public java.lang.String getValue() {
        return value;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    @Override
    public void initWithString(java.lang.String value) {
        setValue(value);
    }

    @Override
    public Serializable getPrimitiveValue() {
        return getValue();
    }

    @Override
    public void setPrimitiveValue(Serializable value) {
        setValue((java.lang.String) value);
    }
}

