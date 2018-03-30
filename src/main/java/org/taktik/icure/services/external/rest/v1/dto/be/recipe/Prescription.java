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

package org.taktik.icure.services.external.rest.v1.dto.be.recipe;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 18/06/13
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class Prescription  implements Serializable {
    protected Date creationDate;
    protected String encryptionKeyId;
    protected boolean feedbackAllowed;
    protected Long patientId;
    protected String rid;
    //protected byte[] prescription;

    Boolean notificationWasSent;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(String encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

    public boolean isFeedbackAllowed() {
        return feedbackAllowed;
    }

    public void setFeedbackAllowed(boolean feedbackAllowed) {
        this.feedbackAllowed = feedbackAllowed;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }


    public Boolean getNotificationWasSent() {
        return notificationWasSent;
    }

    public void setNotificationWasSent(Boolean notificationWasSent) {
        this.notificationWasSent = notificationWasSent;
    }
}
