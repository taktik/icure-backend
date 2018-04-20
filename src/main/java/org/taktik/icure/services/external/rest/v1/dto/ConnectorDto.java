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

package org.taktik.icure.services.external.rest.v1.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.constants.Connectors;

import java.util.HashSet;
import java.util.Set;

public class ConnectorDto extends StoredDto {
	private static final long serialVersionUID = 1L;

	protected String identifier;
	protected Connectors.Type type;
	protected String configuration;
    protected VirtualHostDto defaultVirtualHost;

	protected Set<VirtualHostDto> virtualHosts = new HashSet<>();

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Connectors.Type getType() {
		return type;
	}

	public void setType(Connectors.Type type) {
		this.type = type;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public Set<VirtualHostDto> getVirtualHosts() {
		return virtualHosts;
	}

	public void setVirtualHosts(Set<VirtualHostDto> virtualHosts) {
		this.virtualHosts = virtualHosts;
	}

    public VirtualHostDto getDefaultVirtualHost() {
        return defaultVirtualHost;
    }

    public void setDefaultVirtualHost(VirtualHostDto defaultVirtualHost) {
        this.defaultVirtualHost = defaultVirtualHost;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConnectorDto connector = (ConnectorDto) o;

		if (id != null ? !id.equals(connector.id) : connector.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
