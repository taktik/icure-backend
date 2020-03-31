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

package org.taktik.icure.entities.embed;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseSynchronization implements Serializable {
    public enum Target {
        base, healthdata, patient
    }

	protected String source;
	protected String target;
	protected String filter;

	protected Target localTarget;

	public DatabaseSynchronization() {
	}

	public DatabaseSynchronization(String source, String target) {
		this(source,target,null);
	}
	public DatabaseSynchronization(String source, String target, String filter) {
		this.source = source;
		this.target = target;
		this.filter = filter;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getFilter() {
		return filter;
	}

    public Target getLocalTarget() {
        return localTarget;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setLocalTarget(Target localTarget) {
        this.localTarget = localTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatabaseSynchronization)) return false;
        DatabaseSynchronization that = (DatabaseSynchronization) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(target, that.target) &&
                Objects.equals(filter, that.filter) &&
                localTarget == that.localTarget;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, filter, localTarget);
    }
}
