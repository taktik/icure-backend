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

public class InvoiceRecordType50 extends InvoiceRecord {

	private static final Map<String, ZoneDescription> ZONE_DESCRIPTIONS_BY_ZONE = new LinkedHashMap<>(65);

	private static int position = 1;

	static {
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "1", "EnregistrementDeType50", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "2", "NumeroDordreDeLenregistrement", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "3", "NormePrestationPourcentage", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "4", "CodeNomenclatureOuPseudoCodeNomenclature", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "5", "DatePremierePrestationEffectuee", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "6a,6b", "DateDernierePrestationEffectueePartie1et2", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "7", "NumeroMutualiteDaffiliation", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "8a,8b", "IdentificationBeneficiairePartie1et2", "A", position, 13);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "9", "SexeBeneficiaire", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "10", "Accouchement", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "11", "ReferenceNumeroDeCompteFinancier", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "12", "NuitWeekEndJourFerie", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "13", "CodeService", "N", position, 3);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "14", "LieuDePrestation", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "15", "IdentificationDuDispensateur", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "16", "NormeDispensateur", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "17,18", "PrestationRelativePartie1et2", "N", position, 7);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "19", "SigneMontantInterventionDeLassurance", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "20,21", "DatePrescriptionPartie1et2", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "22", "SigneNombreDunites", "A", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "23", "NombreDeCoupes", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "24,25", "IdentificationPrescripteurPartie1et2", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "26", "NormePrescripteur", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "27", "Z27SigneInterventionPersonnellePatient", "A", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "28", "ReferenceDeLetablissement", "A", position, 25);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "29", "DentTraitee", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "30,31", "SigneMontantSupplementPartie1et2", "A", position, 10);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "32", "ExceptionTiersPayant", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "33", "CodeFacturationInterventionPersonnelleOuSupplement", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "34", "MembreTraite", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "35", "PrestataireConventionne", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "36,37", "HeureDePrestationPartie1et2", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "38", "IdentificationAdministrateurDuSang", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "39,40", "NumeroDeLattestationDadministrationPartie1et2", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "41,42", "NumeroBonDeDelivranceOuSacPartie1et2", "A", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "43a,43b", "CodeImplantPartie1", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "44,45", "LibelleDuProduitPartie1et2", "A", position, 30);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "46", "NormePlafond", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "47", "DateAccordPrestation", "N", position, 8);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "48", "Transplantation", "N", position, 1);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "49", "identification de l'aide soignant", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "50", "Reserve", "N", position, 4);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "51", "SiteHospitalier", "N", position, 6);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "52", "IdentificationAssociationBassinDeSoins", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "53,54a", "numero de course (partie 1 et 2)", "A", position, 11);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "54b", "Reserve", "N", position, 5);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "55,56", "CodeNotificationImplantPartie1et2", "N", position, 12);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "57,58,59", "code d'enregistrement Qermid (partie 1, 2 et 3)", "N", position, 14);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "98", "reserve", "N", position, 2);
		position = register(ZONE_DESCRIPTIONS_BY_ZONE, "99", "chiffres de controle de l'enregistrement", "N", position, 2);
	}

	public InvoiceRecordType50() {
		append(ZONE_DESCRIPTIONS_BY_ZONE.get("1"), "50");
	}

	@Override
	public Map<String, ZoneDescription> getZoneDescriptionsByZone() {
		return new LinkedHashMap<>(ZONE_DESCRIPTIONS_BY_ZONE);
	}
}
