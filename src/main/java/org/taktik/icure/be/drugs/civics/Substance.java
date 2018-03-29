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

package org.taktik.icure.be.drugs.civics;

import java.util.Date;

public class Substance {

	Long casNr;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	String excInd;
	String activeBaseInd;
	Long baseFormCasId;
	String derivFormType;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCasNr() {
        return casNr;
    }

    public void setCasNr(Long casNr) {
        this.casNr = casNr;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCreatedTms() {
        return createdTms;
    }

    public void setCreatedTms(Date createdTms) {
        this.createdTms = createdTms;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getNameId() {
        return nameId;
    }

    public void setNameId(Long nameId) {
        this.nameId = nameId;
    }

    public String getExcInd() {
        return excInd;
    }

    public void setExcInd(String excInd) {
        this.excInd = excInd;
    }

    public String getActiveBaseInd() {
        return activeBaseInd;
    }

    public void setActiveBaseInd(String activeBaseInd) {
        this.activeBaseInd = activeBaseInd;
    }

    public Long getBaseFormCasId() {
        return baseFormCasId;
    }

    public void setBaseFormCasId(Long baseFormCasId) {
        this.baseFormCasId = baseFormCasId;
    }

    public String getDerivFormType() {
        return derivFormType;
    }

    public void setDerivFormType(String derivFormType) {
        this.derivFormType = derivFormType;
    }
}

