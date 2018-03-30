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

package org.taktik.icure.be.drugs.civics;

import java.util.Date;

public class HActualIngredientStrength {

	Long casId;
	Long ampId;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	Long nameId;
	String excInd;
	Long virtualIngredientCasId;
	Double strengthQuantity;
	Double strengthQuantity_2;
	String strengthUnit;
	Double strengthDenomQuantity;
	String strengthDenomUnit;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCasId() {
        return casId;
    }

    public void setCasId(Long casId) {
        this.casId = casId;
    }

    public Long getAmpId() {
        return ampId;
    }

    public void setAmpId(Long ampId) {
        this.ampId = ampId;
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

    public Long getVirtualIngredientCasId() {
        return virtualIngredientCasId;
    }

    public void setVirtualIngredientCasId(Long virtualIngredientCasId) {
        this.virtualIngredientCasId = virtualIngredientCasId;
    }

    public Double getStrengthQuantity() {
        return strengthQuantity;
    }

    public void setStrengthQuantity(Double strengthQuantity) {
        this.strengthQuantity = strengthQuantity;
    }

    public Double getStrengthQuantity_2() {
        return strengthQuantity_2;
    }

    public void setStrengthQuantity_2(Double strengthQuantity_2) {
        this.strengthQuantity_2 = strengthQuantity_2;
    }

    public String getStrengthUnit() {
        return strengthUnit;
    }

    public void setStrengthUnit(String strengthUnit) {
        this.strengthUnit = strengthUnit;
    }

    public Double getStrengthDenomQuantity() {
        return strengthDenomQuantity;
    }

    public void setStrengthDenomQuantity(Double strengthDenomQuantity) {
        this.strengthDenomQuantity = strengthDenomQuantity;
    }

    public String getStrengthDenomUnit() {
        return strengthDenomUnit;
    }

    public void setStrengthDenomUnit(String strengthDenomUnit) {
        this.strengthDenomUnit = strengthDenomUnit;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}

