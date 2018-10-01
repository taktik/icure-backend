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

package org.taktik.icure.db;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;

public class StringUtils {
	public static String sanitizeString(String key) {
		if (key == null) {
			return null;
		}

		return removeDiacriticalMarks(key).replaceAll("[\\s]", "").replaceAll("\\W", "").toLowerCase();
	}

	public static String removeDiacriticalMarks(String key) {
		try {
			if (Normalizer.class.getMethod("normalize", CharSequence.class, Normalizer.Form.class) != null) {
				return Normalizer.normalize(key.replaceAll("ø","o").replaceAll("æ","ae").replaceAll("Æ","AE").replaceAll("Œ","oe").replaceAll("œ","oe"), Normalizer.Form.NFD)
						.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
			}
		} catch (NoSuchMethodException ignored) {
		}

		//Fallback
		return key.replaceAll("[\u00E8\u00E9\u00EA\u00EB]", "e")
				.replaceAll("[\u00FB\u00F9\u00FC]", "u")
				.replaceAll("[\u00E7]", "c")
				.replaceAll("[\u00EF\u00EE\u00EC]", "i")
				.replaceAll("[\u00E0\u00E2\u00E4]", "a")
				.replaceAll("[\u00F6\u00F2\u00F4]", "o")
				.replaceAll("[\u00C8\u00C9\u00CA\u00CB]", "E")
				.replaceAll("[\u00DB\u00D9\u00DC]", "U")
				.replaceAll("[\u00CF\u00CE\u00CC]", "I")
				.replaceAll("[\u00C0\u00C2\u00C4]", "A")
				.replaceAll("[\u00D4\u00D6\u00D2]", "O")
				.replaceAll("ø","o")
				.replaceAll("æ","ae")
				.replaceAll("Æ","AE")
			.replaceAll("Œ","oe")
			.replaceAll("œ","oe");
	}

	public static String safeConcat(String... components) {
		StringBuffer sb = new StringBuffer();
		for (String c : components) {
			if (c != null) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String detectFrenchCp850Cp1252(byte[] data) {
		BufferedReader br;
		int score = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "cp850"));
			int c;
			while ((c = br.read()) != -1) {
				if (c == '\u00e8') {
					score++;
				} else if (c == '\u00e9') {
					score++;
				} else if (c == '\u00e0') {
					score++;
				} else if (c == '\u00e7') {
					score++;
				} else if (c == '\u00b5') {
					score++;
				} else if (c == '\u00d3') {
					score--;
				} else if (c == '\u00fe') {
					score--;
				} else if (c == '\u00de') {
					score--;
				} else if (c == '\u00da') {
					score--;
				} else if (c == '\u00c1') {
					score--;
				}
			}
			return score > 3 ? "cp850" : score < -3 ? "cp1252" : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean equals(@Nullable String s1, @Nullable String s2) {
		return (s1 != null && s2 != null) && (org.apache.commons.lang3.StringUtils.equals(s1,s2) || org.apache.commons.lang3.StringUtils.equals(sanitizeString(s1),sanitizeString(s2)));
	}
}
