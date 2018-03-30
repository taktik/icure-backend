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

package org.taktik.icure.be.format.logic.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.taktik.icure.be.format.logic.MultiFormatLogic;
import org.taktik.icure.be.format.logic.ResultFormatLogic;
import org.taktik.icure.dto.result.ResultInfo;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Document;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Patient;

@org.springframework.stereotype.Service
public class MultiFormatLogicImpl implements MultiFormatLogic {
	List<ResultFormatLogic> engines;

	public MultiFormatLogicImpl(List<ResultFormatLogic> engines) {
		this.engines = engines;
	}

	@Override
	public boolean canHandle(Document doc) throws IOException {
		for (ResultFormatLogic e:engines) {
			if (e.canHandle(doc)) { return true; }
		}
		return false;
	}

	@Override
	public List<ResultInfo> getInfos(Document doc) throws IOException {
		for (ResultFormatLogic e:engines) {
			if (e.canHandle(doc)) {
				List<ResultInfo> infos = e.getInfos(doc);
				infos.forEach(i->i.setEngine(e.getClass().getName()));
				return infos;
			}
		}
 		throw new IllegalArgumentException("Invalid format");
	}

	@Override
	public Contact doImport(String language, Document doc, String hcpId, List<String> protocolIds, List<String> formIds, String planOfActionId, Contact ctc) throws IOException {
		for (ResultFormatLogic e:engines) {
			if (e.canHandle(doc)) { return e.doImport(language, doc, hcpId, protocolIds, formIds, planOfActionId, ctc); }
		}
		throw new IllegalArgumentException("Invalid format");
	}

	@Override
	public void doExport(HealthcareParty sender, HealthcareParty recipient, Patient patient, LocalDateTime date, String ref, String text, OutputStream output) {
		throw new UnsupportedOperationException();
	}
}
