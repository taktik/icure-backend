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

package org.taktik.icure.entities.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialInstitutionInformation implements Serializable {
	protected String name;
	protected String key;

	protected String bankAccount;
	protected String bic;

	protected String proxyBankAccount;
	protected String proxyBic;

	protected Set<String> preferredFiiForPartners = new HashSet<>(); //Insurance Id, Hcp Id

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getProxyBankAccount() {
		return proxyBankAccount;
	}

	public void setProxyBankAccount(String proxyBankAccount) {
		this.proxyBankAccount = proxyBankAccount;
	}

	public String getProxyBic() {
		return proxyBic;
	}

	public void setProxyBic(String proxyBic) {
		this.proxyBic = proxyBic;
	}

	public Set<String> getPreferredFiiForPartners() {
		return preferredFiiForPartners;
	}

	public void setPreferredFiiForPartners(Set<String> preferredFiiForPartners) {
		this.preferredFiiForPartners = preferredFiiForPartners;
	}
}
