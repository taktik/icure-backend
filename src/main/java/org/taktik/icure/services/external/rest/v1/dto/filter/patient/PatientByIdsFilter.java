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

package org.taktik.icure.services.external.rest.v1.dto.filter.patient;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Objects;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.filter.Filter;

import static org.taktik.icure.db.StringUtils.sanitizeString;

@JsonPolymorphismRoot(Filter.class)
public class PatientByIdsFilter extends Filter<Patient> implements org.taktik.icure.dto.filter.patient.PatientByIdsFilter {
    List<String> ids;

    public PatientByIdsFilter() {
    }

    public PatientByIdsFilter(List<String> ids) {
        this.ids = ids;
    }


    @Override
    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientByIdsFilter that = (PatientByIdsFilter) o;

        return Objects.equal(this.ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ids);
    }

	@Override
	public boolean matches(Patient item) {
		return (ids.contains(item.getId()));
	}
}
