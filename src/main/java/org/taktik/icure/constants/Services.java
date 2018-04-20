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

package org.taktik.icure.constants;

public interface Services {
	String RESOURCES_PATH = "r/";

	enum Service {
		ACTIVATION("activation"),
		REGISTER("register"),
		LOST_PASSWORD("lost_password"),


		LOGIN("login"),
		LOGIN_TOKEN("login/token"),
		LOGOUT("logout");

		private String path;

		private Service(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}
}