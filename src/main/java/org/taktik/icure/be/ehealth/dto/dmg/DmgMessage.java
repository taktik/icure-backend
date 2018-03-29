/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.be.ehealth.dto.dmg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.taktik.icure.be.ehealth.dto.Error;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 17/06/14
 * Time: 08:38
 * To change this template use File | Settings | File Templates.
 */
public class DmgMessage implements Serializable {
    protected boolean complete = true;
    private List<Error> errors = new ArrayList<>();
	private String io;
	private String reference;
	private String valueHash;

    public DmgMessage() {
    }

    public DmgMessage(boolean complete) {
        this.complete = complete;
    }

    public DmgMessage withIo(String io) { this.io = io; return this; }

	public DmgMessage withReference(String reference) { this.reference = reference; return this; }

	public DmgMessage withHash(String hash) { this.valueHash = hash; return this; }

	public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

	public String getIo() {
		return io;
	}

	public void setIo(String io) {
		this.io = io;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getValueHash() {
		return valueHash;
	}

	public void setValueHash(String valueHash) {
		this.valueHash = valueHash;
	}
}
