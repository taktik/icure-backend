package org.taktik.icure.entities.embed;

public class MedicalHouseContract {

	private Long validFrom; //yyyyMMdd : start of contract period
	private Long validTo; //yyyyMMdd : end of contract period
	private ContractChangeType contractChangeType; //inscription, inscription end, suspension, coverageChange
	private String changedBy; //user, mcn
	private String mmNihii;
	private Long startOfContract; //yyyyMMdd : signdate
	private Long startOfCoverage; //yyyyMMdd
	private Long endOfContract; //yyyyMMdd : signdate
	private Long endOfCoverage; //yyyyMMdd
	private boolean kine;
	private boolean gp;
	private boolean nurse;
	private boolean noKine;
	private boolean noGp;
	private boolean noNurse;
	private Long startOfSuspension; //yyyyMMdd
	private Long endOfSuspension; //yyyyMMdd
	private SuspensionReason suspensionReason;
	private String suspensionSource;
	private boolean forcedSuspension; //no automatic unSuspension

	public Long getValidFrom() { return validFrom; }

	public void setValidFrom(Long validFrom) { this.validFrom = validFrom; }

	public Long getValidTo() { return validTo; }

	public void setValidTo(Long validTo) { this.validTo = validTo; }

	public ContractChangeType getChangeType() { return contractChangeType; }

	public void setChangeType(ContractChangeType contractChangeType) { this.contractChangeType = contractChangeType; }

	public String getChangedBy() { return changedBy; }

	public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

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

	public String getSuspensionSource() { return suspensionSource; }

	public void setSuspensionSource(String suspensionSource) { this.suspensionSource = suspensionSource; }

	public Long getStartOfSuspension() { return startOfSuspension; }

	public void setStartOfSuspension(Long startOfSuspension) { this.startOfSuspension = startOfSuspension; }

	public Long getEndOfSuspension() { return endOfSuspension; }

	public void setEndOfSuspension(Long endOfSuspension) { this.endOfSuspension = endOfSuspension; }

	public SuspensionReason getSuspensionReason() { return suspensionReason; }

	public void setSuspensionReason(SuspensionReason suspensionReason) { this.suspensionReason = suspensionReason; }

	public boolean isForcedSuspension() { return forcedSuspension; }

	public void setForcedSuspension(boolean forcedSuspension) { this.forcedSuspension = forcedSuspension; }

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

	public boolean isNoKine() {
		return noKine;
	}

	public void setNoKine(boolean noKine) {
		this.noKine = noKine;
	}

	public boolean isNoGp() {
		return noGp;
	}

	public void setNoGp(boolean noGp) {
		this.noGp = noGp;
	}

	public boolean isNoNurse() {
		return noNurse;
	}

	public void setNoNurse(boolean noNurse) { this.noNurse = noNurse; }

}
