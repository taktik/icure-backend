package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.EnumVersion;

import java.io.Serializable;

@EnumVersion(1l)
public enum FrontEndMigrationStatus implements Serializable {
    STARTED, ERROR, SUCCESS
}
