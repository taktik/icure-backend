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

package org.taktik.icure.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ektorp.Attachment;
import org.ektorp.util.Assert;
import org.taktik.icure.entities.embed.RevisionInfo;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@SuppressWarnings({"UnusedDeclaration", "MismatchedQueryAndUpdateOfCollection"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoredDocument implements Versionable<String> {

	@JsonProperty("_attachments")
	private Map<String, Attachment> attachments = new HashMap<>();

	//Do not use deleted as a field... because it is translated to _deleted by ektorp :-(
	@JsonProperty("deleted")
	protected Long deletionDate;
	@JsonProperty("_id")
	protected String id;
	@JsonProperty("_rev")
	protected String rev;
	@JsonProperty("_revs_info")
	protected RevisionInfo[] revisionsInfo;
	@JsonProperty("_conflicts")
	protected String[] conflicts;
	@JsonProperty("java_type")
	protected String _type = this.getClass().getName();
	@JsonProperty("rev_history")
	protected Map<String, String> revHistory = reversedTreeMap();
	
	@JsonIgnore
	public void addInlineAttachment(Attachment a) {
		Assert.notNull(a, "attachment may not be null");
		Assert.hasText(a.getDataBase64(), "attachment must have data base64-encoded");
		if (attachments == null) {
			attachments = new HashMap<>();
		}
		attachments.put(a.getId(), a);
	}

	public void delete() {
		setDeletionDate(System.currentTimeMillis());
	}

	@JsonIgnore
	public void deleteInlineAttachment(String id) {
		Assert.notNull(id, "id may not be null");
		if (attachments != null) {
			attachments.remove(id);
		}
	}

	public Map<String, Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, Attachment> attachments) {
		this.attachments = attachments;
	}

	public Long getDeletionDate() {
		return deletionDate;
	}

	@Override
	@JsonProperty("_id")
	public String getId() {
		return id;
	}

	@JsonProperty("_rev")
	public String getRev() {
		return rev;
	}

	@Override
	@JsonProperty("rev_history")
	public Map<String, String> getRevHistory() {
		return revHistory == null ? reversedTreeMap() : revHistory;
	}

	@JsonProperty("_revs_info")
	public RevisionInfo[] getRevisionsInfo() {
		return revisionsInfo;
	}

	@JsonProperty("_conflicts")
	public String[] getConflicts() {
		return conflicts;
	}

	public void setDeletionDate(Long deletionDate) {
		this.deletionDate = deletionDate;
	}

	@Override
	@JsonProperty("_id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("_rev")
	public void setRev(String rev) {
		this.rev = rev;
	}

	@JsonProperty("rev_history")
	public void setRevHistory(Map<String, String> revHistory) {
		this.revHistory = revHistory;
	}

	public void setRevisionsInfo(RevisionInfo[] revisionsInfo) {
		this.revisionsInfo = revisionsInfo;
	}

	public static <T extends StoredDocument> T strip(T document) {
		T newDoc;

		try {
			//noinspection unchecked
			newDoc = (T) document.getClass().newInstance();
			newDoc.setId(document.getId());
			newDoc.setRev(document.getRev());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return newDoc;
	}

	private TreeMap<String, String> reversedTreeMap() {
		return new TreeMap<>(new ReversedSortedMapComparator());
	}

	private static class ReversedSortedMapComparator implements Comparator<String>, Serializable {
		@Override
		public int compare(String o1, String o2) {
			return o2.compareTo(o1);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StoredDocument)) return false;
		StoredDocument that = (StoredDocument) o;
		return Objects.equals(deletionDate, that.deletionDate) &&
				Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(deletionDate, id, rev);
	}
}
