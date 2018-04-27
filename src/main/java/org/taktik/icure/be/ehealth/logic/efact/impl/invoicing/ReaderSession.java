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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;

public class ReaderSession {
	private final Log log = LogFactory.getLog(this.getClass());
	private final PushbackReader reader;

	public ReaderSession(Reader reader) {
		this.reader = new PushbackReader(reader, 6);
	}

	public String read(String label, int length) throws IOException {
		char[] chars = readChars(label, length);
		return new String(chars);
	}

	public int readInt(String label, int length) throws IOException {
		String segment = read(label, length);
		try {
			return Integer.parseInt(segment);
		} catch (NumberFormatException e) {
			log.error("Could not convert segment '" + segment + "' into an integer, for the field '" + label + "'");
		}
		return 0;
	}

	public long readLong(String label, int length) throws IOException {
		String segment = read(label, length);
		try {
			return Long.parseLong(segment);
		} catch (NumberFormatException e) {
			log.error("Could not convert segment '" + segment + "' into a long, for the field '" + label + "'");
		}
		return 0L;
	}

	private char[] readChars(String label, int length) throws IOException {
		char[] chars = new char[length];
		int readChars = reader.read(chars);
		if (readChars < length) {
			throw new EOFException("Not enough characters left to read " + length + " characters from the field '" + label + "'");
		}
		return chars;
	}

	public String getMessageType() throws IOException {
		String messageType = read("Message type",6);
		reader.unread(messageType.toCharArray());
		return messageType;
	}
}
