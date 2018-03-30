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

package org.taktik.icure.services.external.rest.xstream;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Created by aduchate on 19/11/13, 12:31
 */
public class NumberConverter implements SingleValueConverter {
    protected final static DecimalFormat decimalFormat;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,0",symbols);
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setMaximumFractionDigits(5);
        decimalFormat.setGroupingUsed(false);
    }

    @Override
    public String toString(Object o) {
        return o.toString();
    }

    @Override
    public Object fromString(String s) {
        try {
            return decimalFormat.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public boolean canConvert(Class aClass) {
        return Number.class.isAssignableFrom(aClass);
    }
}
