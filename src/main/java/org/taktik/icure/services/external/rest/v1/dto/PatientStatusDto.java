/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
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

public class PatientStatusDto extends IcureDto {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean tastesDefined;
	boolean cadreDefined;
	boolean cadreValidated;
	boolean recipesValidated;
	boolean recipesDefined;
	boolean needsDefined;
	boolean physicalActivityDefined;
	
	Double weight;
	Double height;
	
	
	public boolean isTastesDefined() {
		return tastesDefined;
	}
	public void setTastesDefined(boolean tastesDefined) {
		this.tastesDefined = tastesDefined;
	}
	public boolean isCadreDefined() {
		return cadreDefined;
	}
	public void setCadreDefined(boolean cadreDefined) {
		this.cadreDefined = cadreDefined;
	}
	public boolean isCadreValidated() {
		return cadreValidated;
	}
	public void setCadreValidated(boolean cadreValidated) {
		this.cadreValidated = cadreValidated;
	}
	public boolean isRecipesValidated() {
		return recipesValidated;
	}
	public void setRecipesValidated(boolean recipesValidated) {
		this.recipesValidated = recipesValidated;
	}
	public boolean isRecipesDefined() {
		return recipesDefined;
	}
	public void setRecipesDefined(boolean recipesDefined) {
		this.recipesDefined = recipesDefined;
	}
	public boolean isNeedsDefined() {
		return needsDefined;
	}
	public void setNeedsDefined(boolean needsDefined) {
		this.needsDefined = needsDefined;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public boolean isPhysicalActivityDefined() {
		return physicalActivityDefined;
	}
	public void setPhysicalActivityDefined(boolean physicalActivityDefined) {
		this.physicalActivityDefined = physicalActivityDefined;
	}

}
