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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.constants.Connectors;
import org.taktik.icure.entities.base.StoredDocument;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Connector extends StoredDocument {
	private static final long serialVersionUID = 1L;

	protected String identifier;
	protected Connectors.Type type;
	protected String configuration;
    protected VirtualHost defaultVirtualHost;

	protected Set<VirtualHost> virtualHosts = new HashSet<>();

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

	public Set<VirtualHost> getVirtualHosts() {
		return virtualHosts;
	}

	public void setVirtualHosts(Set<VirtualHost> virtualHosts) {
		this.virtualHosts = virtualHosts;
	}

    public VirtualHost getDefaultVirtualHost() {
        return defaultVirtualHost;
    }

    public void setDefaultVirtualHost(VirtualHost defaultVirtualHost) {
        this.defaultVirtualHost = defaultVirtualHost;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Connector connector = (Connector) o;

		if (id != null ? !id.equals(connector.id) : connector.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

    public static class Builder {
        private String identifier;
        private Connectors.Type type;
        private String configuration = "{}";
        private VirtualHost defaultVirtualHost;

        public static Builder instance() {
            return new Builder();
        }

        public Builder withIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withType(Connectors.Type type) {
            this.type = type;
            return this;
        }

        public Builder withConfiguration(String configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder withDefaultVirtualHost(VirtualHost defaultVhost) {
            this.defaultVirtualHost = defaultVhost;
            return this;
        }

        public Connector build() {
            Connector c = new Connector();

            c.setIdentifier(identifier);
            c.setType(type);
            c.setConfiguration(configuration);
            c.setDefaultVirtualHost(defaultVirtualHost);
            return c;
        }
    }
}