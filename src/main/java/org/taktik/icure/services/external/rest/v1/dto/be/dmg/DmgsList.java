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

package org.taktik.icure.services.external.rest.v1.dto.be.dmg;

import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 17/06/14
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
@JsonPolymorphismRoot(DmgMessage.class)
public class DmgsList extends DmgMessage implements Serializable {
    List<DmgInscription> inscriptions = new ArrayList<DmgInscription>();
    Long date;

    public List<DmgInscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<DmgInscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
