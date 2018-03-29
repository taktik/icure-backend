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

package org.taktik.icure.services.external.rest.v1.dto;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class VirtualHostDto extends StoredDto implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String identifier;
	protected String hostnames;
	protected String anonymousRole;
	protected String configuration;

    protected Set<String> roles = new HashSet<>();

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getHostnames() {
		return hostnames;
	}

	public void setHostnames(String hostnames) {
		this.hostnames = hostnames;
	}

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAnonymousRole() {
        return anonymousRole;
    }

    public void setAnonymousRole(String anonymousRole) {
        this.anonymousRole = anonymousRole;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public List<String> getHostnamesList() {
		List<String> hostnamesList = new ArrayList<>();
		if (hostnames != null) {
            Collections.addAll(hostnamesList, hostnames.split(" "));
		}
		return hostnamesList;
	}

	public void setHostnamesList(List<String> hostnames) {
		this.hostnames = null;
		if (hostnames != null) {
			this.hostnames = "";
			int h = 0;
			for (String hostname : hostnames) {
				if (h++ > 0) {
					this.hostnames += " ";
				}
				this.hostnames += hostname;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		VirtualHostDto that = (VirtualHostDto) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

    public static class Builder {
        String id;

        public static Builder instance() {
            return new Builder();
        }


        public Builder withId(String id) {
            this.id = id;

            return this;
        }


        public VirtualHostDto build() {
            VirtualHostDto virtualHostDto = new VirtualHostDto();

            virtualHostDto.setId(this.id);

            return virtualHostDto;
        }
    }
}