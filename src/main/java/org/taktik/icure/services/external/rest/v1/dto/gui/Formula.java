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

package org.taktik.icure.services.external.rest.v1.dto.gui;





import org.taktik.icure.dto.gui.FormLifecycle;

import java.io.Serializable;

/**
 * Created by aduchate on 03/12/13, 17:22
 */
public class Formula  implements Serializable{
    String value;
    private FormLifecycle lifecycle;

	public Formula() {
	}

	public FormLifecycle getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(FormLifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
