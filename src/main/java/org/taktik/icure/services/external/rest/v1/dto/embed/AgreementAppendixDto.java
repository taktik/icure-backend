package org.taktik.icure.services.external.rest.v1.dto.embed;

public class AgreementAppendixDto {
	private Integer docSeq;
	private Integer verseSeq;
	private String documentId;
	private String path;

	public Integer getDocSeq() {
		return docSeq;
	}

	public void setDocSeq(Integer docSeq) {
		this.docSeq = docSeq;
	}

	public Integer getVerseSeq() {
		return verseSeq;
	}

	public void setVerseSeq(Integer verseSeq) {
		this.verseSeq = verseSeq;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
