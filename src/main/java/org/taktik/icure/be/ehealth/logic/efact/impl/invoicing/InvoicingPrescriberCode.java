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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 19/08/15
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public enum InvoicingPrescriberCode {
    None(0),
    OnePrescriber(1),
    SelfPrescriber(3),
    AddedCode(4),
    ManyPrescribers(9);
    private int code;

    InvoicingPrescriberCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @SuppressWarnings("unused")
	public static InvoicingPrescriberCode withCode(int prescriberCode) {
        for (InvoicingPrescriberCode ipc : InvoicingPrescriberCode.values()) {
            if (ipc.getCode() == prescriberCode) {
                return ipc;
            }
        }
        return null;

    }
}
