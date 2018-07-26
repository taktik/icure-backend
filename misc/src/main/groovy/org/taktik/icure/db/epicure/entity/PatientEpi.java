package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientEpi {

	private Integer id_;
	private String fiche;
	private String nom;
	private String prenom;
	private Long naissance;
	private Long typepatient; // +++ Oui, à récupérer dans un champ texte de
								// type note
	private Long religion; // +++ Oui, à récupérer dans un champ texte de type
							// note
	private Long etatcivil; // +++ Oui, à récupérer dans un champ texte de type
							// note
	private String adresse;
	private String ville;
	private String code;
	private String telephone1;
	private String telephone2;
	private String adresse2;
	private String ville2;
	private String code2;
	private String fax;
	private String fax2;
	private String gsm;
	private Long mutuelle;
	private String nummutuelle;
	private Long status;
	// private String typemut;
	// private Long datevalablesis;
	// private Long flagpostit;
	private Integer sang;
	private String fichepat;
	private Integer langue;
	private Integer gestionpatient; // +++ pour récupérer les patients flaggés
									// "privés"
	private Long datedeces;
	private Long motifdeces;
	private String prevenir; // +++ a mettre dans champ Note
	private String nomepoux; // +++ a mettre dans champ Note
	// private Long flaglabo;
	// private Long flagcourrier;
	// private Long flagtache;
	// private Long flagvaccin;
	// private String notevaccin;
	// private Float poids;
	// private Float tailles;
	private String titre5;
	private String titre4;
	private String titre3;
	private String titre2;
	private String titre1;
	// private Long flagantecedent;
	// private Long datedebutsis;
	// private Long datefinsis;
	// private Long datelecturesis;
	private Long datedebutass; // +++ date de fin de validité EID
	// private Long flaganonyme;
	// private Long datecontact;
	private Long datecreation; // +++ Date de création de la fiche
	private String mail;
	private String gestionnaire_pat; // +++ Nom de celui qui a créé la fiche :
										// médecin traitant, si c'est un
										// généraliste
	// private Long flagvisiteun;
	// private String nomprenomlie;
	private String prenom2;
	private Integer sexe;
	private Integer deces;
	// private String datemodifie;
	// private Long transfusion;
	// private Long resucite;
	private String antecedent;
	private String notebin;
	private Integer cpas_nation_naissance; // +++ Oui, à récupérer dans un champ
											// texte de type note
	private Integer cpas_nation_pere; // +++ Oui, à récupérer dans un champ
										// texte de type note
	private Integer cpas_acces_soins; // +++ Oui, à récupérer dans un champ
										// texte de type note
	private Integer cpas_statut_fami; // +++ Oui, à récupérer dans un champ
										// texte de type note
	private Integer cpas_statut_etude; // +++ Oui, à récupérer dans un champ
										// texte de type note
	private Integer cpas_statut_social; // +++ Oui, à récupérer dans un champ
										// texte de type note
	private Integer cpas_tabac; // +++ Oui, à récupérer dans un champ texte de
								// type note
	private Integer cpas_mutuelle; // +++ Oui, à récupérer dans un champ texte
									// de type note
	private String idprofession;
	// private Long flagmessage;
	// private Integer titre_entete;
	// private Long flag_synchropocket;
	// private Long flag_synchro_etude;
	// private Long flag_synchro_sumher;
	// private Integer dmg_fin;
	// private Integer flag_nbr_enreg;
	private String responsable_dossier_libre; // +++ Oui, à récupérer dans un
												// champ texte de type note
	// private Long datevalable;
	// private Long flag_maladie_chronique;
	private Integer tarif_attestation; // +++ a définir
	private String national; // +++ NISS
	// private Integer paiement;
	// private Integer type_envoi_attestation;
	// private Integer combo_actif_donne;
	// private Integer combo_choix_donnee;
	// Objet OLEphoto;
	// private Long numpat;
	// private Integer sexe_planning;
	// private Integer nationalite_planning;
	// private Long date_ante;
	// private Long date_postit;
	// private Long don_organe;
	// private Long flag_etude;
	// private Long flag_adressecorriger;
	// private String tel1rech;
	// private String gsmrech;
	// private Long date_attestation;
	// private Long flag_deces_corrige;
	// private Long refus_intubation;
	// private Long demande_euthanasie;
	// private Long screening_masse;
	// private Long flag_medipath;
	// private Long date_envoi_medipath;
	// private Long flag_lienconshub;
	private String idadressefacture; // +++ ID du carnet d'adresse pour adresse
										// de facturation
	// private String id_libelle_type;
	// private Integer arappeler;
	// private Long refus_vaccination;
	// private Long flagenvoyersumehr;
	private Long dateenvoiesumehr; // +++ date du dernier envoi Suhmer
	private Long flag_trajetsoins; // +++ si coché = patient en trajet de soins
									// (utile pour facturation car TM = 0)
	private Integer sel_transfusion; // +++ volonté du patient : 1 = accepte pas
										// 2 = accepte 3 = ?
	private Integer sel_reanimation; // +++ volonté du patient : 1 = accepte pas
										// 2 = accepte 3 = ?
	private Integer sel_intubation; // +++ volonté du patient : 1 = accepte pas
									// 2 = accepte 3 = ?
	private Integer sel_euthanasie; // +++ volonté du patient : 1 = accepte pas
									// 2 = accepte 3 = ?
	private Integer sel_donorgane; // +++ volonté du patient : 1 = accepte pas 2
									// = accepte 3 = ?
	private Integer sel_screening; // +++ volonté du patient : 1 = accepte pas 2
									// = accepte 3 = ?
	private Integer sel_refusvaccin; // +++ volonté du patient : 1 = accepte pas
										// 2 = accepte 3 = ?
	// private Long id_user1_sumehr;
	// private Long flaglueid;
	private Long flaglientherapeutique; // +++ Car on va checker directement le
										// lien thérapeutique
	private Integer type_paiement;
	private Long maisonmedical;
	private Long flag_assure_mut;
	private Long inscrit_rsw; // +++ Car on va checker directement
	// private Long fin_lientherapeutique; // +++ Car on va checker directement
	// private Integer motif_droit_tp; // +++ Car on va checker directement
	// private Integer typecarteeid;
	private String numcarteeid;
	private Integer mmforfait; // +++ MM forfait
	private Integer mmabonnement; // +++ MM forfait
	private Long mmdate_inscription; // +++ MM forfait
	private Integer mmperiode_essai; // +++ MM forfait
	private Long mmdate_facture1; // +++ MM forfait
	private String mmmotif_non_ordre; // +++ MM forfait
	private Long mmdate_deinscription; // +++ MM forfait
	private Long mmdate_deces; // +++ MM forfait
	private Integer mmchoix_desabonnement; // +++ MM forfait
	private Long mmdate_fin_forfait; // +++ MM forfait
	private Long mmdate_fin_facture; // +++ MM forfait
	private Long mmdate_mutation_mutuelle; // +++ MM forfait
	private String mmmutuelle_precedent; // +++ MM forfait
	private Integer mmtype_inscription; // +++ MM forfait
	private String mmnom_anc_mm; // +++ MM forfait
	private Long mmflag_ordre_mut; // +++ MM forfait
	private String mmmotif_desabonnement; // +++ MM forfait
	private Long mmdate_non_ordre; // +++ MM forfait
	private Long mmdate_ordre; // +++ MM forfait
	private Long mmflag_mutuelle_suspens; // +++ MM forfait
	// private Long flag_soins_palliatif; // +++ Car on va checker directement
	// private Long flaglienconsnational; // +++ Car on va checker directement
	// private Long flag_attestation_nonpaye;
	// private Integer choixmodepaiement;
	private Integer nbr_enfant;
	private String numcpas;
	private Integer lieunaissance;
	// private Long datelectureeid;
	private String pays;
	private String volonte_memo; // +++ texte libre dans les volontés (champ
									// note)
	private Long mmflag_pass_diabete; // +++ MM forfait
	private Long mmflag_dmgplus; // +++ MM forfait
	private Long mmflag_dmg2ans; // +++ MM forfait
	// private String id_agenda;
	// private Long datedemandeeuthanasie;
	// private String signaturederniercontact;
	private String remarque; // +++ remarque médicale dans dossier
	private Long date_remarque;
	// private Long flagremarque;
	// private Long acceseid;
	private Long tsdiabete;
	private Long tsrein;
	private Long prediabete;
	private Long conventiondiabete;
	private Integer typenumext; // +++ type de N° de tiers (rubrique NUMCPAS
								// 2602)
	private Integer sousgroupesang;
	// private Long envoiantlibre;
	private Long date_dr; // +++ pour module obstétrique
	private String id_service_gyne_admin; // +++ pour module obstétrique
	private String id_service_gyne_ante; // +++ pour module obstétrique
	private Long date_accou; // +++ pour module obstétrique
	// private Long flagvalidedmg;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fiche = resultset.getString("fiche");
		this.nom = resultset.getString("nom");
		this.prenom = resultset.getString("prenom");
		this.naissance = Tools.cnvTime2Long(resultset, "naissance");
		this.typepatient = resultset.getLong("typepatient");
		this.religion = resultset.getLong("religion");
		this.etatcivil = resultset.getLong("etatcivil");
		this.adresse = resultset.getString("adresse");
		this.ville = resultset.getString("ville");
		this.code = resultset.getString("code");
		this.telephone1 = resultset.getString("telephone1");
		this.telephone2 = resultset.getString("telephone2");
		this.adresse2 = resultset.getString("adresse2");
		this.ville2 = resultset.getString("ville2");
		this.code2 = resultset.getString("code2");
		this.fax = resultset.getString("fax");
		this.fax2 = resultset.getString("fax2");
		this.gsm = resultset.getString("gsm");
		this.mutuelle = resultset.getLong("mutuelle");
		this.nummutuelle = resultset.getString("nummutuelle");
		this.status = resultset.getLong("status");
		this.sang = resultset.getInt("sang");
		this.fichepat = resultset.getString("fichepat");
		this.langue = resultset.getInt("langue");
		this.gestionpatient = resultset.getInt("gestionpatient");
		this.datedeces = Tools.cnvTime2Long(resultset, "datedeces");
		this.motifdeces = resultset.getLong("motifdeces");
		this.prevenir = resultset.getString("prevenir");
		this.nomepoux = resultset.getString("nomepoux");
		this.titre5 = resultset.getString("titre5");
		this.titre4 = resultset.getString("titre4");
		this.titre3 = resultset.getString("titre3");
		this.titre2 = resultset.getString("titre2");
		this.titre1 = resultset.getString("titre1");
		this.datedebutass = Tools.cnvTime2Long(resultset, "datedebutass");
		this.datecreation = Tools.cnvTime2Long(resultset, "datecreation");
		this.mail = resultset.getString("mail");
		this.gestionnaire_pat = resultset.getString("gestionnaire_pat");
		this.prenom2 = resultset.getString("prenom2");
		this.sexe = resultset.getInt("sexe");
		this.deces = resultset.getInt("deces");
		this.antecedent = resultset.getString("antecedent");
		this.notebin = resultset.getString("notebin");
		this.cpas_nation_naissance = resultset.getInt("cpas_nation_naissance");
		this.cpas_nation_pere = resultset.getInt("cpas_nation_pere");
		this.cpas_acces_soins = resultset.getInt("cpas_acces_soins");
		this.cpas_statut_fami = resultset.getInt("cpas_statut_fami");
		this.cpas_statut_etude = resultset.getInt("cpas_statut_etude");
		this.cpas_statut_social = resultset.getInt("cpas_statut_social");
		this.cpas_tabac = resultset.getInt("cpas_tabac");
		this.cpas_mutuelle = resultset.getInt("cpas_mutuelle");
		this.idprofession = resultset.getString("idprofession");
		this.responsable_dossier_libre = resultset.getString("responsable_dossier_libre");
		this.tarif_attestation = resultset.getInt("tarif_attestation");
		this.national = resultset.getString("national");
		// this.photo = Objet OLE ("photo");
		this.idadressefacture = resultset.getString("idadressefacture");
		this.dateenvoiesumehr = Tools.cnvTime2Long(resultset, "dateenvoiesumehr");
		this.flag_trajetsoins = resultset.getLong("flag_trajetsoins");
		this.sel_transfusion = resultset.getInt("sel_transfusion");
		this.sel_reanimation = resultset.getInt("sel_reanimation");
		this.sel_intubation = resultset.getInt("sel_intubation");
		this.sel_euthanasie = resultset.getInt("sel_euthanasie");
		this.sel_donorgane = resultset.getInt("sel_donorgane");
		this.sel_screening = resultset.getInt("sel_screening");
		this.sel_refusvaccin = resultset.getInt("sel_refusvaccin");
		this.flaglientherapeutique = resultset.getLong("flaglientherapeutique");
		this.type_paiement = resultset.getInt("type_paiement");
		this.maisonmedical = resultset.getLong("maisonmedical");
		this.flag_assure_mut = resultset.getLong("flag_assure_mut");
		this.inscrit_rsw = resultset.getLong("inscrit_rsw");
		this.numcarteeid = resultset.getString("numcarteeid");
		this.mmforfait = resultset.getInt("mmforfait");
		this.mmabonnement = resultset.getInt("mmabonnement");
		this.mmdate_inscription = Tools.cnvTime2Long(resultset, "mmdate_inscription");
		this.mmperiode_essai = resultset.getInt("mmperiode_essai");
		this.mmdate_facture1 = Tools.cnvTime2Long(resultset, "mmdate_facture1");
		this.mmmotif_non_ordre = resultset.getString("mmmotif_non_ordre");
		this.mmdate_deinscription = Tools.cnvTime2Long(resultset, "mmdate_deinscription");
		this.mmdate_deces = Tools.cnvTime2Long(resultset, "mmdate_deces");
		this.mmchoix_desabonnement = resultset.getInt("mmchoix_desabonnement");
		this.mmdate_fin_forfait = Tools.cnvTime2Long(resultset, "mmdate_fin_forfait");
		this.mmdate_fin_facture = Tools.cnvTime2Long(resultset, "mmdate_fin_facture");
		this.mmdate_mutation_mutuelle = Tools.cnvTime2Long(resultset, "mmdate_mutation_mutuelle");
		this.mmmutuelle_precedent = resultset.getString("mmmutuelle_precedent");
		this.mmtype_inscription = resultset.getInt("mmtype_inscription");
		this.mmnom_anc_mm = resultset.getString("mmnom_anc_mm");
		this.mmflag_ordre_mut = resultset.getLong("mmflag_ordre_mut");
		this.mmmotif_desabonnement = resultset.getString("mmmotif_desabonnement");
		this.mmdate_non_ordre = Tools.cnvTime2Long(resultset, "mmdate_non_ordre");
		this.mmdate_ordre = Tools.cnvTime2Long(resultset, "mmdate_ordre");
		this.mmflag_mutuelle_suspens = resultset.getLong("mmflag_mutuelle_suspens");
		this.nbr_enfant = resultset.getInt("nbr_enfant");
		this.numcpas = resultset.getString("numcpas");
		this.lieunaissance = resultset.getInt("lieunaissance");
		this.pays = resultset.getString("pays");
		this.volonte_memo = resultset.getString("volonte_memo");
		this.mmflag_pass_diabete = resultset.getLong("mmflag_pass_diabete");
		this.mmflag_dmgplus = resultset.getLong("mmflag_dmgplus");
		this.mmflag_dmg2ans = resultset.getLong("mmflag_dmg2ans");
		this.remarque = resultset.getString("remarque");
		this.date_remarque = Tools.cnvTime2Long(resultset, "date_remarque");
		this.tsdiabete = resultset.getLong("tsdiabete");
		this.tsrein = resultset.getLong("tsrein");
		this.prediabete = resultset.getLong("prediabete");
		this.conventiondiabete = resultset.getLong("conventiondiabete");
		this.typenumext = resultset.getInt("typenumext");
		this.sousgroupesang = resultset.getInt("sousgroupesang");
		this.date_dr = Tools.cnvTime2Long(resultset, "date_dr");
		this.id_service_gyne_admin = resultset.getString("id_service_gyne_admin");
		this.id_service_gyne_ante = resultset.getString("id_service_gyne_ante");
		this.date_accou = Tools.cnvTime2Long(resultset, "date_accou");
	}

	public Integer getId_() {
		return id_;
	}

	public String getFiche() {
		return fiche;
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public Long getNaissance() {
		return naissance;
	}

	public Long getTypepatient() {
		return typepatient;
	}

	public Long getReligion() {
		return religion;
	}

	public Long getEtatcivil() {
		return etatcivil;
	}

	public String getAdresse() {
		return adresse;
	}

	public String getVille() {
		return ville;
	}

	public String getCode() {
		return code;
	}

	public String getTelephone1() {
		return telephone1;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public String getAdresse2() {
		return adresse2;
	}

	public String getVille2() {
		return ville2;
	}

	public String getCode2() {
		return code2;
	}

	public String getFax() {
		return fax;
	}

	public String getFax2() {
		return fax2;
	}

	public String getGsm() {
		return gsm;
	}

	public Long getMutuelle() {
		return mutuelle;
	}

	public String getNummutuelle() {
		return nummutuelle;
	}

	public Long getStatus() {
		return status;
	}

	public Integer getSang() {
		return sang;
	}

	public String getFichepat() {
		return fichepat;
	}

	public Integer getLangue() {
		return langue;
	}

	public Integer getGestionpatient() {
		return gestionpatient;
	}

	public Long getDatedeces() {
		return datedeces;
	}

	public Long getMotifdeces() {
		return motifdeces;
	}

	public String getPrevenir() {
		return prevenir;
	}

	public String getNomepoux() {
		return nomepoux;
	}

	public String getTitre5() {
		return titre5;
	}

	public String getTitre4() {
		return titre4;
	}

	public String getTitre3() {
		return titre3;
	}

	public String getTitre2() {
		return titre2;
	}

	public String getTitre1() {
		return titre1;
	}

	public Long getDatedebutass() {
		return datedebutass;
	}

	public Long getDatecreation() {
		return datecreation;
	}

	public String getMail() {
		return mail;
	}

	public String getGestionnaire_pat() {
		return gestionnaire_pat;
	}

	public String getPrenom2() {
		return prenom2;
	}

	public Integer getSexe() {
		return sexe;
	}

	public Integer getDeces() {
		return deces;
	}

	public String getAntecedent() {
		return antecedent;
	}

	public String getNotebin() {
		return notebin;
	}

	public Integer getCpas_nation_naissance() {
		return cpas_nation_naissance;
	}

	public Integer getCpas_nation_pere() {
		return cpas_nation_pere;
	}

	public Integer getCpas_acces_soins() {
		return cpas_acces_soins;
	}

	public Integer getCpas_statut_fami() {
		return cpas_statut_fami;
	}

	public Integer getCpas_statut_etude() {
		return cpas_statut_etude;
	}

	public Integer getCpas_statut_social() {
		return cpas_statut_social;
	}

	public Integer getCpas_tabac() {
		return cpas_tabac;
	}

	public Integer getCpas_mutuelle() {
		return cpas_mutuelle;
	}

	public String getIdprofession() {
		return idprofession;
	}

	public String getResponsable_dossier_libre() {
		return responsable_dossier_libre;
	}

	public Integer getTarif_attestation() {
		return tarif_attestation;
	}

	public String getNational() {
		return national;
	}

	public String getIdadressefacture() {
		return idadressefacture;
	}

	public Long getDateenvoiesumehr() {
		return dateenvoiesumehr;
	}

	public Long getFlag_trajetsoins() {
		return flag_trajetsoins;
	}

	public Integer getSel_transfusion() {
		return sel_transfusion;
	}

	public Integer getSel_reanimation() {
		return sel_reanimation;
	}

	public Integer getSel_intubation() {
		return sel_intubation;
	}

	public Integer getSel_euthanasie() {
		return sel_euthanasie;
	}

	public Integer getSel_donorgane() {
		return sel_donorgane;
	}

	public Integer getSel_screening() {
		return sel_screening;
	}

	public Integer getSel_refusvaccin() {
		return sel_refusvaccin;
	}

	public Long getFlaglientherapeutique() {
		return flaglientherapeutique;
	}

	public Integer getType_paiement() {
		return type_paiement;
	}

	public Long getMaisonmedical() {
		return maisonmedical;
	}

	public Long getFlag_assure_mut() {
		return flag_assure_mut;
	}

	public Long getInscrit_rsw() {
		return inscrit_rsw;
	}

	public String getNumcarteeid() {
		return numcarteeid;
	}

	public Integer getMmforfait() {
		return mmforfait;
	}

	public Integer getMmabonnement() {
		return mmabonnement;
	}

	public Long getMmdate_inscription() {
		return mmdate_inscription;
	}

	public Integer getMmperiode_essai() {
		return mmperiode_essai;
	}

	public Long getMmdate_facture1() {
		return mmdate_facture1;
	}

	public String getMmmotif_non_ordre() {
		return mmmotif_non_ordre;
	}

	public Long getMmdate_deinscription() {
		return mmdate_deinscription;
	}

	public Long getMmdate_deces() {
		return mmdate_deces;
	}

	public Integer getMmchoix_desabonnement() {
		return mmchoix_desabonnement;
	}

	public Long getMmdate_fin_forfait() {
		return mmdate_fin_forfait;
	}

	public Long getMmdate_fin_facture() {
		return mmdate_fin_facture;
	}

	public Long getMmdate_mutation_mutuelle() {
		return mmdate_mutation_mutuelle;
	}

	public String getMmmutuelle_precedent() {
		return mmmutuelle_precedent;
	}

	public Integer getMmtype_inscription() {
		return mmtype_inscription;
	}

	public String getMmnom_anc_mm() {
		return mmnom_anc_mm;
	}

	public Long getMmflag_ordre_mut() {
		return mmflag_ordre_mut;
	}

	public String getMmmotif_desabonnement() {
		return mmmotif_desabonnement;
	}

	public Long getMmdate_non_ordre() {
		return mmdate_non_ordre;
	}

	public Long getMmdate_ordre() {
		return mmdate_ordre;
	}

	public Long getMmflag_mutuelle_suspens() {
		return mmflag_mutuelle_suspens;
	}

	public Integer getNbr_enfant() {
		return nbr_enfant;
	}

	public String getNumcpas() {
		return numcpas;
	}

	public Integer getLieunaissance() {
		return lieunaissance;
	}

	public String getPays() {
		return pays;
	}

	public String getVolonte_memo() {
		return volonte_memo;
	}

	public Long getMmflag_pass_diabete() {
		return mmflag_pass_diabete;
	}

	public Long getMmflag_dmgplus() {
		return mmflag_dmgplus;
	}

	public Long getMmflag_dmg2ans() {
		return mmflag_dmg2ans;
	}

	public String getRemarque() {
		return remarque;
	}

	public Long getDate_remarque() {
		return date_remarque;
	}

	public Long getTsdiabete() {
		return tsdiabete;
	}

	public Long getTsrein() {
		return tsrein;
	}

	public Long getPrediabete() {
		return prediabete;
	}

	public Long getConventiondiabete() {
		return conventiondiabete;
	}

	public Integer getTypenumext() {
		return typenumext;
	}

	public Integer getSousgroupesang() {
		return sousgroupesang;
	}

	public Long getDate_dr() {
		return date_dr;
	}

	public String getId_service_gyne_admin() {
		return id_service_gyne_admin;
	}

	public String getId_service_gyne_ante() {
		return id_service_gyne_ante;
	}

	public Long getDate_accou() {
		return date_accou;
	}

}
