package org.taktik.icure.entities.embed;

import java.util.Objects;

public class LetterValue {
	private String letter;
	private String index;
	private Double coefficient;
	private Double value;

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LetterValue that = (LetterValue) o;
		return Objects.equals(letter, that.letter) &&
				Objects.equals(index, that.index) &&
				Objects.equals(coefficient, that.coefficient) &&
				Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(letter, index, coefficient, value);
	}
}
