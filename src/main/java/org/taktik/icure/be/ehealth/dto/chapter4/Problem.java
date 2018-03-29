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

package org.taktik.icure.be.ehealth.dto.chapter4;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR;
import be.fgov.ehealth.standards.kmehr.dt.v1.TextType;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 12/06/13
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class Problem implements Serializable {
    private List<CDERROR> cds;
    private String description;
    private String url;

    public Problem() {
    }

    public Problem(List<CDERROR> cds, String description, String url) {
        this.cds = cds;
        this.description = description;
        this.url = url;
    }

    public List<CDERROR> getCds() {
        return cds;
    }

    public void setCds(List<CDERROR> cds) {
        this.cds = cds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
