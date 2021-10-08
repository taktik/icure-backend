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

import org.taktik.commons.serialization.SerializableValue;
import org.taktik.icure.services.external.rest.v2.dto.CodeDto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aduchate on 01/02/13, 12:27
 */
public class FormDataItem extends FormItem implements Serializable
{
    protected Date openingDate;
    protected Date closingDate;

    protected FormItem previousVersion;

    protected Map<String, SerializableValue> content; //Diabete type II induced by obesity
    protected List<HashMap<String, SerializableValue>> listOfMultipleValues;

    protected List<CodeDto> tags = new java.util.ArrayList<CodeDto>(); //Caracterises the data (ex: Objective data, Diagnostic) independent of content
    protected List<CodeDto> codes = new java.util.ArrayList<CodeDto>(); //Caracterises the value of the data (ex: ICD10-1234: Diabete) dependent on content


    public void setUniqueValue(Serializable object) {
    	if(content==null) content = new HashMap<String, SerializableValue>();
    	content.put("value", new SerializableValue(object));
    }

    public SerializableValue getUniqueValue() {
    	return content.get("value");
    }



    public Date getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}
	public Date getClosingDate() {
		return closingDate;
	}
	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}
	public FormItem getPreviousVersion() {
		return previousVersion;
	}
	public void setPreviousVersion(FormItem previousVersion) {
		this.previousVersion = previousVersion;
	}
	public Map<String, SerializableValue> getContent() {
		return content;
	}
	public void setContent(Map<String, SerializableValue> content) {
		this.content = content;
	}
	public List<CodeDto> getTags() {
		return tags;
	}
	public void setTags(List<CodeDto> tags) {
		this.tags = tags;
	}
	public List<CodeDto> getCodes() {
		return codes;
	}
	public void setCodes(List<CodeDto> codes) {
		this.codes = codes;
	}

	public List<HashMap<String, SerializableValue>> getListOfMultipleValues() {
		return listOfMultipleValues;
	}

	public void setListOfMultipleValues(List<HashMap<String, SerializableValue>> listOfMultipleValues) {
		this.listOfMultipleValues = listOfMultipleValues;
	}
}
