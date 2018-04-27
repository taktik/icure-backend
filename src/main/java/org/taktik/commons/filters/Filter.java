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

package org.taktik.commons.filters;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import org.taktik.commons.serialization.SerializableValue;
import org.taktik.commons.service.rest.gson.JsonDiscriminator;
import org.taktik.commons.service.rest.gson.JsonPolymorphismSupport;

@JsonPolymorphismSupport(
		value = {
				FilterListFilters.class, FilterOnProperty.class, FilterTwoFilters.class
		})
@JsonDiscriminator("$type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = FilterListFilters.class),
		@JsonSubTypes.Type(value = FilterOnProperty.class),
		@JsonSubTypes.Type(value = FilterTwoFilters.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "$type")
@ApiModel(description = "A filter",
		discriminator = "$type",
		subTypes = {
				FilterListFilters.class, FilterOnProperty.class, FilterTwoFilters.class
		})
public interface Filter extends Serializable {
	void loadParameters(Map<String, SerializableValue> parameters);
}