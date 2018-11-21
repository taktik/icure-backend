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

package org.taktik.icure.services.external.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.taktik.icure.constants.Roles;
import org.taktik.icure.constants.Users;

import java.io.Serializable;
import java.util.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends StoredDto implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Set<PropertyDto> properties = new HashSet<>();
	private Set<PermissionDto> permissions = new HashSet<>();
	private Users.Type type;
	private Users.Status status;
	private String groupId;
	private String login;
	private String password;
	private String passwordHash;
	private String secret;
	private Boolean use2fa;
	private Long createdDate;
	private Long lastLoginDate;
	private Long expirationDate;
	private String activationToken;
	private Long activationTokenExpirationDate;
	private String passwordToken;
	private Long passwordTokenExpirationDate;
	private Long termsOfUseDate;
	private String healthcarePartyId;

	private List<String> roles = new ArrayList<>();
    private String email;

	private Map<String,Set<String>> autoDelegations = new HashMap<>(); //healthcareIds

	private Map<String, String> applicationTokens = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<PermissionDto> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<PermissionDto> permissions) {
		this.permissions = permissions;
	}

	public Set<PropertyDto> getProperties() {
		return properties;
	}

	public void setProperties(Set<PropertyDto> properties) {
		this.properties = properties;
	}

	public Users.Type getType() {
		return type;
	}

	public void setType(Users.Type value) {
		this.type = value;
	}

	public Users.Status getStatus() {
		return status;
	}

	public void setStatus(Users.Status value) {
		this.status = value;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String value) {
		this.login = value;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String value) {
		this.password = value;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String value) {
		this.passwordHash = value;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Boolean isUse2fa() {
		return use2fa;
	}

	public void setUse2fa(Boolean use2fa) {
		this.use2fa = use2fa;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Long value) {
		this.createdDate = value;
	}

	public Long getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Long value) {
		this.lastLoginDate = value;
	}

	public Long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Long value) {
		this.expirationDate = value;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String value) {
		this.activationToken = value;
	}

	public Long getActivationTokenExpirationDate() {
		return activationTokenExpirationDate;
	}

	public void setActivationTokenExpirationDate(Long value) {
		this.activationTokenExpirationDate = value;
	}

	public String getPasswordToken() {
		return passwordToken;
	}

	public void setPasswordToken(String value) {
		this.passwordToken = value;
	}

	public Long getPasswordTokenExpirationDate() {
		return passwordTokenExpirationDate;
	}

	public void setPasswordTokenExpirationDate(Long value) {
		this.passwordTokenExpirationDate = value;
	}

	public Long getTermsOfUseDate() {
		return termsOfUseDate;
	}

	public void setTermsOfUseDate(Long value) {
		this.termsOfUseDate = value;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String value) {
		this.email = value;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getHealthcarePartyId() {
		return healthcarePartyId;
	}

	public void setHealthcarePartyId(String healthcarePartyId) {
		this.healthcarePartyId = healthcarePartyId;
	}

	public Map<String, Set<String>> getAutoDelegations() {
		return autoDelegations;
	}

	public void setAutoDelegations(Map<String, Set<String>> autoDelegations) {
		this.autoDelegations = autoDelegations;
	}

	public Map<String, String> getApplicationTokens() {
		return applicationTokens;
	}

	public void setApplicationTokens(Map<String, String> applicationTokens) {
		this.applicationTokens = applicationTokens;
	}

	public Roles.VirtualHostDependency getVirtualHostDependency() {
		return Roles.VirtualHostDependency.NONE;
	}

	public Set<String> getVirtualHosts() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserDto user = (UserDto) o;

		if (id != null ? !id.equals(user.id) : user.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
