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

package org.taktik.icure.dto.gui.type;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.io.Serializable;

import java.util.Map;

/**
 * Created by aduchate on 19/11/13, 10:29
 */
@XStreamAlias("TKDictionary")
public class Dictionary implements Serializable , Data {
    @XStreamImplicit
    Map<String,? extends Serializable> value;

    public Map<String, ? extends Serializable> getValue() {
        return value;
    }

    public void setValue(Map<String, ? extends Serializable> value) {
        this.value = value;
    }
}
