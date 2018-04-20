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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.taktik.icure.be.drugs.Informationresponsible;

public class MpFullInfos  implements java.io.Serializable,Comparable<MpFullInfos> {

	private static final long serialVersionUID = 1L;
     private MpId id;
     private Informationresponsible infoResp;
     private DocPreview relatedDoc;
     private String name;
     private String vaccinecode;
     private String pos;
     private String note;
     private String equiv;
     private String dopingcode;
     private String type;
     private Set<MpPreview> equivalences = new HashSet<MpPreview>(0);
     private SortedSet<MppFullInfos> mpps = new TreeSet<MppFullInfos>();

    public MpFullInfos() {
    }

    public MpId getId() {
        return this.id;
    }
    
    public void setId(MpId id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getVaccinecode() {
        return this.vaccinecode;
    }
    
    public void setVaccinecode(String vaccinecode) {
        this.vaccinecode = vaccinecode;
    }

    public String getPos() {
        return this.pos;
    }
    
    public void setPos(String pos) {
        this.pos = pos;
    }
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    public String getEquiv() {
        return this.equiv;
    }
    
    public void setEquiv(String equiv) {
        this.equiv = equiv;
    }
    public String getDopingcode() {
        return this.dopingcode;
    }
    
    public void setDopingcode(String dopingcode) {
        this.dopingcode = dopingcode;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    public SortedSet<MppFullInfos> getMpps() {
        return this.mpps;
    }
    
    public void setMpps(SortedSet<MppFullInfos> mpps) {
        this.mpps = mpps;
    }

	public Set<MpPreview> getEquivalences() {
		return equivalences;
	}

	public void setEquivalences(Set<MpPreview> equivalences) {
		this.equivalences = equivalences;
	}

	public int compareTo(MpFullInfos o) {
		return this.getName().compareTo(o.getName());
	}

	public Informationresponsible getInfoResp() {
		return infoResp;
	}

	public void setInfoResp(Informationresponsible infoResp) {
		this.infoResp = infoResp;
	}

	public DocPreview getRelatedDoc() {
		return relatedDoc;
	}

	public void setRelatedDoc(DocPreview relatedDoc) {
		this.relatedDoc = relatedDoc;
	}


}
