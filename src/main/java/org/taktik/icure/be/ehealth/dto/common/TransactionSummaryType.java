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

package org.taktik.icure.be.ehealth.dto.common;

import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTY;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDHCPARTYschemes;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION;
import be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTIONschemes;
import be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR;
import be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType;
import be.fgov.ehealth.standards.kmehr.schema.v1.HcpartyType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 11/12/12
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class TransactionSummaryType implements Serializable {
    protected List<be.fgov.ehealth.standards.kmehr.id.v1.IDKMEHR> ids;
    protected List<be.fgov.ehealth.standards.kmehr.cd.v1.CDTRANSACTION> cds;
    protected Calendar date;
    protected Calendar time;
    protected be.fgov.ehealth.standards.kmehr.schema.v1.AuthorType author;
    protected boolean iscomplete;
    protected boolean isvalidated;
    protected Instant recorddatetime;
    protected Date dateTime;
    protected String authorsList;
    protected String desc;

    public List<IDKMEHR> getIds() {
        return ids;
    }

    public void setIds(List<IDKMEHR> ids) {
        this.ids = ids;
    }

    public List<CDTRANSACTION> getCds() {
        return cds;
    }

    public void setCds(List<CDTRANSACTION> cds) {
        this.cds = cds;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public AuthorType getAuthor() {
        return author;
    }

    public void setAuthor(AuthorType author) {
        this.author = author;
    }

    public boolean isIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
    }

    public boolean isIsvalidated() {
        return isvalidated;
    }

    public void setIsvalidated(boolean isvalidated) {
        this.isvalidated = isvalidated;
    }

    public Instant getRecorddatetime() {
        return recorddatetime;
    }

    public void setRecorddatetime(Instant recorddatetime) {
        this.recorddatetime = recorddatetime;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getAuthorsList() {
        return authorsList;
    }

    public void setAuthorsList(String authorsList) {
        this.authorsList = authorsList;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void refreshDateTime() {
        if (date != null) {
            Calendar c = (Calendar) date.clone();
            if (time != null) {
                c.set(Calendar.HOUR,time.get(Calendar.HOUR));
                c.set(Calendar.MINUTE,time.get(Calendar.MINUTE));
                c.set(Calendar.SECOND,time.get(Calendar.SECOND));
            }
            setDateTime(c.getTime());
        }
    }

    public void refreshAuthorsList() {
        if (author!=null && author.getHcparties().size()>0) {
            setAuthorsList(StringUtils.join(CollectionUtils.collect(author.getHcparties(), new Transformer<HcpartyType, String>() {
                @Override
                public String transform(HcpartyType hcpartyType) {
                    String title = "";
                    for (CDHCPARTY cd : hcpartyType.getCds()) {
                        if (cd.getS().equals(CDHCPARTYschemes.CD_HCPARTY)) {
                            title = cd.getValue();
                            break;
                        }
                    }
                    String name = hcpartyType.getName();
                    String familyname = hcpartyType.getFamilyname();
                    String firstname = hcpartyType.getFirstname();

                    StringBuilder sb = new StringBuilder();

                    if (!StringUtils.isEmpty(title)) {sb.append(title).append(" ");}
                    if (!StringUtils.isEmpty(name)) {sb.append(name).append(" ");}
                    if (!StringUtils.isEmpty(familyname)) {sb.append(familyname).append(" ");}
                    if (!StringUtils.isEmpty(firstname)) {sb.append(firstname).append(" ");}

                    return sb.toString();
                }
            }), ","));
        } else {
            setAuthorsList("");
        }
    }

    public void refreshDesc() {
        if (cds!=null && cds.size()>0) {
            String s = "";
            StringBuilder sb = new StringBuilder();

            for (CDTRANSACTION cd : cds) {
                if (cd.getS().equals(CDTRANSACTIONschemes.CD_TRANSACTION)) {
                    s = cd.getValue();
                } else {
                    sb.append(",").append(cd.getValue());
                }
            }
            setDesc(s+sb.toString());
        } else {
            setDesc("");
        }
    }


}
