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

package org.taktik.icure.services.external.rest.v1.dto.gui.type.primitive;

import java.io.Serializable;

import org.taktik.icure.services.external.rest.v1.dto.gui.type.Data;

/**
 * Created by aduchate on 19/11/13, 10:14
 */
public class PrimitiveDate extends Data implements Primitive {
    java.util.Date value;

    public java.util.Date getValue() {
        return value;
    }

    public void setValue(java.util.Date value) {
        this.value = value;
    }

    @Override
    public void initWithString(String value) {
    	this.value=new java.util.Date();
//      try {
//      setValue(DateUtils.parseDate(value, new String[]{"yyyy-MM-dd'T'HH:mm:ss.S", "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd", "dd/MM/yyyy", "dd/MM/yy" }));
//  } catch (ParseException e) {
//      throw new IllegalArgumentException(e);
//  }
//}

    }


    @Override
    public Serializable getPrimitiveValue() {
        return getValue();
    }

    @Override
    public void setPrimitiveValue(Serializable value) {
        setValue((java.util.Date) value);
    }
}
