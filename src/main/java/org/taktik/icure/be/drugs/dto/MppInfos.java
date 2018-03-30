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

package org.taktik.icure.be.drugs.dto;

import java.io.Serializable;


/**
 * Detailed informations about a MPP
 * @author abaudoux
 *
 */
public class MppInfos implements Serializable{

	private static final long serialVersionUID = 1L;
     private MppId id;
     private MpPreview mp;
     private GalInfos gal;
     private String copy;
     private String rrsstate;
     private String ouc;
     private String name;
     private Integer ranking;
     private String specifier;
     private Float contentquantity;
     private String contentunits;
     private Float contentfluidquantity;
     private String contentfluidunits;
     private Float contentquantitytoadd;
     private String contentunitstoadd;
     private Float contentfluidquantitytoadd;
     private String contentfluidunitstoadd;
     private String cmucomb;
     private String law;
     private String ssec;
     private Integer pubprice;
     private String use;
     private String note;
     private String pos;
     private String atcCode;
    
    public MppInfos() {
    }

   
    public MppId getId() {
        return this.id;
    }
    
    public void setId(MppId id) {
        this.id = id;
    }

    public String getCopy() {
        return this.copy;
    }
    
    public void setCopy(String copy) {
        this.copy = copy;
    }
    public String getRrsstate() {
        return this.rrsstate;
    }
    
    public void setRrsstate(String rrsstate) {
        this.rrsstate = rrsstate;
    }
    public String getOuc() {
        return this.ouc;
    }
    
    public void setOuc(String ouc) {
        this.ouc = ouc;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public Integer getRanking() {
        return this.ranking;
    }
    
    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
    public String getSpecifier() {
        return this.specifier;
    }
    
    public void setSpecifier(String specifier) {
        this.specifier = specifier;
    }
    public Float getContentquantity() {
        return this.contentquantity;
    }
    
    public void setContentquantity(Float contentquantity) {
        this.contentquantity = contentquantity;
    }
    public String getContentunits() {
        return this.contentunits;
    }
    
    public void setContentunits(String contentunits) {
        this.contentunits = contentunits;
    }
    public Float getContentfluidquantity() {
        return this.contentfluidquantity;
    }
    
    public void setContentfluidquantity(Float contentfluidquantity) {
        this.contentfluidquantity = contentfluidquantity;
    }
    public String getContentfluidunits() {
        return this.contentfluidunits;
    }
    
    public void setContentfluidunits(String contentfluidunits) {
        this.contentfluidunits = contentfluidunits;
    }
    public Float getContentquantitytoadd() {
        return this.contentquantitytoadd;
    }
    
    public void setContentquantitytoadd(Float contentquantitytoadd) {
        this.contentquantitytoadd = contentquantitytoadd;
    }
    public String getContentunitstoadd() {
        return this.contentunitstoadd;
    }
    
    public void setContentunitstoadd(String contentunitstoadd) {
        this.contentunitstoadd = contentunitstoadd;
    }
    public Float getContentfluidquantitytoadd() {
        return this.contentfluidquantitytoadd;
    }
    
    public void setContentfluidquantitytoadd(Float contentfluidquantitytoadd) {
        this.contentfluidquantitytoadd = contentfluidquantitytoadd;
    }
    public String getContentfluidunitstoadd() {
        return this.contentfluidunitstoadd;
    }
    
    public void setContentfluidunitstoadd(String contentfluidunitstoadd) {
        this.contentfluidunitstoadd = contentfluidunitstoadd;
    }
    public String getCmucomb() {
        return this.cmucomb;
    }
    
    public void setCmucomb(String cmucomb) {
        this.cmucomb = cmucomb;
    }
    public String getLaw() {
        return this.law;
    }
    
    public void setLaw(String law) {
        this.law = law;
    }
    public String getSsec() {
        return this.ssec;
    }
    
    public void setSsec(String ssec) {
        this.ssec = ssec;
    }
    public Integer getPubprice() {
        return this.pubprice;
    }
    
    public void setPubprice(Integer pubprice) {
        this.pubprice = pubprice;
    }
    public String getUse() {
        return this.use;
    }
    
    public void setUse(String use) {
        this.use = use;
    }
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    public String getPos() {
        return this.pos;
    }
    
    public void setPos(String pos) {
        this.pos = pos;
    }


	public MpPreview getMp() {
		return mp;
	}

    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }


	public void setMp(MpPreview mp) {
		this.mp = mp;
	}


	public GalInfos getGal() {
		return gal;
	}


	public void setGal(GalInfos gal) {
		this.gal = gal;
	}

}
