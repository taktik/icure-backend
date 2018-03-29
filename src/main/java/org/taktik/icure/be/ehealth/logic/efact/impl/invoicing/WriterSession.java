/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
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
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

public class WriterSession {

	List<WriterSessionField> fields = new LinkedList<WriterSessionField>();
	
	private Writer writer;

	public WriterSession(Writer writer) {
		this.writer = writer;
	}

	public void registerField(String label, Object value, String type, int length) {
		fields.add(new WriterSessionField(label, type, value, length));
	}

	public void writeFieldsWithCheckSum() throws IOException {
		BigInteger bi = BigInteger.ZERO;
		for (WriterSessionField f : fields) {
			bi = bi.add(BigInteger.valueOf(checksum(f.write(writer))));
		}
		NumberFormat nf = new DecimalFormat("00");
		int modulo = bi.mod(BigInteger.valueOf(97)).intValue();
		writer.write(nf.format(modulo == 0 ? 97 : modulo));
	}

	private long checksum(String value) {
		long res = 0;
		for (int i=0;i<value.length();i++) {
			char c = value.charAt(i);
			if (c>='0' && c<='9') { res+=c-'0'; }
			else if (c==' ') { res+=10; }
			else if (c>='A' && c<='Z') { res+=c-'A'+11; }
			else if (c>='a' && c<='z') { res+=c-'a'+11; }
			else { res += 37; }
		}
		return res;
	}

    public void writeFieldsWithoutCheckSum() throws IOException {
        for (WriterSessionField f : fields) {
            f.write(writer);
        }
    }
}