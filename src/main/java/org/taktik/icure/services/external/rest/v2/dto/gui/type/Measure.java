/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.dto.gui.type;

import java.io.Serializable;

/**
 * Created by aduchate on 19/11/13, 10:33
 */
public class Measure extends Data implements Serializable {
    public final static int OK = 0;
    public final static int TOO_LOW = 1;
    public final static int TOO_HIGHT = 2;

    protected String label = "";
    protected String unit = "";

    protected Number value = null;

    protected Number minRef = 0;

    protected Number maxRef = 0;

    protected Number severity = 0;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public Number getMinRef() {
        return minRef;
    }

    public void setMinRef(Number minRef) {
        this.minRef = minRef;
    }

    public Number getMaxRef() {
        return maxRef;
    }

    public void setMaxRef(Number maxRef) {
        this.maxRef = maxRef;
    }

    public Number getSeverity() {
        return severity;
    }

    public void setSeverity(Number severity) {
        this.severity = severity;
    }

    public int checkValue() {
        if ((getSeverity() != null) && (getSeverity().intValue() > 0)) {
            return TOO_HIGHT;
        }
        if (getMinRef() == null) {
            if (getMaxRef() == null) {
                return OK;
            } else {
                if (getValue() == null)
                    return OK;
                if (getValue().doubleValue() > getMaxRef().doubleValue()) {
                    return TOO_HIGHT;
                } else {
                    return OK;
                }
            }
        } else {
            if (getMaxRef() == null) {
                if (getValue() == null)
                    return OK;
                if (getValue().doubleValue() < getMinRef().doubleValue()) {
                    return TOO_LOW;
                } else {
                    return OK;
                }
            } else {
                if (getMinRef().equals(getMaxRef())) {
                    return OK;
                }
                if (getValue() == null)
                    return OK;
                if (((getMaxRef().doubleValue()>getMinRef().doubleValue())) && (getValue().doubleValue() > getMaxRef().doubleValue())) {
                    return TOO_HIGHT;
                } else if (getValue().doubleValue() < getMinRef().doubleValue()) {
                    return TOO_LOW;
                } else {
                    return OK;
                }
            }
        }
    }





    public String getRestriction() {
        if (getMinRef() == null) {
            if (getMaxRef() == null) {
                return null;
            } else {
                return "<"+getMaxRef();
            }
        } else {
            if (getMaxRef() == null) {
                return ">"+getMinRef();
            } else {
                if (getMinRef().equals(getMaxRef())) {
                    return null;
                }
                return ""+getMinRef()+"-"+getMaxRef();
            }
        }
    }


}
