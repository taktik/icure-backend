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

package org.taktik.icure.logic;

import java.util.List;
import java.util.Set;

import org.taktik.icure.db.PaginatedList;
import org.taktik.icure.db.PaginationOffset;
import org.taktik.icure.entities.Insurance;
import org.taktik.icure.exceptions.DeletionException;

public interface InsuranceLogic extends EntityPersister<Insurance, String> {

	Insurance createInsurance(Insurance insurance);

	String deleteInsurance(String insuranceId) throws DeletionException;

	Insurance getInsurance(String insuranceId);

	List<Insurance> listInsurancesByCode(String code);

	List<Insurance> listInsurancesByName(String name);

	Insurance modifyInsurance(Insurance insurance);

	List<Insurance> getInsurances(Set<String> ids);
}
