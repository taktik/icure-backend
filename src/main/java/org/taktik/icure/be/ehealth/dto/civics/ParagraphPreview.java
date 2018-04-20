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

package org.taktik.icure.be.ehealth.dto.civics;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 10/06/13
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
public class ParagraphPreview implements Serializable {
    String chapterName;
    String paragraphName;
    String keyStringNl;
    String keyStringFr;
    Long paragraphVersion;
    private Long id;

    public Long getParagraphVersion() {
        return paragraphVersion;
    }

    public void setParagraphVersion(Long paragraphVersion) {
        this.paragraphVersion = paragraphVersion;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getParagraphName() {
        return paragraphName;
    }

    public void setParagraphName(String paragraphName) {
        this.paragraphName = paragraphName;
    }

    public String getKeyStringNl() {
        return keyStringNl;
    }

    public void setKeyStringNl(String keyStringNl) {
        this.keyStringNl = keyStringNl;
    }

    public String getKeyStringFr() {
        return keyStringFr;
    }

    public void setKeyStringFr(String keyStringFr) {
        this.keyStringFr = keyStringFr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
