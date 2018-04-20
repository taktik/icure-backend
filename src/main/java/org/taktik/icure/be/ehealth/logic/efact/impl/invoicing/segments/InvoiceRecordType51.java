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

package org.taktik.icure.be.ehealth.logic.efact.impl.invoicing.segments;

import java.util.LinkedHashMap;
import java.util.Map;

public class InvoiceRecordType51 extends InvoiceRecord {

	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(65);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1", "EnregistrementType", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "numero d'ordre de l'enregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "code nomenclature ou pseudo-code nomenclature", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5", "date prestation", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6a", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6b", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a,8b", "identification beneficiaire", "A", position, 13);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "13", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "identification du dispensateur", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "17", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "18", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "19", "signe + montant intervention de l'assurance", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "20", "reserve", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "21", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "22", "reserve", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "23", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "24", "reserve", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "25", "reserve", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "26", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "27", "ct1 + ct2", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "28", "reserve", "N", position, 25);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "29", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "30", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "31", "reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "32", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "33", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "34", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "35", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "36", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "37", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "38", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "39", "reserve", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "40", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "41", "reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "42", "donnees de reference reseau", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43a", "donnees de reference reseau", "N", position, 11);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43b", "donnees de reference reseau", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "44", "donnees de reference reseau", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "45", "donnees de reference reseau", "N", position, 26);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "46", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "47", "reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "48", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "49", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "50", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "51", "reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "52", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "53", "reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54a", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54b", "reserve", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "55", "date communication information", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "56", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "57", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "58", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "59", "reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "98", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "Chiffres de controle de l'enregistrement", "N", position, 2);
	}

	public InvoiceRecordType51() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "51");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
