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

package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.EnumVersion;

@EnumVersion(1l)
public enum DocumentType {
    admission,
    alert,
    bvt_sample,
    clinicalpath,
    clinicalsummary,
    contactreport,
    quote,
    invoice,
    death,
    discharge,
    dischargereport,
    ebirth_baby_medicalform,
    ebirth_baby_notification,
    ebirth_mother_medicalform,
    ebirth_mother_notification,
    ecare_safe_consultation,
    epidemiology,
    intervention,
    labrequest,
    labresult,
    medicaladvisoragreement,
    medicationschemeelement,
    note,
    notification,
    pharmaceuticalprescription,
    prescription,
    productdelivery,
    quickdischargereport,
    radiationexposuremonitoring,
    referral,
    report,
    request,
    result,
    sumehr,
    telemonitoring,
    template,
    template_admin,
    treatmentsuspension,
    vaccination;


    public static DocumentType fromName(String name){
        DocumentType[] tmpList = DocumentType.values();
        for(DocumentType tmpElem : tmpList){
            if(tmpElem.name().equals(name)){
                return tmpElem;
            }
        }
        return null;
    }
}
