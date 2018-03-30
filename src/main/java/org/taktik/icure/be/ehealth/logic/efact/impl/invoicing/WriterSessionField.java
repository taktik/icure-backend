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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.taktik.icure.db.StringUtils;

public class WriterSessionField {
	private static final String ZEROS="0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	private static final String BLANKS="                                                                                                                                                                                                                                                                                                                                                                                                                ";
	
	String label;
	String type;
	Object value;
	int length;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public WriterSessionField(String label, String type, Object value, int length) {
		super();
		if (length>280) {
			throw new IllegalArgumentException(label+" too large");
		}
		this.label = label;
		this.type = type;
		this.value = value;
		this.length = length;
	}
	
	public String write(Writer w) throws IOException {
		if (type != null) {
			if (type.equals("N")) {
				NumberFormat nf = new DecimalFormat(ZEROS.substring(0,length));
				long longValue = 0;
				
				if (value instanceof Number) {
					longValue = ((Number) value).longValue();
				} else if (value instanceof String) {
					longValue = ((String) value).length()>0?Long.valueOf((String) value):0l; 
				}
				
				String val = nf.format(value != null ? longValue:0l);
				if (val.length()!= length) {
					throw new IllegalStateException(label+" value is too long");
				}
				w.write(val);
				return val;
			} else if (type.equals("A")) {
				String val = value!=null? StringUtils.removeDiacriticalMarks(value.toString()):"";
				if (val.length()>length) {
					throw new IllegalStateException(label+" value is too long");
				} else if (val.length()<length) {
					val = val+BLANKS.substring(0,length-val.length());
				}
				w.write(val);
				return val;
			} else {
				throw new IllegalStateException("Illegal type "+type+" for "+label);
			}
		} else {
			throw new IllegalStateException("Missing type for "+label);
		}
	}
	
	public static String padBlanks(String val, int n) {
		if (val.length()<n) {
			val = val+BLANKS.substring(0,n-val.length());
		}
		return val;
	}
}
