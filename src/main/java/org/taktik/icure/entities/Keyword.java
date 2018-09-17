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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.KeywordSubword;
import org.taktik.icure.validation.NotNull;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Keyword extends StoredICureDocument {

    @NotNull
    protected String shortcut;

    @NotNull
    protected List<KeywordSubword> values;

    @NotNull
    protected String userId;

    public String getShortcut() {
        return shortcut;
    }

    public void setShortcut(String shortcut) {
        this.shortcut = shortcut;
    }

    public List<KeywordSubword> getValues() {
        return values;
    }

    public void setValues(List<KeywordSubword> values) {
        this.values = values;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
