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

package org.taktik.icure.services.external.rest.v1.dto.be.efact.invoicing.segments;

@SuppressWarnings("unused")
public class ErrorDetail {
	private int creationDate;
	private String errorCodeComment;
	private int index;
	private InvoiceRecord invoiceRecord;
	private int invoicingYearMonth;
	private int mutualityCode;
	private String oaResult;
	private String rejectionCode1;
	private String rejectionCode2;
	private String rejectionCode3;
	private String rejectionLetter1;
	private String rejectionLetter2;
	private String rejectionLetter3;
    private String rejectionDescr1;
    private String rejectionDescr2;
    private String rejectionDescr3;
    private String rejectionZoneDescr1;
    private String rejectionZoneDescr2;
    private String rejectionZoneDescr3;
    private String reserve;
	private int sendingId;

	public ErrorDetail() {
	}

	public int getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(int creationDate) {
		this.creationDate = creationDate;
	}

	public String getErrorCodeComment() {
		return errorCodeComment;
	}

	public void setErrorCodeComment(String errorCodeComment) {
		this.errorCodeComment = errorCodeComment;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ErrorDetail(InvoiceRecord invoiceRecord) {
		this.invoiceRecord = invoiceRecord;
	}

	public InvoiceRecord getInvoiceRecord() {
		return invoiceRecord;
	}

	public void setInvoiceRecord(InvoiceRecord invoiceRecord) {
		this.invoiceRecord = invoiceRecord;
	}

	public int getInvoicingYearMonth() {
		return invoicingYearMonth;
	}

	public void setInvoicingYearMonth(int invoicingYearMonth) {
		this.invoicingYearMonth = invoicingYearMonth;
	}

	public int getMutualityCode() {
		return mutualityCode;
	}

	public void setMutualityCode(int mutualityCode) {
		this.mutualityCode = mutualityCode;
	}

	public String getOaResult() {
		return oaResult;
	}

	public void setOaResult(String oaResult) {
		this.oaResult = oaResult;
	}

	public String getRejectionCode1() {
		return rejectionCode1;
	}

	public void setRejectionCode1(String rejectionCode1) {
		this.rejectionCode1 = rejectionCode1;
	}

	public String getRejectionCode2() {
		return rejectionCode2;
	}

	public void setRejectionCode2(String rejectionCode2) {
		this.rejectionCode2 = rejectionCode2;
	}

	public String getRejectionCode3() {
		return rejectionCode3;
	}

	public void setRejectionCode3(String rejectionCode3) {
		this.rejectionCode3 = rejectionCode3;
	}

	public String getRejectionLetter1() {
		return rejectionLetter1;
	}

	public void setRejectionLetter1(String rejectionLetter1) {
		this.rejectionLetter1 = rejectionLetter1;
	}

	public String getRejectionLetter2() {
		return rejectionLetter2;
	}

	public void setRejectionLetter2(String rejectionLetter2) {
		this.rejectionLetter2 = rejectionLetter2;
	}

	public String getRejectionLetter3() {
		return rejectionLetter3;
	}

	public void setRejectionLetter3(String rejectionLetter3) {
		this.rejectionLetter3 = rejectionLetter3;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public int getSendingId() {
		return sendingId;
	}

	public void setSendingId(int sendingId) {
		this.sendingId = sendingId;
	}

    public String getRejectionDescr1() {
        return rejectionDescr1;
    }

    public void setRejectionDescr1(String rejectionDescr1) {
        this.rejectionDescr1 = rejectionDescr1;
    }

    public String getRejectionDescr2() {
        return rejectionDescr2;
    }

    public void setRejectionDescr2(String rejectionDescr2) {
        this.rejectionDescr2 = rejectionDescr2;
    }

    public String getRejectionDescr3() {
        return rejectionDescr3;
    }

    public void setRejectionDescr3(String rejectionDescr3) {
        this.rejectionDescr3 = rejectionDescr3;
    }

    public String getRejectionZoneDescr1() {
        return rejectionZoneDescr1;
    }

    public void setRejectionZoneDescr1(String rejectionZoneDescr1) {
        this.rejectionZoneDescr1 = rejectionZoneDescr1;
    }

    public String getRejectionZoneDescr2() {
        return rejectionZoneDescr2;
    }

    public void setRejectionZoneDescr2(String rejectionZoneDescr2) {
        this.rejectionZoneDescr2 = rejectionZoneDescr2;
    }

    public String getRejectionZoneDescr3() {
        return rejectionZoneDescr3;
    }

    public void setRejectionZoneDescr3(String rejectionZoneDescr3) {
        this.rejectionZoneDescr3 = rejectionZoneDescr3;
    }
}
