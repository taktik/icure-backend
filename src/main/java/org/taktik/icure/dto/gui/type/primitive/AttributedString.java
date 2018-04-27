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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by aduchate on 19/11/13, 10:41
 */
@XStreamAlias("TKAttributedString")
public class AttributedString implements Primitive {
    private java.lang.String rtfString = null;
    private byte[] rtfData = null;

    public static java.lang.String getRtfUnicodeEscapedString(java.lang.String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == 0x0a || c == 0x0d)
                sb.append("\\line\n");
            else if (c <= 0x7f)
                sb.append(c);
            else
                sb.append("\\u").append(((int) c)).append("?");
        }
        return sb.toString();
    }

    public AttributedString() {
    }

    public AttributedString(java.lang.String value) {
        initWithString(value);
    }

    public byte[] rtfData() {
        return this.rtfData;
    }

    public void setRtfData(byte[] bs) {
        this.rtfData = bs;
    }

    public void setRtfString(java.lang.String string) {
        this.rtfString = string;
    }

    public java.lang.String rtfString() {
        return this.rtfString;
    }

    public int length() {
        return rtfString.length() > 0 ? rtfString.length() : rtfData.length;
    }

    @Override
    public void initWithString(java.lang.String value) {
        this.rtfString = value;

        try {
            this.rtfData = ("{\\rtf{\\fonttbl{\\f0 Courier;}}\\f0\\fs24 " +
                    getRtfUnicodeEscapedString(value)
                    + " }").getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public Serializable getPrimitiveValue() {
        try {
            return new java.lang.String(this.rtfData(),"UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setPrimitiveValue(Serializable value) {
        try {
            setRtfData(((java.lang.String)value).getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
