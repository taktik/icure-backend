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

/**
 * Infos about a Galenic form
 * @author abaudoux
 *
 */
public class GalInfos implements Serializable {
	private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private Boolean deleted;

   public GalInfos() {
   }
   
   public String getId() {
       return this.id;
   }
   
   public void setId(String id) {
       this.id = id;
   }
   public String getName() {
       return this.name;
   }
   
   public void setName(String name) {
       this.name = name;
   }
   public Boolean getDeleted() {
       return this.deleted;
   }
   
   public void setDeleted(Boolean deleted) {
       this.deleted = deleted;
   }

}
