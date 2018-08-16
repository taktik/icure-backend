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

package org.taktik.icure.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by aduchate on 21/04/13, 17:50
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemTag extends Code {
	public static final String SYSTEM_TYPE = "ICURE_SYSTEM";

	public static final SystemTag MEDICAL_HISTORY = new SystemTag("MEDICAL_HISTORY");
	public static final SystemTag VISIT = new SystemTag("VISIT");
	public static final SystemTag PARACLINIC = new SystemTag("PARACLINIC");
	public static final SystemTag GENERAL_FOLLOWUP = new SystemTag("GENERAL_FOLLOWUP");
	public static final SystemTag GENERAL_STATUS = new SystemTag("GENERAL_STATUS");;


	public SystemTag() {
	}

	public SystemTag(String code) {
		super(SYSTEM_TYPE, code, "1.0");
	}
	
	
}
