package org.taktik.icure.db.be.icure

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.google.common.collect.Sets
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ektorp.CouchDbInstance
import org.ektorp.DbAccessException
import org.ektorp.DocumentNotFoundException
import org.ektorp.ViewQuery
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.slf4j.LoggerFactory
import org.taktik.icure.db.Importer
import org.taktik.icure.entities.Tarification
import org.taktik.icure.entities.embed.LetterValue
import org.taktik.icure.entities.embed.Valorisation
import org.taktik.icure.utils.FuzzyValues

import java.security.Security
import java.text.Normalizer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TarificationCodeImporter extends Importer {
	def language = 'fr'
	def refs = [
		"a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_sans_regime_preferentiel"                                                                                                                  : "old_no_preferentialstatus_dmg,dmg_no_preferentialstatus_chronical",
		"_"                                                                                                                                                                                                              : "any",
		"_prix_sur_facture"                                                                                                                                                                                              : "any",
		""                                                                                                                                                                                                               : "any",
		"a_l_acte"                                                                                                                                                                                                       : "any",
		"base_de_remboursement"                                                                                                                                                                                          : "any",
		"montant_fixe_mensuel_pour_les_maisons_medicales"                                                                                                                                                                : "any",
		"forfait_partiel_125_code_prestation_relative_0081255_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0051251_pour_seances_depassant_la_capacite_normale_de_facturation"                     : "any",
		"forfait_partiel_133_code_prestation_relative_0081336_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0051332_pour_seances_depassant_la_capacite_normale_de_facturation"                     : "any",
		"forfait_partiel_150_code_prestation_relative_0081502_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0051505_pour_seances_depassant_la_capacite_normale_de_facturation"                     : "any",
		"forfait_partiel_166_code_prestation_relative_0081664_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0051660_pour_seances_depassant_la_capacite_normale_de_facturation"                     : "any",
		"forfait_partiel_175_code_prestation_relative_0081756_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0051752_pour_seances_depassant_la_capacite_normale_de_facturation"                     : "any",
		"forfait_partiel_200_code_prestation_relative_0082003_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0052006_pour_seances_depassant_la_capacite_normale_de_facturation"                     : "any",
		"forfait_partiel_25_code_prestation_relative_0080253_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0050256_pour_seances_depassant_la_capacite_normale_de_facturation"                      : "any",
		"forfait_partiel_33_code_prestation_relative_0080334_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0050330_pour_seances_depassant_la_capacite_normale_de_facturation"                      : "any",
		"forfait_partiel_50_code_prestation_relative_0080500_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0050503_pour_seances_depassant_la_capacite_normale_de_facturation"                      : "any",
		"forfait_partiel_66_code_prestation_relative_0080662_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0050665_pour_seances_depassant_la_capacite_normale_de_facturation"                      : "any",
		"forfait_partiel_75_code_prestation_relative_0080754_pour_seances_jusque_et_y_compris_la_capacite_normale_de_facturation_0050750_pour_seances_depassant_la_capacite_normale_de_facturation"                      : "any",
		"maximale"                                                                                                                                                                                                       : "any",
		"prix"                                                                                                                                                                                                           : "any",
		"seulement_les_centres_agrees_pour_groupe_cible_15_peuvent_facturer_ce_numero_de_code"                                                                                                                           : "any",
		"seulement_les_centres_agrees_pour_groupe_cible_16_peuvent_facturer_ce_numero_de_code"                                                                                                                           : "any",
		"seulement_les_centres_agrees_pour_groupe_cible_17_peuvent_facturer_ce_numero_de_code"                                                                                                                           : "any",
		"seulement_les_centres_agrees_pour_groupe_cible_18_peuvent_facturer_ce_numero_de_code"                                                                                                                           : "any",
		"seulement_les_centres_agrees_pour_groupe_cible_19_peuvent_facturer_ce_numero_de_code"                                                                                                                           : "any",
		"seulement_les_centres_agrees_pour_groupe_cible_1bis_peuvent_facturer_ce_numero_de_code"                                                                                                                         : "any",
		"voir_liste_9_53_et_9_65_centres_annexe_du_circulaire_aux_organismes_assureurs_"                                                                                                                                 : "any",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_medecin_stagiaire_avec_regime_preferentiel"                                                                           : "trainee_preferentialstatus_regular_dmg_preferentialstatus,trainee_preferentialstatus_old_no_dmg_preferentialstatus,trainee_preferentialstatus_chronical_no_dmg_preferentialstatus",
		"a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli_avec_regime_preferentiel"                                         : "trainee_old_dmg_preferentialstatus,trainee_chronical_dmg_referentialstatus",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_medecin_stagiaire_sans_regime_preferentiel"                                                                           : "trainee_no_preferentialstatus_regular_dmg,trainee_no_preferentialstatus_old_no_dmg,trainee_no_preferentialstatus_chronical_no_dmg",
		"a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli_sans_regime_preferentiel"                                         : "trainee_no_preferentialstatus_old_dmg,trainee_no_preferentialstatus_chronical_dmg",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_medecin_stagiaire_avec_regime_preferentiel"                                                                 : "trainee_regular_dmg_preferentialstatus,trainee_old_preferentialstatus,trainee_chronical_preferentialstatus",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_medecin_stagiaire_sans_regime_preferentiel"                                                                 : "trainee_regular_dmg_preferentialstatus,trainee_old_preferentialstatus,trainee_chronical_preferentialstatus",
		"avec_regime_preferentiel_a_taux_exceptionnel"                                                                                                                                                                   : "preferentialstatus",
		"sans_regime_preferentiel_a_taux_exceptionnel"                                                                                                                                                                   : "no_preferentialstatus",
		"avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg"                                                                                                                                   : "child-120m_preferentialstatus",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_avec_regime_preferentiel"                                                                                                                                   : "child-120m_preferentialstatus",
		"sans_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"       : "child-120m_no_preferentialstatus,regular_no_preferentialstatus_dmg,old_no_preferentialstatus_no_dmg,chronical_no_preferentialstatus_no_dmg",
		"sans_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg": "child-120m_no_preferentialstatus,regular_no_preferentialstatus_dmg,old_no_preferentialstatus,chronical_no_preferentialstatus",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_sans_regime_preferentiel"                                                                                                                                   : "child-120m_no_preferentialstatus",
		"sans_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg"                                                                                                                                   : "child-120m_no_preferentialstatus",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                                : "child-120m,regular_dmg,old_no_dmg,chronical_no_dmg",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_ou_sans_dmg_malade_chronique_avec_ou_sans_dmg"                : "child-120m,regular_dmg,old,chronical",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dossier_medical_global"                                                                                                                                         : "child-120m",
		"jusqu_au_18eme_anniversaire_sans_regime_preferentiel"                                                                                                                                                           : "child-18y_no_preferentialstatus",
		"jusqu_au_18eme_anniversaire_avec_regime_preferentiel"                                                                                                                                                           : "child-18y_preferentialstatus",
		"jusqu_au_18eme_anniversaire"                                                                                                                                                                                    : "child-18y",
		"avec_regime_preferentiel_prestataire_conventionne_cabinet"                                                                                                                                                      : "preferentialstatus_convention",
		"avec_regime_preferentiel_prestataire_non_conventionne_cabinet"                                                                                                                                                  : "preferentialstatus_no_convention",
		"sans_regime_preferentiel_prestataire_conventionne_cabinet"                                                                                                                                                      : "no_preferentialstatus_convention",
		"sans_regime_preferentiel_prestataire_non_conventionne_cabinet"                                                                                                                                                  : "no_preferentialstatus_no_convention",
		"enfant_de_moins_de_5_ans_sans_regime_preferentiel"                                                                                                                                                              : "child-60m_no_preferentialstatus",
		"enfant_de_moins_de_5_ans"                                                                                                                                                                                       : "child-60m",
		"chez_les_nouveau_nes_et_les_nourrissons_de_moins_de_6_mois"                                                                                                                                                     : "child-6m",
		"enfant_de_moins_de_7_ans_sans_regime_preferentiel"                                                                                                                                                              : "child-84m_no_preferentialstatus",
		"enfant_de_moins_de_7_ans"                                                                                                                                                                                       : "child-84m",
		"_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dossier_medical_global"                                                                                                                                        : "child",
		"sans_regime_preferentiel_dans_le_cadre_du_dmg"                                                                                                                                                                  : "dmg_no_preferentialstatus",
		"avec_regime_preferentiel_dans_le_cadre_du_dmg"                                                                                                                                                                  : "dmg_preferentialstatus",
		"dans_le_cadre_du_dossier_medical_global"                                                                                                                                                                        : "dmg",
		"a_partir_du_18eme_anniversaire_sans_regime_preferentiel"                                                                                                                                                        : "major_no_preferentialstatus",
		"a_partir_du_18eme_anniversaire_avec_regime_preferentiel"                                                                                                                                                        : "major_preferentialstatus",
		"a_partir_du_18eme_anniversaire"                                                                                                                                                                                 : "major",
		"chez_les_nouveau_nes_et_les_nourrissons_de_moins_de_6_mois_sans_regime_preferentiel"                                                                                                                            : "no_preferentialstatus_child-6m",
		"ntervention_sans_regime_preferentiel_prestataire_conventionne_a_taux_exceptionnel"                                                                                                                              : "no_preferentialstatus_convention",
		"sans_regime_preferentiel_prestataire_conventionne_montant_arrondi_par_unite"                                                                                                                                    : "no_preferentialstatus_convention",
		"sans_regime_preferentiel_prestataire_conventionne_montant_non_arrondi_par_unite"                                                                                                                                : "no_preferentialstatus_convention",
		"sans_regime_preferentiel_prestataire_conventionne"                                                                                                                                                              : "no_preferentialstatus_convention",
		"sans_regime_preferentiel_prestataire_non_conventionne_a_taux_exceptionel"                                                                                                                                       : "no_preferentialstatus_no_convention",
		"sans_regime_preferentiel_prestataire_non_conventionne_montant_arrondi_par_unite"                                                                                                                                : "no_preferentialstatus_no_convention",
		"sans_regime_preferentiel_prestataire_non_conventionne"                                                                                                                                                          : "no_preferentialstatus_no_convention",
		"a_taux_exceptionnel_sans_regime_preferentiel"                                                                                                                                                                   : "no_preferentialstatus",
		"patient_palliatif_sans_regime_preferentiel"                                                                                                                                                                     : "no_preferentialstatus",
		"sans_regime_preferentiel_a_50_"                                                                                                                                                                                 : "no_preferentialstatus",
		"sans_regime_preferentiel_a_75_"                                                                                                                                                                                 : "no_preferentialstatus",
		"sans_regime_preferentiel_a_88_"                                                                                                                                                                                 : "no_preferentialstatus",
		"sans_regime_preferentiel_fonction_agreee_de_soins_intensifs"                                                                                                                                                    : "no_preferentialstatus",
		"sans_regime_preferentiel_pas_de_prestations_dans_l_annee_calendrier_precedente"                                                                                                                                 : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_anesthesie_pratiquee_au_cours_d_une_prestation_dont_la_valeur_relative_est_egale_ou_superieure_a_k_400_n_600_ou_i_600"                                                            : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_anesthesie_pratiquee_au_cours_d_une_prestation_dont_la_valeur_relative_est_superieure_a_k_75_n_125_ou_i_125_et_inferieure_a_k_400_n_600_ou_i_600"                                 : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prestation_dans_categorie_i_600"                                                                                                                                                  : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prestation_dans_categorie_k_180_ou_n_300_et_k_400_ou_n_600"                                                                                                                       : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prestation_dans_categorie_k_300_ou_n500_et_k_390_ou_n_600"                                                                                                                        : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prestation_dans_categorie_k_75_et_k_400"                                                                                                                                          : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prestation_dans_categorie_k400_ou_n_600"                                                                                                                                          : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prestation_dans_categorie_n_600_ou_i_600"                                                                                                                                         : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prothese_amovible_de_1_a_5_dents"                                                                                                                                                 : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prothese_amovible_de_10_ou_11_dents"                                                                                                                                              : "no_preferentialstatus",
		"sans_regime_preferentiel_pour_prothese_amovible_de_12_dents_et_plus"                                                                                                                                            : "no_preferentialstatus",
		"sans_regime_preferentiel_prix_sur_facture"                                                                                                                                                                      : "no_preferentialstatus",
		"sans_regime_preferentiel_si_les_conditions_ne_sont_pas_respectees"                                                                                                                                              : "no_preferentialstatus",
		"sans_regime_preferentiel_sur_base_du_paragraphe_1_de_l_article_7"                                                                                                                                               : "no_preferentialstatus",
		"sans_regime_preferentiel_sur_base_du_paragraphe_2_de_l_article_7"                                                                                                                                               : "no_preferentialstatus",
		"sans_regime_preferentiel_sur_base_du_paragraphe_3_de_l_article_7"                                                                                                                                               : "no_preferentialstatus",
		"sans_regime_preferentiel"                                                                                                                                                                                       : "no_preferentialstatus",
		"sans_regime_preferentiel_a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg"                                                                                                                  : "old_dmg_no_preferentialstatus,chronical_no_preferentialstatus_dmg",
		"_a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg"                                                                                                                                          : "old_dmg,chronical_dmg",
		"a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg"                                                                                                                                           : "old_dmg,chronical_dmg",
		"avec_regime_preferentiel_a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg"                                                                                                                  : "old_preferentialstatus_dmg,chronical_preferentialstatus_dmg",
		"avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"       : "preferentialstatus_child-120m,preferentialstatus_regular_dmg,preferentialstatus_regular_dmg,preferentialstatus_old_no_dmg,preferentialstatus_chronical_no_dmg",
		"avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg": "preferentialstatus_child-120m,preferentialstatus_regular_dmg,preferentialstatus_regular_dmg,preferentialstatus_old,preferentialstatus_chronical",
		"enfant_de_moins_de_5_ans_avec_regime_preferentiel"                                                                                                                                                              : "preferentialstatus_child-60m",
		"chez_les_nouveau_nes_et_les_nourrissons_de_moins_de_6_mois_avec_regime_preferentiel"                                                                                                                            : "preferentialstatus_child-6m",
		"enfant_de_moins_de_7_ans_avec_regime_preferentiel"                                                                                                                                                              : "preferentialstatus_child-84m",
		"avec_regime_preferentiel_prestataire_conventionne_a_taux_exceptionnel"                                                                                                                                          : "preferentialstatus_convention",
		"avec_regime_preferentiel_prestataire_conventionne_montant_arrondi_par_unite"                                                                                                                                    : "preferentialstatus_convention",
		"avec_regime_preferentiel_prestataire_conventionne_montant_non_arrondi_par_unite"                                                                                                                                : "preferentialstatus_convention",
		"avec_regime_preferentiel_prestataire_conventionne"                                                                                                                                                              : "preferentialstatus_convention",
		"avec_regime_preferentiel_prestataire_non_conventionne_a_taux_exceptionnel"                                                                                                                                      : "preferentialstatus_no_convention",
		"avec_regime_preferentiel_prestataire_non_conventionne_montant_arrondi_par_unite"                                                                                                                                : "preferentialstatus_no_convention",
		"avec_regime_preferentiel_prestataire_non_conventionne_montant_non_arrondi_par_unite"                                                                                                                            : "preferentialstatus_no_convention",
		"avec_regime_preferentiel_prestataire_non_conventionne"                                                                                                                                                          : "preferentialstatus_no_convention",
		"base_de_remboursement_avec_regime_preferentiel_prestataire_non_conventionne"                                                                                                                                    : "preferentialstatus_no_convention",
		"a_taux_exceptionnel_avec_regime_preferentiel"                                                                                                                                                                   : "preferentialstatus",
		"avec_regime_preferentiel_a_30_"                                                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_a_50_"                                                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_a_75_"                                                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_a_90_"                                                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_des_qui_consultent_un_medecin_specialiste_apres_avoir_ete_envoyes_par_un_medecin_generale"                                                                                             : "preferentialstatus",
		"avec_regime_preferentiel_fonction_agreee_de_soins_intensifs"                                                                                                                                                    : "preferentialstatus",
		"avec_regime_preferentiel_montant_fixe"                                                                                                                                                                          : "preferentialstatus",
		"avec_regime_preferentiel_pas_de_prestations_dans_l_annee_calendrier_precedent"                                                                                                                                  : "preferentialstatus",
		"avec_regime_preferentiel_pas_de_prestations_dans_l_annee_calendrier_precedente"                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_pour_anesthesie_pratiquee_au_cours_d_une_prestation_dont_la_valeur_relative_est_egale_ou_superieure_a_k_400_n_600_ou_i_600"                                                            : "preferentialstatus",
		"avec_regime_preferentiel_pour_anesthesie_pratiquee_au_cours_d_une_prestation_dont_la_valeur_relative_est_superieure_a_k_75_n_125_ou_i_125_et_inferieure_a_k_400_n_600_ou_i_600"                                 : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_i_600"                                                                                                                                                  : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_k_180_ou_n_300_et_k_400_ou_n_600"                                                                                                                       : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_k_300_ou_n500_et_k_390_ou_n_600"                                                                                                                        : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_k_300_ou_n500_ou_i_600_et_k_270_n450_ou_i_550"                                                                                                          : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_k_400"                                                                                                                                                  : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_k_75_et_k_400"                                                                                                                                          : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_k400_ou_n_600"                                                                                                                                          : "preferentialstatus",
		"avec_regime_preferentiel_pour_prestation_dans_categorie_n_600_ou_i_600"                                                                                                                                         : "preferentialstatus",
		"avec_regime_preferentiel_pour_prothese_amovible_de_1_a_5_dents"                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_pour_prothese_amovible_de_10_ou_11_dents"                                                                                                                                              : "preferentialstatus",
		"avec_regime_preferentiel_pour_prothese_amovible_de_12_dents_et_plus"                                                                                                                                            : "preferentialstatus",
		"avec_regime_preferentiel_pour_prothese_amovible_de_6_ou_7_dents"                                                                                                                                                : "preferentialstatus",
		"avec_regime_preferentiel_pour_prothese_amovible_de_8_ou_9_dents"                                                                                                                                                : "preferentialstatus",
		"avec_regime_preferentiel_prix_sur_facture"                                                                                                                                                                      : "preferentialstatus",
		"avec_regime_preferentiel_si_les_conditions_ne_sont_pas_respectees"                                                                                                                                              : "preferentialstatus",
		"avec_regime_preferentiel_sur_base_de_l_alinea_1_de_l_article_7"                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_sur_base_de_l_alinea_2_de_l_article_7"                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_sur_base_de_l_alinea_3_de_l_article_7"                                                                                                                                                 : "preferentialstatus",
		"avec_regime_preferentiel_sur_base_du_paragraphe_1_de_l_article_7"                                                                                                                                               : "preferentialstatus",
		"avec_regime_preferentiel_sur_base_du_paragraphe_2_de_l_article_7"                                                                                                                                               : "preferentialstatus",
		"avec_regime_preferentiel_sur_base_du_paragraphe_3_de_l_article_7"                                                                                                                                               : "preferentialstatus",
		"avec_regime_preferentiel"                                                                                                                                                                                       : "preferentialstatus",
		"maximale_avec_regime_preferentiel"                                                                                                                                                                              : "preferentialstatus",
		"patient_palliatif_avec_regime_preferentiel"                                                                                                                                                                     : "preferentialstatus",
		"sans_regime_preferentiel_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg"                                                 : "regular_dmg_no_preferentialstatus,old_no_preferentialstatus,chronical_no_preferentialstatus",
		"sans_regime_preferentiel_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                                                           : "regular_dmg_no_preferentialstatus,old_no_preferentialstatus_no_dmg,chronical_no_preferentialstatus_no_dmg",
		"_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                                                                                   : "regular_dmg,old_no_dmg,chronical_no_dmg",
		"a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                                                                                    : "regular_dmg,old_no_dmg,chronical_no_dmg",
		"_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_ou_sans_dmg_malade_chronique_avec_ou_sans_dmg"                                                                   : "regular_dmg,old,chronical",
		"a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_ou_sans_dmg_malade_chronique_avec_ou_sans_dmg"                                                                    : "regular_dmg,old,chronical",
		"de_10_a_75_ans_sans_dmg_et_sans_etre_malade_chronique_sans_regime_preferentiel"                                                                                                                                 : "regular_no_chronical_no_dmg_no_preferentialstatus",
		"a_partir_du_10eme_anniversaire_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique_avec_regime_preferentiel"                                                                                  : "regular_no_chronical_no_dmg_preferentialstatus",
		"a_partir_du_10eme_anniversaire_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique"                                                                                                           : "regular_no_dmg_no_chronical",
		"sans_regime_preferentiel_a_partir_du_10eme_anniversaire_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique"                                                                                  : "regular_no_dmg_no_preferentialstatus_no_chronical",
		"_a_partir_du_10eme_anniversaire_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique"                                                                                                          : "regular_no_dmg_no_preferentialstatus",
		"avec_regime_preferentiel_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg"                                                 : "regular_preferentialstatus_dmg,old_preferentialstatus,chronical_preferentialstatus",
		"avec_regime_preferentiel_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                                                           : "regular_preferentialstatus_dmg,old_preferentialstatus_no_dmg,chronical_preferentialstatus_no_dmg",
		"avec_regime_preferentiel_a_partir_du_10eme_anniversaire_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique"                                                                                  : "regular_preferentialstatus_no_dmg_no_chronical",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_avec_regime_preferentiel"                                                                                             : "regular_preferentialstatus_dmg,old_no_dmg_preferentialstatus,chronical_no_dmg_preferentialstatus",
		"a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_avec_regime_preferentiel"                                                                                                                  : "regular_dmg_preferentialstatus,chronical_dmg_preferentialstatus",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_avec_regime_preferentiel"                                                                                   : "regular_dmg_preferentialstatus,old_preferentialstatus,chronical_preferentialstatus",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_sans_regime_preferentiel"                                                                                   : "regular_dmg_no_preferentialstatus,old_no_preferentialstatus,chronical_no_preferentialstatus",
		"de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_sans_regime_preferentiel"                                                                                             : "regular_dmg_no_preferentialstatus,old_no_dmg_no_preferentialstatus,chronical_no_dmg_no_preferentialstatus",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_medecin_stagiaire_avec_regime_preferentiel"                                                                                                                 : "trainee_child-120m_preferentialstatus",
		"de_10_a_75_ans_sans_dmg_et_sans_etre_malade_chronique_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli_avec_regime_preferentiel"                                                        : "trainee_regular_no_dmg_preferentialstatus",
		"enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_medecin_stagiaire_sans_regime_preferentiel"                                                                                                                 : "trainee_child-120m_no_preferentialstatus",
		"de_10_a_75_ans_sans_dmg_et_sans_etre_malade_chronique_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli_sans_regime_preferentiel"                                                        : "trainee_regular_no_chronical_no_dmg_no_preferentialstatus",
		"sans_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_medecin_stagiaire"                          : "trainee_child-120m_no_preferentialstatus,trainee_regular_no_preferentialstatus_dmg,trainee_old_no_preferentialstatus_no_dmg,trainee_chronical_no_preferentialstatus_no_dmg",
		"sans_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_medecin_stagiaire"                : "trainee_child-120m_no_preferentialstatus,trainee_regular_no_preferentialstatus_dmg,trainee_old_no_preferentialstatus,trainee_chronical_no_preferentialstatus",
		"medecin_stagiaire_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                 : "trainee_child-120m,trainee_regular_dmg,trainee_old_no_dmg,trainee_chronical_no_dmg",
		"medecin_stagiaire_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg"       : "trainee_child-120m,trainee_regular_dmg,trainee_old,trainee_chronical",
		"medecin_stagiaire_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dossier_medical_global"                                                                                                                       : "trainee_child-120m",
		"medecin_stagiaire_dans_le_cadre_du_dmg_1_des_conditions_surveillance_non_rempli"                                                                                                                                : "trainee_dmg",
		"sans_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_medecin_stagiaire"                                                                                                                 : "trainee_no_preferentialstatus_child-120m",
		"sans_regime_preferentiel_dans_le_cadre_du_dmg_medecin_stagiaire_1_des_conditions_surveillance_non_rempli"                                                                                                       : "trainee_no_preferentialstatus_dmg",
		"sans_regime_preferentiel_pas_dans_le_cadre_du_dmg_medecin_stagiaire_1_des_conditions_surveillance_non_rempli"                                                                                                   : "trainee_no_preferentialstatus_no_dmg",
		"sans_regime_preferentiel_a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli"                                         : "trainee_no_preferentialstatus_old_dmg_chronical_dmg",
		"sans_regime_preferentiel_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_medecin_stagiaire"                                                                           : "trainee_no_preferentialstatus_regular,trainee_no_preferentialstatus_dmg_old,trainee_no_preferentialstatus_chronical_no_dmg",
		"sans_regime_preferentiel_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_medecin_stagiaire"                                                                 : "trainee_no_preferentialstatus_regular_dmg,trainee_old_no_preferentialstatus,trainee_chronical_no_preferentialstatus",
		"sans_regime_preferentiel_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli"                      : "trainee_no_preferentialstatus_regular_no_dmg_no_chronical",
		"sans_regime_preferentiel_des_qui_consultent_un_medecin_specialiste_apres_avoir_ete_envoyes_par_un_medecin_generale_medecin_specialiste_stagiaire"                                                               : "trainee_no_preferentialstatus",
		"sans_regime_preferentiel_medecin_specialiste_stagiaire"                                                                                                                                                         : "trainee_no_preferentialstatus",
		"medecin_stagiaire_a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_une_des_conditions_de_surveillance_de_stage_non_rempli"                                                                  : "trainee_old_dmg_chronical_dmg",
		"medecin_stagiaire_avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg"                : "trainee_preferentialstatus_child-120m,trainee_regular_preferentialstatus_dmg,trainee_old_preferentialstatus,trainee_chronical_preferentialstatus",
		"avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_medecin_stagiaire"                          : "trainee_preferentialstatus_child-120m,trainee_preferentialstatus_regular_dmg,trainee_preferentialstatus_old_no_dmg,trainee_preferentialstatus_chronical_no_dmg",
		"avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_sans_dmg_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_medecin_stagiaire"                : "trainee_preferentialstatus_child-120m,trainee_preferentialstatus_regular_dmg,trainee_preferentialstatus_old,trainee_preferentialstatus_chronical",
		"avec_regime_preferentiel_enfant_avant_le_10eme_anniversaire_avec_ou_sans_dmg_medecin_stagiaire"                                                                                                                 : "trainee_preferentialstatus_child-120m",
		"avec_regime_preferentiel_dans_le_cadre_du_dmg_medecin_stagiaire_1_des_conditions_surveillance_non_rempli"                                                                                                       : "trainee_preferentialstatus_dmg",
		"avec_regime_preferentiel_pas_dans_le_cadre_du_dmg_medecin_stagiaire_1_des_conditions_surveillance_non_rempli"                                                                                                   : "trainee_preferentialstatus_no_dmg",
		"avec_regime_preferentiel_a_partir_du_75eme_anniversaire_avec_dmg_et_malade_chronique_avec_dmg_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli"                                         : "trainee_preferentialstatus_old_dmg_chronical_dmg",
		"avec_regime_preferentiel_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg_medecin_stagiaire"                                                                 : "trainee_preferentialstatus_regular_dmg,trainee_old_preferentialstatus,trainee_chronical_preferentialstatus",
		"avec_regime_preferentiel_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique_medecin_stagiaire_une_des_conditions_de_surveillance_de_stage_non_rempli"                      : "trainee_preferentialstatus_regular_no_dmg_no_chronical",
		"avec_regime_preferentiel_des_qui_consultent_un_medecin_specialiste_apres_avoir_ete_envoyes_par_un_medecin_generale_medecin_specialiste_stagiaire"                                                               : "trainee_preferentialstatus",
		"avec_regime_preferentiel_medecin_specialiste_stagiaire"                                                                                                                                                         : "trainee_preferentialstatus",
		"medecin_stagiaire_patient_palliatif_avec_regime_preferentiel_une_des_conditions_de_surveillance_de_stage_non_rempli"                                                                                            : "trainee_preferentialstatus",
		"medecin_stagiaire_patient_palliatif_sans_regime_preferentiel_une_des_conditions_de_surveillance_de_stage_non_rempli"                                                                                            : "trainee_preferentialstatus",
		"medecin_stagiaire_une_des_conditions_de_surveillande_de_stage_non_rempli_patient_palliatif_avec_regime_preferentiel"                                                                                            : "trainee_preferentialstatus",
		"avec_regime_preferentiel_de_10_a_75_ans_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg_medecin_stagiaire"                                                                           : "trainee_regular_dmg,trainee_old_no_dmg,trainee_chronical_no_dmg",
		"medecin_stagiaire_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_sans_dmg_malade_chronique_sans_dmg"                                                                  : "trainee_regular_dmg,trainee_old_no_dmg,trainee_chronical_no_dmg",
		"medecin_stagiaire_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_avec_dmg_a_partir_du_75eme_anniversaire_avec_sans_dmg_malade_chronique_avec_sans_dmg"                                                        : "trainee_regular_dmg,trainee_old,trainee_chronical",
		"medecin_stagiaire_a_partir_du_10eme_jusqu_a_son_75eme_anniversaire_sans_dmg_et_sans_etre_malade_chronique_une_des_conditions_de_surveillance_de_stage_non_rempli"                                               : "trainee_regular_no_dmg_no_chronical",
		"medecin_specialiste_stagiaire"                                                                                                                                                                                  : "trainee",
		"medecin_stagiaire_1_des_conditions_surveillance_non_rempli"                                                                                                                                                     : "trainee",
		"medecin_stagiaire"                                                                                                                                                                                              : "trainee",
		"pour_les_qui_satisfont_aux_conditions_citees_dans_l_ar_du_16_02_2009_il_n_y_a_pas_d_personnelle_medecin_stagiaire_1_des_conditions_surveillance_non_rempli"                                                     : "trainee"
	]

	Map<String, TarificationCodeInfo> tarficationInfos = new HashMap<>()

	class TarificationCodeInfo {
		String code
		Boolean prescriber
		Boolean relatedCode
		List<String> relatedCodes
	}

	private void initHttpClient(username, password, couchdbBase = 'icure-base', couchdbPatient = 'icure-patient', couchdbContact = 'icure-healthdata', couchdbConfig = 'icure-config') {
		HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("${DB_PROTOCOL ?: "http"}://${DB_HOST ?: "127.0.0.1"}:" + DB_PORT).username(username ?: System.getProperty("dbuser") ?: "icure").password(password ?: System.getProperty("dbpass") ?: "S3clud3dM@x1m@").build()
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)
		// if the second parameter is true, the database will be created if it doesn't exists
		this.couchdbBase = couchdbBase ? dbInstance.createConnector(couchdbBase, false) : null
		this.couchdbPatient = couchdbPatient ? dbInstance.createConnector(couchdbPatient, false) : null
		this.couchdbContact = couchdbContact ? dbInstance.createConnector(couchdbContact, false) : null
		this.couchdbConfig = couchdbConfig ? dbInstance.createConnector(couchdbConfig, false) : null

		Security.addProvider(new BouncyCastleProvider())
		this.getClass().getResourceAsStream("prescriberRelatedCodes.json").withReader("UTF8") { new Gson().fromJson(it, new TypeToken<ArrayList<TarificationCodeInfo>>() {}.type).each { this.tarficationInfos[it.code] = it } }
	}

	TarificationCodeImporter(dbprotocol, dbhost, dbport, couchdbBase, couchdbPatient, couchdbContact, couchdbConfig, username, password, lang) {
		this.DB_PROTOCOL = dbprotocol
		this.DB_HOST = dbhost
		this.DB_PORT = dbport
		this.DB_NAME = null
		this.language = lang

		initHttpClient(username, password, couchdbBase, couchdbPatient, couchdbContact, couchdbConfig)
	}

	TarificationCodeImporter(dbprotocol, dbhost, dbport, dbname, username, password, lang) {
		this.DB_PROTOCOL = dbprotocol
		this.DB_HOST = dbhost
		this.DB_PORT = dbport
		this.DB_NAME = dbname
		this.language = lang

		initHttpClient(username, password, DB_NAME + '-base', DB_NAME + '-patient', DB_NAME + '-healthdata', DB_NAME + '-config')
	}

	TarificationCodeImporter() {
		initHttpClient(null, null)
	}

	static void main(String... args) {
		def options = args
		((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR);

		def language = 'fr'
		def keyRoot = null
		def src_file = options[-1]
		def type = 'INAMI-RIZIV'

		options.each {
			if (it.startsWith("lang=")) {
				language = it.substring(5)
			} else if (it.startsWith("keyroot=")) {
				keyRoot = it.substring(8)
			} else if (it.startsWith("type=")) {
				type = it.substring(5)
			}
		}

		def start = System.currentTimeMillis()

		def importer = new TarificationCodeImporter()

		((Logger) LoggerFactory.getLogger("org.apache.http.wire")).setLevel(Level.ERROR);
		((Logger) LoggerFactory.getLogger("org.apache.http.headers")).setLevel(Level.ERROR);
		((Logger) LoggerFactory.getLogger("org.apache.http")).setLevel(Level.ERROR);
		((Logger) LoggerFactory.getLogger("org.ektorp.impl")).setLevel(Level.ERROR);

		importer.language = language;
		importer.keyRoot = keyRoot ?: importer.DEFAULT_KEY_DIR;

		File root = new File(src_file)
		assert root?.exists()

		importer.doScan(root, type);

		println "Process completed in ${(System.currentTimeMillis() - start) / 1000.0} seconds"
	}

	def splitTextKey(String key) {
		def filtersMap = [preferentialstatus: "any", trainee: "any", child: "any", major: "any", old: "any", regular: "any", dmg: "any", chronical: "any", convention: "any"]
		"${key}_".eachMatch("(no_)?(.+?)_") { _0, _1, _2 ->

			if (_2.startsWith("child-")) {
				String age = _2.substring(6)

				if (age.endsWith('y')) {
					age = "${(age[0..-2] as Integer) * 18}m"
				}

				filtersMap["child"] = age
			} else {
				filtersMap[_2] = _1 ? "no" : "yes"
			}
		}
		return filtersMap
	}

	def doScan(File root, String type) {
		def refsValues = [:]
		def YEAR = 2018

		refs.each { key, value ->
			def parts = value.split(",")
			parts.each {
				if (!refsValues[it]) {
					refsValues[it] = []
				}
				refsValues[it] << key
			}
			if (parts.size() > 1) {
				if (!refsValues[value]) {
					refsValues[value] = []
				}
				refsValues[value] << key
			}
		}

		def rubrics = [:]
		def valTypes = [:]
		def tarifications = [:]
		def unknownCodes = [:]
		def fee = ['01']
		def rei = ['02', '03', '04', '06']
		def tm = ['05']

		def groups = [
			Base: ['Rééducation fonctionnelle et professionnelle - quote part person.', 'Consultations, visites et avis de médecins', 'Placement et frais déplacement - quote-part personnelle CMP', 'Prestations spéciales générales et ponctions', 'Prestations techniques médicales - prestations courantes', 'Prestations techniques urgentes  - Article 26, §1bis', 'Prestations techniques urgentes - Article 26, §1 et §1ter', 'Réanimation', 'Regularisations ne pouvant pas être ventilées par document N', 'Rhumatologie', 'Sevrage tabagique', 'Soins donnés par infirmières, soigneuses et gardes-malades', 'Surveillance des bénéficiaires hospitalisés'],
			Full: ['Accouchements - accoucheuses', 'Cardiologie', 'Chirurgie abdominale', 'Chirurgie des vaisseaux', 'Chirurgie générale', 'Chirurgie plastique', 'Chirurgie thoracique', 'Dermato-vénéréologie', 'Gastro-entérologie', 'Gynécologie et obstétrique', 'Logopédie', 'Médecine interne', 'Neurochirurgie', 'Neuropsychiatrie', 'Ophtalmologie', 'Orthopédie', 'Oto-rhino-laryngologie', 'Pédiatrie', 'Physiothérapie', 'Pneumologie', 'Radiodiagnostic', 'Soins dentaires', 'Soins par audiciens', 'Soins par opticiens', 'Stomatologie', 'Urologie', 'Rééducation fonctionnelle et professionnelle - quote part person.', 'Consultations, visites et avis de médecins', 'Placement et frais déplacement - quote-part personnelle CMP', 'Prestations spéciales générales et ponctions', 'Prestations techniques médicales - prestations courantes', 'Prestations techniques urgentes  - Article 26, §1 bis', 'Prestations techniques urgentes - Article 26, §1 et 1 ter', 'Réanimation', 'Regularisations ne pouvant pas être ventilées par document N', 'Rhumatologie', 'Sevrage tabagique', 'Soins donnés par infirmières, soigneuses et gardes-malades', 'Surveillance des bénéficiaires hospitalisés']
			//Excluded: ['Accouchements - aide opératoire', 'Aide opératoire', 'Anatomo-pathologie - Article 32', 'Anesthésiologie', 'Appareils', 'Avances prévues par convention et non récupérables', 'Bandages, ceintures et protheses des seins', 'Biologie clinique - Article 3', 'Biologie clinique - Article 24§1', 'Biologie moléculaire - matériel génétique de micro-organismes', 'Code bande magnétique', 'Codes de régularisation', 'Conventions internationales', 'Dialyse rénale', 'Examens génétiques - Article 33', 'Honoraires forfaitaires - biologie clinique - ambulant', 'Honoraires forfaitaires - biologie clinique - Art 24§2', 'Hospitalisation', 'Materiel de synthese art 28 §1', 'Materiel de synthese art 28 §8', 'Médecine nucléaire in vitro', 'Médecine nucléaire in vivo', 'Montants payés indûment inférieur à 400 francs et non récupérés', 'Part personnelle pour patients hospitalisés', 'Pas de rubrique ou rubrique pas connu', 'Prestations interventionnelles percutanées - imagerie médicale', 'Prestations pharmaceutiques', 'Projets article 56', 'Quote-part personnelle hospitalisation', 'Radio-isotopes', 'Radiodiagnostic', 'Radiothérapie et radiumthérapie', 'Tests de biologie moléculaire sur du matériel génétique humain', 'Tissues d\'origine humaine', 'Transplantations', 'Urinal, anus artificiel et canule tracheale']
		]

		new File(root, 'NOMEN_SUMMARY_EXT.xml').withInputStream {
			new XmlSlurper().parse(it).NOMEN_SUMMARY_EXT.each { e ->
				def r = rubrics[e.nomen_grp_n.text()] ?: (rubrics[e.nomen_grp_n.text()] = [id: e.nomen_grp_n.text(), fr: e.nomen_grp_n_desc_fr.text(), nl: e.nomen_grp_n_desc_nl.text(), tarifications: []])

				r.tarifications << (tarifications[e.nomen_code.text()] = [
					id           : e.nomen_code.text(),
					amb          : e.ambhos_pat_cat.text() != "2",
					startCode    : Date.parse(e.dbegin.text()?.contains('T') ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd", e.dbegin.text()),
					fr           : e.nomen_desc_fr.text(),
					nl           : e.nomen_desc_nl.text(),
					rubric       : r,
					letter1      : e.key_letter1.text(),
					letter_index1: e.key_letter_index1.text(),
					coeff1       : e.key_coeff1.text(),
					letter1_value: e.key_letter1_value.text(),
					letter2      : e.key_letter2.text(),
					letter_index2: e.key_letter_index2.text(),
					coeff2       : e.key_coeff2.text(),
					letter2_value: e.key_letter2_value.text(),
					letter3      : e.key_letter3.text(),
					letter_index3: e.key_letter_index3.text(),
					coeff3       : e.key_coeff3.text(),
					letter3_value : e.key_letter3_value.text(),
					valorisations: []
				])
			}
		}

		new File(root, 'NOMEN_FEECODES.xml').withInputStream {
			new XmlSlurper().parse(it).NOMEN_FEECODES.each { e ->
				if (e.fee_code_cat.text() != '07') valTypes[e.fee_code.text()] = [key: e.fee_code.text(), fr: e.fee_code_desc_fr.text(), nl: e.fee_code_desc_nl.text(), cat: e.fee_code_cat.text()]
			}
		}

		new File(root, 'NOMEN_CODE_FEE_LIM.xml').withInputStream { f ->
			new File(root, 'NOMEN_CODE_FEE_BIS_LIM.xml').withInputStream { fb ->
				def parseVal = { e ->
					def t = tarifications[e.nomen_code.text()]
					if (t && valTypes[e.fee_code.text()]) {
						t.valorisations << [
							fr                 : valTypes[e.fee_code.text()].fr,
							nl                 : valTypes[e.fee_code.text()].nl,
							type               : e.fee_code.text(),
							from               : Date.parse(e.dbegin_fee.text()?.contains('T') ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd", e.dbegin_fee.text()),
							to                 : Date.parse(e.dend_fee.text()?.contains('T') ? "yyyy-MM-dd'T'HH:mm:ss" : "yyyy-MM-dd", e.dend_fee.text()),
							fee                : new Double(fee.contains(valTypes[e.fee_code.text()].cat) ? Double.parseDouble(e.fee.text()) : 0.0),
							reimbursement      : new Double(rei.contains(valTypes[e.fee_code.text()].cat) ? Double.parseDouble(e.fee.text()) : 0.0),
							patientIntervention: new Double(tm.contains(valTypes[e.fee_code.text()].cat) ? Double.parseDouble(e.fee.text()) : 0.0)
						]
					} else {
						println("${e.nomen_code.text()} valorisation not found")
					}
				}
				new XmlSlurper().parse(f).NOMEN_CODE_FEE_LIM.each(parseVal)
				new XmlSlurper().parse(fb).NOMEN_CODE_FEE_BIS_LIM.each(parseVal)
			}
		}


		def codes = []


		Map<String,String> conditions = [:]
		refsValues.each { String key, options ->
			Map<String,String> map = [:]

			def cnd = key.contains(",") ? (key.split(",").collect { '( ' + conditions[it] + ' )' ?: "<<${it}>>" })
				.join(' || ') : (map = splitTextKey(key)).keySet().sort().collect { k ->
				def v = map[k]
				def ref = (k == 'convention' || k == 'trainee') ? 'hcp' : 'patient'
				v == 'any' ? 'true' :
						(k == 'old' && v == 'yes') ? "${ref}.age >= 75" :
								(k == 'old' && v == 'no') ? "${ref}.age < 75" :
										(k == 'major' && v == 'yes') ? "${ref}.age >= 18" :
												(k == 'major' && v == 'no') ? "${ref}.age < 18" :
														(k == 'regular' && v == 'yes') ? "( ${ref}.age >= 10 && ${ref}.age < 75 )" :
																(k == 'regular' && v == 'no') ? "( ${ref}.age < 10 || ${ref}.age >= 75 )" :
																		(k == 'child' && v == 'no') ? "${ref}.age >= 10" :
																				(k == 'child') ? "${ref}.age < ${v.replaceAll('yes', '10').replaceAll('([0-9]+)m', '$1/12')}" :
																						v == 'yes' ? "${ref}.${k}" : v == 'no' ? "!${ref}.${k}" : "${ref}.${k} == '${v}'"
			}.findAll { it != 'true' }.join(' && ')
			conditions[key] = cnd.length() ? cnd : 'true'
			conditions[key] = cnd.length() ? cnd : 'true'
		}

		conditions.each { k, String vv ->
			if (vv.contains('<<')) {
				conditions[k] = vv.replaceAll(/<<(.+)>>/) { _, String ref -> "( ${conditions[ref]} )" }
			}
		}

		println "Key\tDescr\tRaw conditions\tpreferentialstatus\ttrainee\tchild\tmajor\told\tregular\tdmg\tchronical\tconvention\tpredicate"
		valTypes.forEach { k, v ->
			def frt = (Normalizer.normalize( v.fr, Normalizer.Form.NFD).replaceAll(/\p{InCombiningDiacriticalMarks}+/, "").toLowerCase())
					.replaceAll(/[^a-z0-9]+/, "_")
					.replaceAll("(honoraires?|rembousement|intervention|montant_de_l_de_l_assurance_|montant_+de_+l_+de_+l_+assurance|montant_de_l_indemnite|part_personnelle_|beneficiaires?)_?", "")
					.replaceAll("_pour_prestation_dans_categorie.+", "").replaceAll("__+", "_")
					.replaceAll("__+", "_").replaceAll("__+", "_").replaceAll("__+", "_")

			if (!frt.startsWith("le_numero_de_code_est_supprime") && !frt.startsWith("base_de_remboursement") && !frt?.contains('pas_encore_repris_de_tarifs_dans_nomensoft') && !frt?.contains('listes_limitatives') && !frt?.contains('liste_limitative') && !frt?.contains("ticket_moderateur") && !frt?.contains("pas_de_tarifs")) {
				def frtCode = refs[frt]
				if (!frtCode) {
					unknownCodes[frt] = (unknownCodes[frt] ?: 0) + 1
					frtCode = "_" + frt
				}
				v.code = frt
				v.predicateSource = conditions[frtCode] ? frtCode.split(',').collect { it -> splitTextKey(it) } : [:]
				v.predicate = (conditions[frtCode] ?: "false&&'${frtCode}'").toString()
				if (conditions[frtCode]) { println "$k\t${v.fr}\t$frtCode\t${v.predicateSource.collect { it -> it.preferentialstatus}.join(',')}\t${v.predicateSource.collect { it -> it.trainee}.join(',')}\t${v.predicateSource.collect { it -> it.child}.join(',')}\t${v.predicateSource.collect { it -> it.major}.join(',')}\t${v.predicateSource.collect { it -> it.old}.join(',')}\t${v.predicateSource.collect { it -> it.regular}.join(',')}\t${v.predicateSource.collect { it -> it.dmg}.join(',')}\t${v.predicateSource.collect { it -> it.chronical}.join(',')}\t${v.predicateSource.collect { it -> it.convention}.join(',')}\t${v.predicate}" }
			} else {
				v.code = frt
				v.predicate = ("false&&'_${frt}'").toString()
			}
		}

		println(new Gson().toJson(valTypes.values()))

		[false,true].forEach { amb ->
			groups.each { kg, g ->
				println g
				def rubKeys= new ArrayList(rubrics.keySet()).sort { a, b -> a <=> b }
				rubKeys.each { kr ->
					def r = rubrics[kr]
					if (!g.contains(r.fr)) {
						println r.fr
						return
					}

					r.tarifications.findAll { map -> map.amb == amb && map.rubric.id == kr }.sort { it.id }.each { map ->
						def label = [:]
						label.fr = map.fr
						label.nl = map.nl
						def code = new Tarification(
								Sets.newHashSet('be', 'fr'),
								type,
								map.id,
								"1.0",
								label)

						code.nGroup = r.id

						code.letterValues = new LinkedList()
						if (map.letter1 && map.letter1 != '-') {
							code.letterValues.add(new LetterValue(letter: map.letter1, index: map.letter_index1, coefficient: Double.valueOf(map.coeff1), value: Double.valueOf(map.letter1_value)))
						}
						if (map.letter2 && map.letter2 != '-') {
							code.letterValues.add(new LetterValue(letter: map.letter2, index: map.letter_index2, coefficient: Double.valueOf(map.coeff2), value: Double.valueOf(map.letter2_value)))
						}
						if (map.letter3 && map.letter3 != '-') {
							code.letterValues.add(new LetterValue(letter: map.letter3, index: map.letter_index3, coefficient: Double.valueOf(map.coeff3), value: Double.valueOf(map.letter3_value)))
						}

						code.category = [fr: map.rubric.fr, nl: map.rubric.nl] as Map<String, String>
						int i = 0

						code.valorisations = new HashSet(map.valorisations.collect { val ->
							def vt = valTypes[val.type]

							if (vt) {
								return new Valorisation(
										startOfValidity: val.from ? FuzzyValues.getFuzzyDateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(val.from.time), ZoneId.systemDefault()), ChronoUnit.SECONDS) : (YEAR*10000+101)*1000000,
										endOfValidity:  (Long.valueOf(YEAR+1L)*10000+101)*1000000L,
										label: ([fr: vt?.fr, nl: vt?.nl] as Map<String, String>),
										predicate: vt.predicate,
										patientIntervention: val.patientIntervention ?: 0,
										reimbursement: val.reimbursement ?: 0,
										doctorSupplement: 0,
										totalAmount: val.fee ?: val.patientIntervention + val.reimbursement 	?: 0,
										vat: 0
									)

							} else {
								println "Couldn't find valorisation for ${code}"
								return null
							}
						})
						Set<Valorisation> compacted = new HashSet<>()

						//First compact valorisations
						code.valorisations.findAll { it != null }.each { v ->
							def eq = compacted.find { e -> e != v && e.predicate == v.predicate && e.startOfValidity == v.startOfValidity && e.endOfValidity == v.endOfValidity }
							if (eq) {
								if (eq.totalAmount?.doubleValue() > 0.0 && v.totalAmount?.doubleValue() > 0.0) {
									//println("Invalid predicates for code ${code.id} : ${eq.label.fr} <-> ${v.label.fr}")
									compacted << v
								} else {
									eq.patientIntervention += v.patientIntervention
									eq.reimbursement += v.reimbursement

									eq.totalAmount += v.totalAmount
									eq.vat = (eq.vat ?: 0) + (v.vat ?: 0)
								}
							} else {
								compacted << v
							}
						}

						def trueCode = compacted.find { it.predicate == 'true' }

						if (trueCode) {
							compacted.remove(trueCode)
							compacted.removeIf { v -> v.totalAmount == trueCode.totalAmount && v.reimbursement == 0 as Double & v.patientIntervention == 0 as Double }
							List<String> preds = []
							compacted.each { v ->
								if (v.totalAmount == 0.0 as Double) {
									v.totalAmount = trueCode.totalAmount
									preds << '( ' + v.predicate + ' )'
								}
							}
							if (preds.size()) {
								trueCode.predicate = "!(${preds.join('||')})"
							}
						}
						code.valorisations = compacted
						if (trueCode) {
							code.valorisations << trueCode
						}

						TarificationCodeInfo tci = tarficationInfos[code.code]
						if (tci) {
							code.needsPrescriber = tci.prescriber
							code.hasRelatedCode = tci.relatedCode
							if (tci.relatedCodes.size()) {
								code.relatedCodes = new HashSet<>(tci.relatedCodes)
							}
						}

						codes << code
					}
				}
			}
		}

		println "Unknowns"
		unknownCodes.entrySet().each { println "${it.value}: ${it.key}" }

		def current = [:]

		boolean retry = true;
		while (retry) {
			retry = false;
			try {
				couchdbBase.queryView(new ViewQuery(includeDocs: true).dbPath(couchdbBase.path()).designDocId("_design/Tarification").viewName("all"), Tarification.class).each { Tarification t ->
					current[t.id] = t
				}
			} catch (DbAccessException e) {
				if (e instanceof DocumentNotFoundException) {
					println "Bailing because of $e"
					return [:]
				} else {
					println "Retrying because of $e"
					retry = true
				}
			}
		}

		Map<String, Tarification> res = [:]

		codes.sort {a,b -> a.code <=> b.code}
		codes.each { newCode ->
			res[newCode.code] = newCode
			if (current.containsKey(newCode.id)) {
				newCode.rev = current[newCode.id].rev

				def keptCodes = current[newCode.id].valorisations.findAll { v -> v != null }.collect { Valorisation v ->
					if (v.startOfValidity < 29991231) { v.startOfValidity *= 1000000L }
					if (v.endOfValidity < 29991231) { v.endOfValidity *= 1000000L }

					if (v.startOfValidity >= (YEAR * 10000 + 101) * 1000000L ) {
						return null
					}

					for (Valorisation nv in newCode.valorisations) {
						if (v.predicate == nv.predicate) {
							if (nv.startOfValidity <= v.startOfValidity && (nv.endOfValidity ?: 29991231000000L) > v.startOfValidity) {
								v.startOfValidity = Math.min(v.endOfValidity, nv.endOfValidity)
							}
							if ((nv.endOfValidity ?: 29991231000000L) >= (v.endOfValidity ?: 29991231000000L) && nv.startOfValidity < (v.endOfValidity ?: 29991231000000L)) {
								v.endOfValidity = Math.max(v.startOfValidity, nv.startOfValidity)
							}
						}
					}
					for (Valorisation nv in newCode.valorisations) {
						if ((Math.abs(v.startOfValidity-nv.startOfValidity)<1000000 || v.startOfValidity > nv.startOfValidity) && (Math.abs(v.endOfValidity-nv.endOfValidity)<1000000 || v.endOfValidity < nv.endOfValidity)) {
							return null
						}
					}
					return v
				}.findAll { vv -> vv && vv.startOfValidity < vv.endOfValidity }
				newCode.valorisations.addAll(keptCodes)
			}
		}

		codes.collate(1000).each {
			couchdbBase.executeBulk(it)
		}

		return res
	}
}
