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

package org.taktik.icure.services.external.rest.v2.dto.data;

import org.taktik.icure.services.external.rest.v2.dto.CodeDto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aduchate on 01/02/13, 12:23
 */
public class FormItem implements Serializable {
	protected String label;
	protected Integer index;
	protected String guid; //in case of Form: entityId to form correspondant //or label of field. si nouveau formulaire: pas de guid

	protected List<CodeDto> tags;

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }

	public List<CodeDto> getTags() {
		return tags;
	}

	public void setTags(List<CodeDto> tags) {
		this.tags = tags;
	}
}
