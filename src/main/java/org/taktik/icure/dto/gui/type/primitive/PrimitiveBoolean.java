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
import java.util.Arrays;
import java.util.List;

public class PrimitiveBoolean implements Primitive {
    static List<String> positiveValues = Arrays.asList("yes", "ja", "oui", "si", "yo", "ok");
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
