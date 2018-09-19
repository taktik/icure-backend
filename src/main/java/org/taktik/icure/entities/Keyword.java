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
    protected String value;

    @NotNull
    protected List<KeywordSubword> subWords;

    @NotNull
    protected String userId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<KeywordSubword> getSubWords() {
        return subWords;
    }

    public void setSubWords(List<KeywordSubword> subWords) {
        this.subWords = subWords;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
