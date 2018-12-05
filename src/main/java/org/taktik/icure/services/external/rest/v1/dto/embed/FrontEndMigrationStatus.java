package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.base.EnumVersion;

import java.io.Serializable;

@EnumVersion(1l)
public enum FrontEndMigrationStatus implements Serializable {
    STARTED, ERROR, SUCCESS
}
