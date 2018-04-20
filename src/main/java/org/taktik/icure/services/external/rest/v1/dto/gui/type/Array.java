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

package org.taktik.icure.services.external.rest.v1.dto.gui.type;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by aduchate on 19/11/13, 10:21
 */
@XStreamAlias("TKArray")
public class Array<C extends Serializable> extends Data implements Serializable, Collection<C> {
    @XStreamImplicit
    private List<C> value;

    public List<C> getValue() {
        return value;
    }

    public void setValue(List<C> value) {
        this.value = value;
    }

    @Override
    public int size() {
        return value==null?0:value.size();
    }

    @Override
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return value != null && value.contains(o);
    }

    @Override
    public Iterator<C> iterator() {
        return value==null?null:value.iterator();
    }

    @Override
    public Object[] toArray() {
        return value==null?null:value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return value==null?null:value.toArray(ts);
    }

    @Override
    public boolean add(C c) {
        if (value==null) { value = new ArrayList<C>(); }
        return value.add(c);
    }

    @Override
    public boolean remove(Object o) {
        return value != null && value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return value != null && value.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends C> cs) {
        if (value==null) { value = new ArrayList<C>(); }
        return value.addAll(cs);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return value != null && value.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return value != null && value.retainAll(objects);
    }

    @Override
    public void clear() {
        if (value!=null) { value.clear(); }
    }
}
