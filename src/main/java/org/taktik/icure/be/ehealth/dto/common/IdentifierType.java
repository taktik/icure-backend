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

package org.taktik.icure.be.ehealth.dto.common;

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
    private static Map<String, be.ehealth.technicalconnector.utils.IdentifierType> ehMapping = new HashMap<String, be.ehealth.technicalconnector.utils.IdentifierType>();
    private static Map<String, IdentifierType> icMapping = new HashMap<String, IdentifierType>();
    private static IdentifierType register(String code, be.ehealth.technicalconnector.utils.IdentifierType ehIt) {
        IdentifierType it = new IdentifierType(code);
        ehMapping.put(code, ehIt);
        icMapping.put(code, it);
        return it;
    }

    public static final IdentifierType CBE = register("CBE", be.ehealth.technicalconnector.utils.IdentifierType.CBE);
    public static final IdentifierType SSIN = register("SSIN",be.ehealth.technicalconnector.utils.IdentifierType.SSIN);
    public static final IdentifierType NIHII = register("NIHII",be.ehealth.technicalconnector.utils.IdentifierType.NIHII);
    public static final IdentifierType NIHII11 = register("NIHII11",be.ehealth.technicalconnector.utils.IdentifierType.NIHII11);
    public static final IdentifierType NIHII_PHARMACY = register("NIHII_PHARMACY",be.ehealth.technicalconnector.utils.IdentifierType.NIHII_PHARMACY);
    public static final IdentifierType NIHII_LABO = register("NIHII_LABO",be.ehealth.technicalconnector.utils.IdentifierType.NIHII_LABO);
    public static final IdentifierType NIHII_RETIREMENT = register("NIHII_RETIREMENT",be.ehealth.technicalconnector.utils.IdentifierType.NIHII_RETIREMENT);
    public static final IdentifierType NIHII_OTD_PHARMACY = register("NIHII_OTD_PHARMACY",be.ehealth.technicalconnector.utils.IdentifierType.NIHII_OTD_PHARMACY);
    public static final IdentifierType NIHII_HOSPITAL = register("NIHII_HOSPITAL",be.ehealth.technicalconnector.utils.IdentifierType.NIHII_HOSPITAL);
    public static final IdentifierType NIHII_GROUPOFNURSES = register("NIHII_GROUPOFNURSES",be.ehealth.technicalconnector.utils.IdentifierType.NIHII_GROUPOFNURSES);
    public static final IdentifierType HUB = register("HUB",be.ehealth.technicalconnector.utils.IdentifierType.HUB);

    private String code;

    public static IdentifierType fromEhType(be.ehealth.technicalconnector.utils.IdentifierType ehIt) {
        for (Map.Entry<String, be.ehealth.technicalconnector.utils.IdentifierType> e:ehMapping.entrySet()) {
            if (e.getValue().equals(ehIt)) return icMapping.get(e.getKey());
        }
        return null;
    }

    public IdentifierType() {
    }

    public IdentifierType(String code) {
        this.code = code;
    }

    public be.ehealth.technicalconnector.utils.IdentifierType toEhType() {
        return ehMapping.get(this.getCode());
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
