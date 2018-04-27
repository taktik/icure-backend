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
 * Created by aduchate on 14/07/11, 08:50
 */
public class IamFullInfos implements Serializable {
    private IamId iamId;
     private String atc1;
     private String atc2;
     private String description;
     private String management;
     private String type;
     private MppInfos mppInfos;

    public IamFullInfos() {
    }

    public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public String getManagement() {
         return management;
     }

     public void setManagement(String management) {
         this.management = management;
     }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }

     public String getAtc2() {
         return atc2;
     }

     public void setAtc2(String atc2) {
         this.atc2 = atc2;
     }

     public String getAtc1() {
         return atc1;
     }

     public void setAtc1(String atc1) {
         this.atc1 = atc1;
     }

     public IamId getIamId() {
         return iamId;
     }

     public void setIamId(IamId iamId) {
         this.iamId = iamId;
     }

    public void setMppInfos(MppInfos infos) {
        this.mppInfos = infos;
    }

    public MppInfos getMppInfos() {
        return mppInfos;
    }
}
