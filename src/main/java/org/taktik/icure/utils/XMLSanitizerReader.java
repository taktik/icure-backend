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

package org.taktik.icure.utils;

import java.io.IOException;
import java.io.Reader;

public class XMLSanitizerReader extends Reader {
	private Reader nextReader;

	public XMLSanitizerReader(Reader r) {
		nextReader = r;
	}

	@Override
	public void close() throws IOException {
		nextReader.close();
		nextReader = null;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int lengthRead = 0;

		while(lengthRead<len) {
			char[] tbuf = new char[len];
			int res = nextReader.read(tbuf, lengthRead, len-lengthRead);
			if (res == -1) {
				return lengthRead>0 ? lengthRead : -1;
			}

			int delta = 0;

			for (int i = lengthRead;i<lengthRead+res;i++) {
				char c = tbuf[i];
				if ((c>=0) && (c<0x20) && (c!=0x9) && (c!=0xa) && (c!=0xd)) {
					delta++;
				} else {
					cbuf[off+i-delta] = c;
				}
			}
			lengthRead += res - delta;
		}
		return lengthRead;
	}

}

