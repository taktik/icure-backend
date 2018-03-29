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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments;

@SuppressWarnings("unused")
public class Bordereau95 {
	private long accountARequestedAmount;
	private String accountARequestedAmountSign;
	private long accountBRequestedAmount;
	private String accountBRequestedAmountSign;
	private String accountsABTotalRequestedAmountSign;
	private long accountsABTotalRequestedAmount;
	private long invoiceRecapNumber;
	private int mutualityCode;
	private int mutualityControlNumber;
	private int recordsAmount;
	private String reserve;
	private int type;
	public Bordereau95() {
	}
	public Bordereau95(long accountARequestedAmount, String accountARequestedAmountSign,
	                   long accountBRequestedAmount, String accountBRequestedAmountSign,
	                   long accountsABTotalRequestedAmount, String accountsABTotalRequestedAmountSign,
	                   long invoiceRecapNumber, int mutualityCode, int mutualityControlNumber, int recordsAmount,
	                   String reserve, int type) {
		this.accountARequestedAmount = accountARequestedAmount;
		this.accountARequestedAmountSign = accountARequestedAmountSign;
		this.accountBRequestedAmount = accountBRequestedAmount;
		this.accountBRequestedAmountSign = accountBRequestedAmountSign;
		this.accountsABTotalRequestedAmount = accountsABTotalRequestedAmount;
		this.accountsABTotalRequestedAmountSign = accountsABTotalRequestedAmountSign;
		this.invoiceRecapNumber = invoiceRecapNumber;
		this.mutualityCode = mutualityCode;
		this.mutualityControlNumber = mutualityControlNumber;
		this.recordsAmount = recordsAmount;
		this.reserve = reserve;
		this.type = type;
	}
	public long getAccountARequestedAmount() {
		return accountARequestedAmount;
	}
	public void setAccountARequestedAmount(long accountARequestedAmount) {
		this.accountARequestedAmount = accountARequestedAmount;
	}
	public String getAccountARequestedAmountSign() {
		return accountARequestedAmountSign;
	}
	public void setAccountARequestedAmountSign(String accountARequestedAmountSign) {
		this.accountARequestedAmountSign = accountARequestedAmountSign;
	}
	public long getAccountBRequestedAmount() {
		return accountBRequestedAmount;
	}
	public void setAccountBRequestedAmount(long accountBRequestedAmount) {
		this.accountBRequestedAmount = accountBRequestedAmount;
	}
	public String getAccountBRequestedAmountSign() {
		return accountBRequestedAmountSign;
	}
	public void setAccountBRequestedAmountSign(String accountBRequestedAmountSign) {
		this.accountBRequestedAmountSign = accountBRequestedAmountSign;
	}
	public long getAccountsABTotalRequestedAmount() {
		return accountsABTotalRequestedAmount;
	}
	public void setAccountsABTotalRequestedAmount(long accountsABTotalRequestedAmount) {
		this.accountsABTotalRequestedAmount = accountsABTotalRequestedAmount;
	}
	public String getAccountsABTotalRequestedAmountSign() {
		return accountsABTotalRequestedAmountSign;
	}
	public void setAccountsABTotalRequestedAmountSign(String accountsABTotalRequestedAmountSign) {
		this.accountsABTotalRequestedAmountSign = accountsABTotalRequestedAmountSign;
	}
	public long getInvoiceRecapNumber() {
		return invoiceRecapNumber;
	}
	public void setInvoiceRecapNumber(long invoiceRecapNumber) {
		this.invoiceRecapNumber = invoiceRecapNumber;
	}
	public int getMutualityCode() {
		return mutualityCode;
	}
	public void setMutualityCode(int mutualityCode) {
		this.mutualityCode = mutualityCode;
	}
	public int getMutualityControlNumber() {
		return mutualityControlNumber;
	}
	public void setMutualityControlNumber(int mutualityControlNumber) {
		this.mutualityControlNumber = mutualityControlNumber;
	}
	public int getRecordsAmount() {
		return recordsAmount;
	}
	public void setRecordsAmount(int recordsAmount) {
		this.recordsAmount = recordsAmount;
	}
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
