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

package org.taktik.icure.services.external.rest.v1.dto.be.kmehr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.taktik.icure.services.external.rest.v1.dto.HealthcarePartyDto;

public class SumehrExportInfoDto extends ExportInfoDto implements Serializable {
    private String comment;
	private Boolean includeIrrelevantInformation;

    public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

    public Boolean getIncludeIrrelevantInformation() {
        return includeIrrelevantInformation;
    }

    public void setIncludeIrrelevantInformation(Boolean includeIrrelevantInformation) {
        this.includeIrrelevantInformation = includeIrrelevantInformation;
    }
}
