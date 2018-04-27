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

public class InvoiceRecordType52 extends InvoiceRecord {

	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(19);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1", "Enregistrement de type 52", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "Numero d'ordre de l'enregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "Code nomenclature", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5", "Date de prestation", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6a,6b", "Date de lecture document identite electronique (1 et 2)", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a,8b", "Numero NISS du patient sauf en cas de convention internationale ou nouveaux-nes (1 et 2)", "A", position, 13);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "Type de support document identite electronique", "A", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "Type de lecture document identite electronique", "A", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12,13", "Heure de lecture document identite electronique (1 et 2)", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "Numero INAMI", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "reserve", "N", position, 269);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "Enregistrement chiffres de controle", "N", position, 2);
	}

	public InvoiceRecordType52() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "52");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
