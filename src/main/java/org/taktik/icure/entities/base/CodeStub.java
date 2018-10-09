package org.taktik.icure.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeStub implements Serializable, CodeIdentification {
	@JsonProperty("_id")
	String id;
	String code;
	String type;
	String version;

	public CodeStub() {
	}

	public CodeStub(String type, String code, String version) {
		this.code = code;
		this.type = type;
		this.version = version;

		this.id = type+'|'+code+'|'+version;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CodeStub codeStub = (CodeStub) o;
		return Objects.equals(id, codeStub.id) &&
				Objects.equals(code, codeStub.code) &&
				Objects.equals(type, codeStub.type) &&
				Objects.equals(version, codeStub.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, code, type, version);
	}
}
