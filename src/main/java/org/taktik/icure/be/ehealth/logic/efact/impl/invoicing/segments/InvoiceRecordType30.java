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

public class InvoiceRecordType30 extends InvoiceRecord {

	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(65);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1", "EnregistrementType", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "numero d'ordre de l'enregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "norme journee d'entretien", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "pseudo-code journee d'entretien et forfait", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5", "date premier jour facture", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6a,6b", "date dernier jour facture (partie 1 et 2)", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "numero mutualite d'affiliation", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a,8b", "identification beneficiaire (partie 1 et 2)", "A", position, 13);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "sexe beneficiaire", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "accouchement", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "reference numero de compte financier", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "13", "code service", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "lieu de prestation", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "identification convention/etablissement de sejour", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "17,18", "prestation relative (partie 1 et 2)", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "19", "signe + montant intervention de l'assurance", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "20", "reserve", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "21", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "22", "signe + nombre de jours ou forfaits", "A", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "23", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "24,25", "signe + montant indicatif ordre de grandeur frais de sejour (partie 1 et 2)", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "26", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "27", "signe + intervention personnelle patient", "A", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "28", "reference de l'etablissement", "A", position, 25);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "29", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "30,31", "signe + montant supplement (partie 1 et 2)", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "32", "exception tiers payant", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "33", "code facturation intervention personnelle ou supplement", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "34", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "35", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "36", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "37", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "38", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "39", "reserve", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "40", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "41", "reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "42", "reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43a", "reserve", "N", position, 11);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43b", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "44", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "45", "reserve", "N", position, 26);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "46", "reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "47", "date accord prestation", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "48", "transplantation", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "49", "reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "50", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "51", "site hospitalier", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "52", "identification association bassin de soins", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "53", "reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54a", "reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54b", "reserve", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "55", "reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "56", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "57", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "58", "reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "59", "reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "98", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "Chiffres de controle de l'enregistrement", "N", position, 2);
	}

	public InvoiceRecordType30() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "30");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
