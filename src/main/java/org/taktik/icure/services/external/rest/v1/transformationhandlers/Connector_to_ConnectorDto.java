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

package org.taktik.icure.services.external.rest.v1.transformationhandlers;

import org.taktik.icure.entities.Connector;
import org.taktik.icure.services.external.rest.v1.dto.ConnectorDto;
import org.taktik.icure.services.external.rest.v1.dto.VirtualHostDto;
import org.taktik.icure.services.internal.transformationservice.impl.AbstractTransformationHandler;
import org.taktik.commons.service.transformationservice.TransformationContext;
import org.taktik.commons.service.transformationservice.TransformationHandler;

import java.util.Collection;

public class Connector_to_ConnectorDto extends AbstractTransformationHandler implements TransformationHandler<Connector, ConnectorDto> {
	@Override
	public void transform(Collection<? extends Connector> connectors, Collection<? super ConnectorDto> webConnectors, TransformationContext context) {
		for (Connector connector : connectors) {
            ConnectorDto connectorDto = new ConnectorDto();
			connectorDto.setIdentifier(connector.getIdentifier());
			connectorDto.setType(connector.getType());
			connectorDto.setConfiguration(connector.getConfiguration());
			connectorDto.setDefaultVirtualHost(VirtualHostDto.Builder.instance().withId(connector.getDefaultVirtualHost() != null ? connector.getDefaultVirtualHost().getIdentifier() : null).build());
			webConnectors.add(connectorDto);
		}
	}
}