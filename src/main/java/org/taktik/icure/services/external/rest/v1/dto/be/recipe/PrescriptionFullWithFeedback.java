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

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: aduchate
 * Date: 22/06/13
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class PrescriptionFullWithFeedback extends Prescription {
    String nihii;
    String patientName;
    List<String> medicines;
    Date deliverableFrom;
    Date deliverableTo;

    List<Feedback> feedbacks;
    private String fullAuthorName;

    public String getNihii() {
        return nihii;
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public List<String> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<String> medicines) {
        this.medicines = medicines;
    }

    public Date getDeliverableFrom() {
        return deliverableFrom;
    }

    public void setDeliverableFrom(Date deliverableFrom) {
        this.deliverableFrom = deliverableFrom;
    }

    public Date getDeliverableTo() {
        return deliverableTo;
    }

    public void setDeliverableTo(Date deliverableTo) {
        this.deliverableTo = deliverableTo;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
        /*return Arrays.asList(new Feedback("1234567890",1234567890l,new Date(),"This is a first feedback"),
                new Feedback("1234567890",1234567890l,new Date(),"This is a second feedback"));*/
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public void setFullAuthorName(String fullAuthorName) {
        this.fullAuthorName = fullAuthorName;
    }

    public String getFullAuthorName() {
        return fullAuthorName;
    }
}
