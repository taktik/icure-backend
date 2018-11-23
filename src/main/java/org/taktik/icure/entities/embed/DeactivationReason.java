package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.EnumVersion;

import java.io.Serializable;

@EnumVersion(1l)
public enum DeactivationReason implements Serializable{
    deceased, moved, other_doctor, retired, no_contact, unknown, none
}