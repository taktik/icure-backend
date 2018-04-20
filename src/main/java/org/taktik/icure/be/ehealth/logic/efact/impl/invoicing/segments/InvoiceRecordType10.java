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

public class InvoiceRecordType10 extends InvoiceRecord {

	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(64);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1", "EnregistrementType", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "NumeroOrdreEnregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "NombreNumerosComptesFinanciers", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "VersionFichier", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5,6a", "NumeroCompteFinancierAPartie1et2", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6b", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "NumeroDeLenvoi", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a", "NumeroCompteFinancierB", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8b", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "CodeSuppressionFacturePapier", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "CodeFichierDeDecompte", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "13", "ContenuDeLaFacturation", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "NumeroTiersPayant", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "NumeroDaccreditationCin", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "17", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "18", "Reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "19", "Reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "20", "Reserve", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "21", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "22", "AnneeDeFacturation", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "23", "MoisDeFacturation", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "24", "Reserve", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "25,26", "DateDeCreationPartie1et2", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "27", "BCE", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "28", "ReferenceDeLetablissement", "A", position, 25);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "29", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "30", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "31,32,33,34", "BicCompteFinancierAPartie1_2_3et4", "A", position, 11);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "35", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "36, 37, 38, 39, 40, 41", "IbanCompteFinancierAPartie1_2_3_4_5et6", "A", position, 34);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "42", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43a", "BicCompteFinancierB", "A", position, 11);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43b", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "44", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "45", "Reserve", "N", position, 26);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "46", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "47", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "48", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "49, 50, 51, 52", "IbanCompteFinancierBPartie1_2_3et4", "A", position, 34);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "53", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "55", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "56", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "57", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "58", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "59", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "98", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "Chiffres de controle de l'enregistrement", "N", position, 2);
	}

	public InvoiceRecordType10() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "10");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
