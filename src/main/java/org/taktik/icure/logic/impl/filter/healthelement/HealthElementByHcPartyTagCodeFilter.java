package org.taktik.icure.logic.impl.filter.healthelement;

import org.springframework.beans.factory.annotation.Autowired;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.User;
import org.taktik.icure.logic.HealthElementLogic;
import org.taktik.icure.logic.ICureSessionLogic;
import org.taktik.icure.logic.impl.filter.Filter;
import org.taktik.icure.logic.impl.filter.Filters;

import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;

public class HealthElementByHcPartyTagCodeFilter implements Filter<String, HealthElement, org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter> {
	HealthElementLogic healthElementLogic;
	ICureSessionLogic sessionLogic;

	@Autowired
	public void setHealthElementLogic(HealthElementLogic healthElementLogic) {
		this.healthElementLogic = healthElementLogic;
	}

	@Autowired
	public void setSessionLogic(ICureSessionLogic sessionLogic) {
		this.sessionLogic = sessionLogic;
	}

	@Override
	public Set<String> resolve(org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter filter, Filters context) {
		try {
			String hcPartyId = filter.getHealthCarePartyId() != null ? filter.getHealthCarePartyId() : getLoggedHealthCarePartyId();
			HashSet<String> ids = null;
			if (filter.getTagType() != null && filter.getTagCode() != null) {
				ids = new HashSet<>(healthElementLogic.findByHCPartyAndTags(hcPartyId, filter.getTagType(), filter.getTagCode()));
				;
			}

            if (filter.getCodeType() != null && filter.getCodeNumber() != null) {
				HashSet<String> byCode = new HashSet<>(healthElementLogic.findByHCPartyAndCodes(hcPartyId, filter.getCodeType(), filter.getCodeNumber()));
				if (ids == null) {
					ids = byCode;
				} else {
					ids.retainAll(byCode);
				}
			}

			if (filter.getStatus() != null) {
				HashSet<String> byStatus = new HashSet<>(healthElementLogic.findByHCPartyAndStatus(hcPartyId, filter.getStatus()));

				if (ids == null) {
					ids = byStatus;
				} else {
					ids.retainAll(byStatus);
				}
			}

			return ids != null ? ids : new HashSet<>();
		} catch (LoginException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private String getLoggedHealthCarePartyId() throws LoginException {
		User user = sessionLogic.getCurrentSessionContext().getUser();
		if (user == null || user.getHealthcarePartyId() == null) {
			throw new LoginException("You must be logged to perform this action. ");
		}
		return user.getHealthcarePartyId();
	}
}
