package org.taktik.icure.services.external.rest.v1.dto.embed;

import java.io.Serializable;
import java.util.Map;

public class MedicalHouseContractDto implements Serializable {

    private String mmNihii;
    private Long startOfContract; //yyyyMMdd
    private Long startOfCoverage; //yyyyMMdd
    private Long endOfContract; //yyyyMMdd
    private Long endOfCoverage; //yyyyMMdd
    private boolean kine;
    private boolean gp;
    private boolean nurse;
    private String hcpId;
    private Integer unsubscriptionReasonId;

    public String getMmNihii() {
        return mmNihii;
    }

    public void setMmNihii(String mmNihii) {
        this.mmNihii = mmNihii;
    }

    public Long getStartOfContract() {
        return startOfContract;
    }

    public void setStartOfContract(Long startOfContract) {
        this.startOfContract = startOfContract;
    }

    public Long getStartOfCoverage() {
        return startOfCoverage;
    }

    public void setStartOfCoverage(Long startOfCoverage) {
        this.startOfCoverage = startOfCoverage;
    }

    public Long getEndOfContract() {
        return endOfContract;
    }

    public void setEndOfContract(Long endOfContract) {
        this.endOfContract = endOfContract;
    }

    public Long getEndOfCoverage() {
        return endOfCoverage;
    }

    public void setEndOfCoverage(Long endOfCoverage) {
        this.endOfCoverage = endOfCoverage;
    }

    public boolean isKine() {
        return kine;
    }

    public void setKine(boolean kine) {
        this.kine = kine;
    }

    public boolean isGp() {
        return gp;
    }

    public void setGp(boolean gp) {
        this.gp = gp;
    }

    public boolean isNurse() {
        return nurse;
    }

    public void setNurse(boolean nurse) {
        this.nurse = nurse;
    }

    public String getHcpId() {
        return hcpId;
    }

    public void setHcpId(String hcpId) {
        this.hcpId = hcpId;
    }

    public Integer getUnsubscriptionReasonId() {
        return unsubscriptionReasonId;
    }

    public void setUnsubscriptionReasonId(Integer unsubscriptionReasonId) {
        this.unsubscriptionReasonId = unsubscriptionReasonId;
    }
}
