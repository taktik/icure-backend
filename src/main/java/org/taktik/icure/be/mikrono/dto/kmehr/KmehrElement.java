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

package org.taktik.icure.be.mikrono.dto.kmehr;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: aduchate
 * Date: 15 sept. 2010
 * Time: 12:14:21
 * To change this template use File | Settings | File Templates.
 */
public class KmehrElement implements Serializable {

    String id;
    List<String> otherIds = new ArrayList<>();
    List<String> types = new ArrayList<>();


    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTypes() {
        return types;
    }

    public void addType(String s) {
        if (types == null) {
            types = new ArrayList<>();
        }
        types.add(s);
    }

    public void addId(String s) {
        if (id == null) {
            id = s;
        } else {
            if (otherIds == null) {
                otherIds = new ArrayList<>();
            }
            otherIds.add(s);
        }

    }

    public List<String> getOtherIds() {
        return otherIds;
    }

    public void setOtherIds(List<String> otherIds) {
        this.otherIds = otherIds;
    }

    public String getId(String s) {
        if (id != null && id.startsWith(s + ":")) {
            return id.split(":")[1];
        }
        if (otherIds != null) {
            for (String sid : otherIds) {
                if (sid.startsWith(s + ":")) {
                    return sid.split(":")[1];
                }
            }
        }
        return null;
    }

    @JsonIgnore
    public List<String> getIds() {
        List<String> result = new ArrayList<>();
        
        result.add(id);
        if (otherIds!=null) { result.addAll(otherIds); }
        
        return result;
    }
}
