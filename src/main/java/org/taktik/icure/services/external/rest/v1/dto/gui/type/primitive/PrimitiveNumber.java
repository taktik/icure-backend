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



import org.taktik.icure.services.external.rest.v1.dto.gui.type.Data;

import java.io.Serializable;

/**
 * Created by aduchate on 19/11/13, 10:38
 */
public class PrimitiveNumber extends Data implements Primitive {

	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void initWithString(String value) {
	}

	@Override
	public Serializable getPrimitiveValue() {
		return null;
	}

	@Override
	public void setPrimitiveValue(Serializable value) {
	}

//
//    java.lang.Number value;
//
//
//
//    public PrimitiveNumber() {
//    }
//
//    public PrimitiveNumber(java.lang.Number value) {
//        this.value = value;
//    }
//
//
//    public PrimitiveNumber(String value) {
//    	 initWithString(value);
//    }
//
//    public java.lang.Number getValue() {
//        return value;
//    }
//
//    public void setValue(java.lang.Number value) {
//        this.value = value;
//    }
//
//    public void setValue(String value) {
//        initWithString(value);
//    }
//
//
//    @Override
//    public void initWithString(String value) {
////        try {
////            setValue(decimalFormat.parse(value));
//            setValue(Double.valueOf(value));
////        } catch (ParseException e) {
////            throw new IllegalArgumentException(e);
////        }
//    }
//
//    @Override
//    public Serializable getPrimitiveValue() {
//        return getValue();
//    }
//
//    @Override
//    public void setPrimitiveValue(Serializable value) {
//        setValue((java.lang.Number) value);
//    }
}
