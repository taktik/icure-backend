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
import org.jetbrains.annotations.Nullable;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.entities.base.ICureDocument;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.NotNull;
import org.taktik.icure.validation.ValidCode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by aduchate on 09/07/13, 16:30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanOfAction implements ICureDocument, Serializable {
	private static final long serialVersionUID = 1L;

    @NotNull
    protected String id;

    protected String name;
    protected String descr;

    //Usually one of the following is used
    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long valueDate;   // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.

    @NotNull(autoFix = AutoFix.FUZZYNOW)
    protected Long openingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.
    protected Long closingDate; // YYYYMMDDHHMMSS if unknown, 00, ex:20010800000000. Note that to avoid all confusion: 2015/01/02 00:00:00 is encoded as 20140101235960.

    protected String idOpeningContact;
    protected String idClosingContact;

    @NotNull(autoFix = AutoFix.CURRENTUSERID)
    protected String author; //userId
    @NotNull(autoFix = AutoFix.CURRENTHCPID)
    protected String responsible; //healthcarePartyId

    @NotNull(autoFix = AutoFix.NOW)
    protected Long created;
    @NotNull(autoFix = AutoFix.NOW)
    protected Long modified;
    protected Long endOfLife;

	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected Set<CodeStub> codes = new HashSet<>();
	@ValidCode(autoFix = AutoFix.NORMALIZECODE)
	protected Set<CodeStub> tags = new HashSet<>();

	public PlanOfAction solveConflictWith(PlanOfAction other) {
		this.created = other.created==null?this.created:this.created==null?other.created:Long.valueOf(Math.min(this.created,other.created));
		this.modified = other.modified==null?this.modified:this.modified==null?other.modified:Long.valueOf(Math.max(this.modified,other.modified));
		this.codes.addAll(other.codes);
		this.tags.addAll(other.tags);

		this.openingDate = other.openingDate==null?this.openingDate:this.openingDate==null?other.openingDate:Long.valueOf(Math.min(this.openingDate,other.openingDate));
		this.closingDate = other.closingDate==null?this.closingDate:this.closingDate==null?other.closingDate:Long.valueOf(Math.max(this.closingDate,other.closingDate));
		this.valueDate = other.valueDate==null?this.valueDate:this.valueDate==null?other.valueDate:Long.valueOf(Math.min(this.valueDate,other.valueDate));

		this.name = this.name == null ? other.name : this.name;
		this.descr = this.descr == null ? other.descr : this.descr;

		this.idOpeningContact = this.idOpeningContact == null ? other.idOpeningContact : this.idOpeningContact;
		this.idClosingContact = this.idClosingContact == null ? other.idClosingContact : this.idClosingContact;

		return this;
	}

	public PlanOfAction() {
    }

    public @Nullable String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @Nullable String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public @Nullable String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

	private String encryptedSelf;
	@Override
	public String getEncryptedSelf() {
		return encryptedSelf;
	}

	@Override
	public void setEncryptedSelf(String encryptedSelf) {
		this.encryptedSelf = encryptedSelf;
	}

	public Set<CodeStub> getCodes() {
        return codes;
    }

    public void setCodes(Set<CodeStub> codes) {
        this.codes = codes;
    }

    public Set<CodeStub> getTags() {
        return tags;
    }

    public void setTags(Set<CodeStub> tags) {
        this.tags = tags;
    }

    public @Nullable String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public @Nullable String getIdOpeningContact() {
        return idOpeningContact;
    }

    public void setIdOpeningContact(String idOpeningContact) {
        this.idOpeningContact = idOpeningContact;
    }

    public @Nullable String getIdClosingContact() {
        return idClosingContact;
    }

    public void setIdClosingContact(String idClosingContact) {
        this.idClosingContact = idClosingContact;
    }

	public @Nullable String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public @Nullable Long getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Long closingDate) {
        this.closingDate = closingDate;
    }

    public @Nullable Long getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Long openingDate) {
        this.openingDate = openingDate;
    }

    public @Nullable Long getValueDate() {
        return valueDate;
    }

    public void setValueDate(Long valueDate) {
        this.valueDate = valueDate;
    }

    @Override
    public @Nullable Long getEndOfLife() {
        return endOfLife;
    }

    @Override
    public void setEndOfLife(Long endOfLife) {
        this.endOfLife = endOfLife;
    }

    @Override
    public @Nullable Long getModified() {
        return modified;
    }

    @Override
    public void setModified(Long modified) {
        this.modified = modified;
    }

    @Override
    public @Nullable Long getCreated() {
        return created;
    }

    @Override
    public void setCreated(Long created) {
        this.created = created;
    }
}
