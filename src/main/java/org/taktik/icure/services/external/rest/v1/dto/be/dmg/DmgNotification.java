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

package org.taktik.icure.services.external.rest.v1.dto.be.dmg;

import java.io.Serializable;

import org.taktik.icure.services.external.rest.handlers.JsonPolymorphismRoot;
import org.taktik.icure.services.external.rest.v1.dto.be.ehealth.HcParty;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 17/06/14
 * Time: 07:55
 * To change this template use File | Settings | File Templates.
 */
@JsonPolymorphismRoot(DmgMessage.class)
public class DmgNotification extends DmgMessage implements Serializable {
    private HcParty hcParty;
    private Boolean payment;
    private Long from;

    public DmgNotification() {
    }

    public DmgNotification(boolean complete) {
        super(complete);
    }

    public HcParty getHcParty() {
        return hcParty;
    }

    public void setHcParty(HcParty hcParty) {
        this.hcParty = hcParty;
    }

    public Boolean getPayment() {
        return payment;
    }

    public void setPayment(Boolean payment) {
        this.payment = payment;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getFrom() {
        return from;
    }
}
