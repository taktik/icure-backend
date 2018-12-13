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

package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jboss.aerogear.security.otp.api.Base32;
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.constants.Roles;
import org.taktik.icure.constants.Users;
import org.taktik.icure.entities.base.Principal;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.DelegationTag;
import org.taktik.icure.entities.embed.Permission;
import org.taktik.icure.utils.InstantDeserializer;
import org.taktik.icure.utils.InstantSerializer;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends StoredDocument implements Principal, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Set<Property> properties = new HashSet<>();
	private Set<Permission> permissions = new HashSet<>();
	private Users.Type type;
	private Users.Status status;
	private String login;
	private String passwordHash;
	private String secret;
	private Boolean use2fa;

	private String groupId;
    private String healthcarePartyId;
	private Map<DelegationTag,Set<String>> autoDelegations = new HashMap<>(); //DelegationTag -> healthcarePartyIds

	@JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	private Instant createdDate;
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	private Instant lastLoginDate;
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	private Instant expirationDate;
	private String activationToken;
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	private Instant activationTokenExpirationDate;
	private String passwordToken;
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	private Instant passwordTokenExpirationDate;
    @JsonSerialize(using = InstantSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = InstantDeserializer.class)
	private Instant termsOfUseDate;

	private Set<String> roles = new HashSet<>();
    private String email;

	private Map<String, String> applicationTokens = new HashMap<>();

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property> properties) {
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String value) {
		this.login = value;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String value) {
		this.passwordHash = value;
	}

    public String getHealthcarePartyId() {
        return healthcarePartyId;
    }

    public void setHealthcarePartyId(String healthcarePartyId) {
        this.healthcarePartyId = healthcarePartyId;
    }

    public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant value) {
		this.createdDate = value;
	}

	public Instant getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Instant value) {
		this.lastLoginDate = value;
	}

	public Instant getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Instant value) {
		this.expirationDate = value;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String value) {
		this.activationToken = value;
	}

	public Instant getActivationTokenExpirationDate() {
		return activationTokenExpirationDate;
	}

	public void setActivationTokenExpirationDate(Instant value) {
		this.activationTokenExpirationDate = value;
	}

	public String getPasswordToken() {
		return passwordToken;
	}

	public void setPasswordToken(String value) {
		this.passwordToken = value;
	}

	public Instant getPasswordTokenExpirationDate() {
		return passwordTokenExpirationDate;
	}

	public void setPasswordTokenExpirationDate(Instant value) {
		this.passwordTokenExpirationDate = value;
	}

	public Instant getTermsOfUseDate() {
		return termsOfUseDate;
	}

	public void setTermsOfUseDate(Instant value) {
		this.termsOfUseDate = value;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String value) {
		this.email = value;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Map<DelegationTag, Set<String>> getAutoDelegations() {
		return autoDelegations;
	}

	public void setAutoDelegations(Map<DelegationTag, Set<String>> autoDelegations) {
		this.autoDelegations = autoDelegations;
	}

	public Map<String, String> getApplicationTokens() {
		return applicationTokens;
	}

	public void setApplicationTokens(Map<String, String> applicationTokens) {
		this.applicationTokens = applicationTokens;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    @JsonIgnore
    @Override
	public Set<String> getParents() {
		return roles;
	}

    @JsonIgnore
    @Override
	public Roles.VirtualHostDependency getVirtualHostDependency() {
		return Roles.VirtualHostDependency.NONE;
	}

    @JsonIgnore
    @Override
	public Set<String> getVirtualHosts() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		return !(id != null ? !id.equals(user.id) : user.id != null);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@JsonIgnore
	public boolean isSecretEmpty() {
		return secret == null;
	}

	public String getSecret() {
		if (secret == null) {
			this.secret = Base32.random();
		}
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public @Nullable Boolean isUse2fa() {
		return use2fa;
	}

	public void setUse2fa(Boolean use2fa) {
		this.use2fa = use2fa;
	}
}
