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

package org.taktik.icure.services.external.rest.v1.dto.be.chapter4;

import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.kmehr.v20130710.be.fgov.ehealth.standards.kmehr.cd.v1.CDERROR;

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
    private String descr;
    private String url;

    public Problem() {
    }

    public Problem(List<CDERROR> cds, String descr, String url) {
        this.cds = cds;
        this.descr = descr;
        this.url = url;
    }

    public List<CDERROR> getCds() {
        return cds;
    }

    public void setCds(List<CDERROR> cds) {
        this.cds = cds;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
