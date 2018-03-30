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

package org.taktik.icure.services.external.rest.v1.dto;

import java.io.Serializable;

public class ReplicationInfoDto implements Serializable {
	private static final long serialVersionUID = 1L;
	Boolean active = false;
	Boolean running = false;
	Integer pendingFrom;
	Integer pendingTo;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getRunning() {
		return running;
	}

	public void setRunning(Boolean running) {
		this.running = running;
	}

	public Integer getPendingFrom() {
		return pendingFrom;
	}

	public void setPendingFrom(Integer pendingFrom) {
		this.pendingFrom = pendingFrom;
	}

	public Integer getPendingTo() {
		return pendingTo;
	}

	public void setPendingTo(Integer pendingTo) {
		this.pendingTo = pendingTo;
	}
}
