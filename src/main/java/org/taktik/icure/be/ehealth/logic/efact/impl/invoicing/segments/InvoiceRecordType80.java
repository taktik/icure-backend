/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
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

public class InvoiceRecordType80 extends InvoiceRecord {

	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(65);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1", "EnregistrementDeType80", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "NumeroDordreDeLenregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "HeureDadmission", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5", "DateDadmission", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6a,6b", "DateDeSortiePartie1et2", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "NumeroMutualiteDaffiliation", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a,8b", "IdentificationBeneficiairePartie1", "N", position, 13);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "SexeBeneficiaire", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "TypeFacture", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "13", "Service721Bis", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "NumeroDeLetablissementQuiFacture", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "SigneMontantDeCompteFinancierB", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "17", "CausesDuTraitement", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "18", "NumeroMutualiteDeDestination", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "19", "SigneMontantDeCompteFinancierA", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "20,21", "DateDeLaFacturePartie1et2", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "22", "HeureDeSortie", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "23", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "24,25", "NumeroDeLaFactureIndividuellePartie1et2", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "26", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "27", "SigneInterventionPersonnellePatient", "A", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "28", "ReferenceDeLetablissement", "A", position, 25);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "29", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "30,31", "Z27SigneMontantSupplementPartie1et2", "A", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "32", "FlagIdentificationDuBeneficiaire", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "33", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "34", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "35", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "36", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "37", "Reserve", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "38", "SigneAcompteNumeroCompteFinancierA", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "39", "Reserve", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "40", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "41", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "42", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43a", "Reserve", "N", position, 11);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43b", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "44", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "45", "Reserve", "N", position, 26);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "46", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "47", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "48", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "49", "Reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "50", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "51", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "52", "Reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "53", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54a,54b", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "55", "Reserve", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "56", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "57", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "58", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "59", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "98", "chiffres de controle de la facture", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "Chiffres de controle de l'enregistrement", "N", position, 2);
	}

	public InvoiceRecordType80() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "80");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
