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

package org.taktik.icure.services.external.rest.v1.dto.be.ehealth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 02/12/13
 * Time: 15:07
 */
public class IdentifierType implements Serializable {
    private String code;

    public static IdentifierType fromEhType(be.ehealth.technicalconnector.utils.IdentifierType ehIt) {
        return new IdentifierType(ehIt.getType(be.ehealth.technicalconnector.utils.IdentifierType.EHBOXV2));
    }

    public be.ehealth.technicalconnector.utils.IdentifierType toEhType() {
        return be.ehealth.technicalconnector.utils.IdentifierType.valueOf(this.code);
    }

    public IdentifierType() {
    }

    public IdentifierType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifierType that = (IdentifierType) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }


}
