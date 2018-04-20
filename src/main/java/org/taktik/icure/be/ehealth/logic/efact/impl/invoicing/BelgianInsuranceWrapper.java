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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("message")
public class BelgianInsuranceWrapper {
    private String raw;
    private String xades;
    private BelgianInsuranceInvoicingWrapper analysis;

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getXades() {
        return xades;
    }

    public void setXades(String xades) {
        this.xades = xades;
    }

    public BelgianInsuranceInvoicingWrapper getAnalysis() {
        return analysis;
    }

    public void setAnalysis(BelgianInsuranceInvoicingWrapper analysis) {
        this.analysis = analysis;
    }

    public static class BelgianInsuranceInvoicingWrapper {
        @XStreamAlias("org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.BelgianInsuranceInvoicing")
        private BelgianInsuranceInvoicing analysis;

        public BelgianInsuranceInvoicingWrapper() {
        }

        public BelgianInsuranceInvoicingWrapper(BelgianInsuranceInvoicing analysis) {
            this.analysis = analysis;
        }

        public BelgianInsuranceInvoicing getAnalysis() {
            return analysis;
        }

        public void setAnalysis(BelgianInsuranceInvoicing analysis) {
            this.analysis = analysis;
        }
    }
}
