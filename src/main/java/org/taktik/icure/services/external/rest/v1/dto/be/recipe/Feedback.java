/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.dto.be.recipe;

import java.io.Serializable;
import java.util.Date;

public class Feedback implements Serializable, Comparable<Feedback> {
    protected String rid;
    protected Long sentBy;
    protected Date sentDate;
    protected String textContent;

    public Feedback() {
    }

    public Feedback(String rid, Long sentBy, Date sentDate, String textContent) {
        this.rid = rid;
        this.sentBy = sentBy;
        this.sentDate = sentDate;
        this.textContent = textContent;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Long getSentBy() {
        return sentBy;
    }

    public void setSentBy(Long sentBy) {
        this.sentBy = sentBy;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @Override
    public int compareTo(Feedback feedback) {
        if (sentDate==null) {return 1;}
        return sentDate.compareTo(feedback.sentDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feedback feedback = (Feedback) o;

        if (textContent != null ? !textContent.equals(feedback.textContent) : feedback.textContent != null) return false;
        if (rid != null ? !rid.equals(feedback.rid) : feedback.rid != null) return false;
        if (sentBy != null ? !sentBy.equals(feedback.sentBy) : feedback.sentBy != null) return false;
        if (sentDate != null ? !sentDate.equals(feedback.sentDate) : feedback.sentDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rid != null ? rid.hashCode() : 0;
        result = 31 * result + (sentBy != null ? sentBy.hashCode() : 0);
        result = 31 * result + (sentDate != null ? sentDate.hashCode() : 0);
        result = 31 * result + (textContent != null ? textContent.hashCode() : 0);
        return result;
    }
}
