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

package org.taktik.icure.services.external.rest.v1.dto.gui.type;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.taktik.icure.services.external.rest.xstream.NumberConverter;

import java.io.Serializable;

/**
 * Created by aduchate on 19/11/13, 10:33
 */
@XStreamAlias("TKMeasure")
public class Measure extends Data implements Serializable {
    public final static int OK = 0;
    public final static int TOO_LOW = 1;
    public final static int TOO_HIGHT = 2;

    @XStreamAsAttribute
    protected String label = "";
    @XStreamAsAttribute
    protected String unit = "";
    @XStreamAsAttribute
    @XStreamConverter(NumberConverter.class)
    protected Number value = null;
    @XStreamAsAttribute
    @XStreamConverter(NumberConverter.class)
    protected Number minRef = 0;
    @XStreamAsAttribute
    @XStreamConverter(NumberConverter.class)
    protected Number maxRef = 0;
    @XStreamAsAttribute
    @XStreamConverter(NumberConverter.class)
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
