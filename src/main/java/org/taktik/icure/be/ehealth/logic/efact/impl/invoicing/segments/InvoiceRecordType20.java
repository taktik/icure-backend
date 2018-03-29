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

public class InvoiceRecordType20 extends InvoiceRecord {
	
	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(65);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1,", "EnregistrementDeType20", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "NumeroDordreDeLenregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "AutorisationTiersPayant", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "HeureDadmission", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5", "DateDadmission", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6a", "DateDeSortiePartie1", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6b", "DateDeSortiePartie2", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "NumeroMutualiteDaffiliation", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a,8b", "IdentificationBeneficiairePartie1et2", "N", position, 13);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "SexeBeneficiaire", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "TypeFacture", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "TypeDeFacturation", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "13", "Service721Bis", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "NumeroDeLetablissementQuiFacture", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "EtablissementDeSejour", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "CodeLeveeDelaiDePrescription", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "17", "CausesDuTraitement", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "18", "NumeroMutualiteDeDestination", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "19", "NumeroDadmission", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "20,21", "DateAccordTraitementPartie1et2", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "22", "HeureDeSortie", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "23", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "24,25", "NumeroDeLaFactureIndividuellePartie1et2", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "26", "ApplicationFranchiseSociale", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "27", "Ct1Ct2", "N", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "28", "ReferenceDeLetablissement", "A", position, 25);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "29,30,31", "NumeroDeFacturePrecedentePartie1_2et3", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "32", "FlagIdentificationDuBeneficiaire", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "33", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "34,35,36", "NumeroEnvoiPrecedentPartie1_2et3", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "37", "NumeroMutualiteFacturationPrecedente", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "38,39", "ReferenceMutualiteNumeroDeCompteFinancierAPartie1et2", "A", position, 22);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "40", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "41", "AnneeEtMoisDeFacturationPrecedente", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "42,43a,43b,44,45", "DonneesDeReferenceReseauOuCarteSisPartie1_2_3_4et5", "A", position, 48);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "46", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "47", "Date de facturation", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "48", "Reserve", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "49,50,51", "ReferenceMutualiteNumeroCompteFinancierBPartie1_2et3", "A", position, 22);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "52", "Reserve", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "53", "DateDebutAssurabilite", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54a,54b", "DateFinAssurabilite", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "55", "DateCommunicationInformation", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "56", "MafAnneeEnCours", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "57", "MafAnneeEnCours1", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "58", "MafAnneeEnCours2", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "59", "Reserve", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "98", "Reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "Chiffres de controle de l'enregistrement", "N", position, 2);
	}

	public InvoiceRecordType20() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "20");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
