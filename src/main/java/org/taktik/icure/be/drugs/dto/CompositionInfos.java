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


package org.taktik.icure.be.drugs.dto;

import java.io.Serializable;

public class CompositionInfos implements Serializable {

	private static final long serialVersionUID = 1L;
     private CompositionId id;
     private IngredientInfos ingredient;
     private String ppid;
     private Float unitsquantity;
     private Float ingredientquantity;
     private String ingredientunits;
     private String separator;
     private Float inbasq;
     private String inbasu;
     private Float inq2;
     private String inu2;
     private String dim;
     private String type;

    public CompositionInfos() {
    }

	
    public CompositionId getId() {
        return this.id;
    }
    
    public void setId(CompositionId id) {
        this.id = id;
    }
    public IngredientInfos getIngredient() {
        return this.ingredient;
    }
    
    public void setIngredient(IngredientInfos ingredient) {
        this.ingredient = ingredient;
    }
    public String getPpid() {
        return this.ppid;
    }
    
    public void setPpid(String ppid) {
        this.ppid = ppid;
    }
    public Float getUnitsquantity() {
        return this.unitsquantity;
    }
    
    public void setUnitsquantity(Float unitsquantity) {
        this.unitsquantity = unitsquantity;
    }
    public Float getIngredientquantity() {
        return this.ingredientquantity;
    }
    
    public void setIngredientquantity(Float ingredientquantity) {
        this.ingredientquantity = ingredientquantity;
    }
    public String getIngredientunits() {
        return this.ingredientunits;
    }
    
    public void setIngredientunits(String ingredientunits) {
        this.ingredientunits = ingredientunits;
    }
    public String getSeparator() {
        return this.separator;
    }
    
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    public Float getInbasq() {
        return this.inbasq;
    }
    
    public void setInbasq(Float inbasq) {
        this.inbasq = inbasq;
    }
    public String getInbasu() {
        return this.inbasu;
    }
    
    public void setInbasu(String inbasu) {
        this.inbasu = inbasu;
    }
    public Float getInq2() {
        return this.inq2;
    }
    
    public void setInq2(Float inq2) {
        this.inq2 = inq2;
    }
    public String getInu2() {
        return this.inu2;
    }
    
    public void setInu2(String inu2) {
        this.inu2 = inu2;
    }
    public String getDim() {
        return this.dim;
    }
    
    public void setDim(String dim) {
        this.dim = dim;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }



}
