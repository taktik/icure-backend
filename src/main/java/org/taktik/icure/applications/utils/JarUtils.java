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

package org.taktik.icure.applications.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarUtils {
	public static String getJarPath() {
		// Note : This will only work when packaged as JAR
		try {
			String jarPath = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

			// Make sure we found a jar path
			if (jarPath != null && jarPath.toLowerCase().endsWith(".jar")) {
				return jarPath;
			}
		} catch (URISyntaxException ignored) {
		}

		return null;
	}

	public static Manifest getManifest() {
		Manifest manifest = null;

		// Load JAR Manifest
		String jarPath = getJarPath();
		if (jarPath != null) {
			JarFile jar = null;
			try {
				jar = new JarFile(jarPath);
				manifest = jar.getManifest();
			} catch (IOException ignored) {
			} finally {
				try {
					if (jar != null) {
						jar.close();
					}
				} catch (IOException ignored) {
				}
			}
		}

		return manifest;
	}
}