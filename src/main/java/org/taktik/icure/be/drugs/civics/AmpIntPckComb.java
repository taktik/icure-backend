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

public class AmpIntPckComb {

	Amp ampCmb;
	Amp amp;

	Double ampIntPckCqCmb;
	String ampIntPckCuCmb;
	Double ampIntPckCq;
	String ampIntPckCu;
	Date startDate;
	Date createdTms;
	String createdUserId;
	Date endDate;
	String ampIntPckCmbSeq;
	String modificationStatus;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Amp getAmpCmb() {
        return ampCmb;
    }

    public void setAmpCmb(Amp ampCmb) {
        this.ampCmb = ampCmb;
    }

    public Amp getAmp() {
        return amp;
    }

    public void setAmp(Amp amp) {
        this.amp = amp;
    }

    public Double getAmpIntPckCqCmb() {
        return ampIntPckCqCmb;
    }

    public void setAmpIntPckCqCmb(Double ampIntPckCqCmb) {
        this.ampIntPckCqCmb = ampIntPckCqCmb;
    }

    public String getAmpIntPckCuCmb() {
        return ampIntPckCuCmb;
    }

    public void setAmpIntPckCuCmb(String ampIntPckCuCmb) {
        this.ampIntPckCuCmb = ampIntPckCuCmb;
    }

    public Double getAmpIntPckCq() {
        return ampIntPckCq;
    }

    public void setAmpIntPckCq(Double ampIntPckCq) {
        this.ampIntPckCq = ampIntPckCq;
    }

    public String getAmpIntPckCu() {
        return ampIntPckCu;
    }

    public void setAmpIntPckCu(String ampIntPckCu) {
        this.ampIntPckCu = ampIntPckCu;
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

    public String getAmpIntPckCmbSeq() {
        return ampIntPckCmbSeq;
    }

    public void setAmpIntPckCmbSeq(String ampIntPckCmbSeq) {
        this.ampIntPckCmbSeq = ampIntPckCmbSeq;
    }

    public String getModificationStatus() {
        return modificationStatus;
    }

    public void setModificationStatus(String modificationStatus) {
        this.modificationStatus = modificationStatus;
    }
}

