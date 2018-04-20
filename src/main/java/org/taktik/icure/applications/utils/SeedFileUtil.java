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

package org.taktik.icure.applications.utils;

import java.io.File;
import java.net.InetAddress;

import org.apache.commons.io.FileUtils;

public class SeedFileUtil {
	public static String readOrCreateSeedsFile(File file) {
		try {
			if (file.exists()) {
				return FileUtils.readFileToString(file);
			} else {
				String ip = InetAddress.getLocalHost().getHostAddress();
				FileUtils.writeStringToFile(file, ip);
				return ip;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeSeeds(File file, String seeds) {
		try {
			FileUtils.writeStringToFile(file, seeds);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}