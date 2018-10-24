package org.taktik.icure.entities;

import org.taktik.icure.entities.base.StoredDocument;

import java.util.Objects;

public class EntityReference extends StoredDocument  {
	String docId;

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		EntityReference that = (EntityReference) o;
		return Objects.equals(docId, that.docId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), docId);
	}
}
