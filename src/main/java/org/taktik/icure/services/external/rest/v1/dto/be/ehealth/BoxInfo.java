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

package org.taktik.icure.services.external.rest.v1.dto.be.ehealth;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 04/10/12
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public class BoxInfo implements Serializable {
    protected String boxId;
    protected String quality;
    protected Integer nbrMessagesInStandBy;
    protected Long currentSize;
    protected Long maxSize;

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getNbrMessagesInStandBy() {
        return nbrMessagesInStandBy;
    }

    public void setNbrMessagesInStandBy(Integer nbrMessagesInStandBy) {
        this.nbrMessagesInStandBy = nbrMessagesInStandBy;
    }

    public Long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Long currentSize) {
        this.currentSize = currentSize;
    }

    public Long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Long maxSize) {
        this.maxSize = maxSize;
    }
}
