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
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class DocExtendedInfos implements Serializable {

	private static final long serialVersionUID = 1L;
     private DocId id;
     private String hierarchy;
     private Integer docindex;
     private Boolean mpgrp;
     private String title;
     private String content;
     private String pos;
     private String type;
     private DocPreview parent;
     private DocPreview next;
     private DocPreview previous;
     private SortedSet<MpExtendedInfos> mps = new TreeSet<MpExtendedInfos>();
     
     private List<DocPreview> children;
     
     private List<DocExtendedInfos> mpGroups;

    public List<DocExtendedInfos> getMpGroups() {
		return mpGroups;
	}


	public void setMpGroups(List<DocExtendedInfos> mpGroups) {
		this.mpGroups = mpGroups;
	}


	public DocExtendedInfos() {
    }
    

    public DocId getId() {
        return this.id;
    }
    
    public void setId(DocId id) {
        this.id = id;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }
    
    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }
    public Integer getDocindex() {
        return this.docindex;
    }
    
    public void setDocindex(Integer docindex) {
        this.docindex = docindex;
    }
    public Boolean getMpgrp() {
        return this.mpgrp;
    }
    
    public void setMpgrp(Boolean mpgrp) {
        this.mpgrp = mpgrp;
    }
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    public String getPos() {
        return this.pos;
    }
    
    public void setPos(String pos) {
        this.pos = pos;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }


	public DocPreview getParent() {
		return parent;
	}

	public void setParent(DocPreview parent) {
		this.parent = parent;
	}

	public DocPreview getNext() {
		return next;
	}

	public void setNext(DocPreview next) {
		this.next = next;
	}

	public DocPreview getPrevious() {
		return previous;
	}

	public void setPrevious(DocPreview previous) {
		this.previous = previous;
	}

	public SortedSet<MpExtendedInfos> getMps() {
		return mps;
	}

	public void setMps(SortedSet<MpExtendedInfos> mps) {
		this.mps = mps;
	}

	public List<DocPreview> getChildren() {
		return children;
	}

	public void setChildren(List<DocPreview> children) {
		this.children = children;
	}

}
