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

package org.taktik.icure.services.external.rest.v1.dto.be.ehealth;

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
public class TransactionSummary implements Serializable {
    protected List<KmehrId> ids;
    protected List<KmehrCd> cds;
    protected Long date;
    protected Long time;
    protected Author author;
    protected boolean iscomplete;
    protected boolean isvalidated;
    protected Long recorddatetime;
    protected Long dateTime;
    protected String authorsList;
    protected String desc;

    public List<KmehrId> getIds() {
        return ids;
    }

    public void setIds(List<KmehrId> ids) {
        this.ids = ids;
    }

    public List<KmehrCd> getCds() {
        return cds;
    }

    public void setCds(List<KmehrCd> cds) {
        this.cds = cds;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean iscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
    }

    public boolean isvalidated() {
        return isvalidated;
    }

    public void setIsvalidated(boolean isvalidated) {
        this.isvalidated = isvalidated;
    }

    public Long getRecorddatetime() {
        return recorddatetime;
    }

    public void setRecorddatetime(Long recorddatetime) {
        this.recorddatetime = recorddatetime;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
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

    public void refreshAuthorsList() {
        if (author!=null && author.getHcparties().size()>0) {
            setAuthorsList(StringUtils.join(CollectionUtils.collect(author.getHcparties(), new Transformer<HcParty, String>() {
                @Override
                public String transform(HcParty hcpartyType) {
                    String title = "";
                    for (KmehrCd cd : hcpartyType.getCds()) {
                        if (cd.getS().equals(CDHCPARTYschemes.CD_HCPARTY.value())) {
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

            for (KmehrCd cd : cds) {
                if (cd.getS().equals(CDTRANSACTIONschemes.CD_TRANSACTION.value())) {
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
