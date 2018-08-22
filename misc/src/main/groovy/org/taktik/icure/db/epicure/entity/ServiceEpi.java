package org.taktik.icure.db.epicure.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.taktik.icure.db.epicure.Tools;

public class ServiceEpi {

	private Integer id_;
	private String fichecontact;
	// private String datemodifie;
	private Long dateheure_enrg;
	private Long dateheure_valeur;
	private String auteur;
	private String unite;
	private String valeur_texte;
	private Integer type_sous_saisie;
	private String valeur_rtf;
	// // Objet OLEvaleur_binaire;
	// private Long flag_alerte;
	private Long flag_path;
	private Long flag_export;
	// private Integer gravite;
	private String ibui;
	private String commentaire;
	private Long rappel;
	private Long date_rappel;
	// private String medecin_rappel;
	private String libelle_rappel;
	// private Integer nbr_jour_avant;
	private Integer statut_rappel;
	private Integer type_saisie;
	private Integer type_image;
	private String id_contact;
	private String fichepat;
	// private Long num_place;
	// private Long flag_valider;
	private String id_classe_item;
	// private Integer flag_importance;
	private String codeicpc;
	private Integer type_tache;
	private Long flag_personnel;
	// private Long flag_lien;
	// private Long flag_rappelenvoye;
	// private Long date_envoi_rappel;
	// private Long mailingenvoyetache;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.dateheure_enrg = Tools.cnvTime2Long(resultset, "dateheure_enrg");
		this.dateheure_valeur = Tools.cnvTime2Long(resultset, "dateheure_valeur");
		this.auteur = resultset.getString("auteur");
		this.unite = resultset.getString("unite");
		this.valeur_texte = resultset.getString("valeur_texte");
		this.type_sous_saisie = resultset.getInt("type_sous_saisie");
		this.valeur_rtf = resultset.getString("valeur_rtf");
		this.flag_path = resultset.getLong("flag_path");
		this.flag_export = resultset.getLong("flag_export");
		this.ibui = resultset.getString("ibui");
		this.commentaire = resultset.getString("commentaire");
		this.rappel = resultset.getLong("rappel");
		this.date_rappel = Tools.cnvTime2Long(resultset, "date_rappel");
		this.libelle_rappel = resultset.getString("libelle_rappel");
		this.statut_rappel = resultset.getInt("statut_rappel");
		this.type_saisie = resultset.getInt("type_saisie");
		this.type_image = resultset.getInt("type_image");
		this.id_contact = resultset.getString("id_contact");
		this.fichepat = resultset.getString("fichepat");
		this.id_classe_item = resultset.getString("id_classe_item");
		this.codeicpc = resultset.getString("codeicpc");
		this.type_tache = resultset.getInt("type_tache");
		this.flag_personnel = resultset.getLong("flag_personnel");
	}

	public Integer getId_() {
		return id_;
	}

	public void setId_(Integer id_) {
		this.id_ = id_;
	}

	public String getFichecontact() {
		return fichecontact;
	}

	public void setFichecontact(String fichecontact) {
		this.fichecontact = fichecontact;
	}

	public Long getDateheure_enrg() {
		return dateheure_enrg;
	}

	public void setDateheure_enrg(Long dateheure_enrg) {
		this.dateheure_enrg = dateheure_enrg;
	}

	public Long getDateheure_valeur() {
		return dateheure_valeur;
	}

	public void setDateheure_valeur(Long dateheure_valeur) {
		this.dateheure_valeur = dateheure_valeur;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getUnite() {
		return unite;
	}

	public void setUnite(String unite) {
		this.unite = unite;
	}

	public String getValeur_texte() {
		return valeur_texte;
	}

	public void setValeur_texte(String valeur_texte) {
		this.valeur_texte = valeur_texte;
	}

	public Integer getType_sous_saisie() {
		return type_sous_saisie;
	}

	public void setType_sous_saisie(Integer type_sous_saisie) {
		this.type_sous_saisie = type_sous_saisie;
	}

	public String getValeur_rtf() {
		return valeur_rtf;
	}

	public void setValeur_rtf(String valeur_rtf) {
		this.valeur_rtf = valeur_rtf;
	}

	public Long getFlag_path() {
		return flag_path;
	}

	public void setFlag_path(Long flag_path) {
		this.flag_path = flag_path;
	}

	public Long getFlag_export() {
		return flag_export;
	}

	public void setFlag_export(Long flag_export) {
		this.flag_export = flag_export;
	}

	public String getIbui() {
		return ibui;
	}

	public void setIbui(String ibui) {
		this.ibui = ibui;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Long getRappel() {
		return rappel;
	}

	public void setRappel(Long rappel) {
		this.rappel = rappel;
	}

	public Long getDate_rappel() {
		return date_rappel;
	}

	public void setDate_rappel(Long date_rappel) {
		this.date_rappel = date_rappel;
	}

	public String getLibelle_rappel() {
		return libelle_rappel;
	}

	public void setLibelle_rappel(String libelle_rappel) {
		this.libelle_rappel = libelle_rappel;
	}

	public Integer getStatut_rappel() {
		return statut_rappel;
	}

	public void setStatut_rappel(Integer statut_rappel) {
		this.statut_rappel = statut_rappel;
	}

	public Integer getType_saisie() {
		return type_saisie;
	}

	public void setType_saisie(Integer type_saisie) {
		this.type_saisie = type_saisie;
	}

	public Integer getType_image() {
		return type_image;
	}

	public void setType_image(Integer type_image) {
		this.type_image = type_image;
	}

	public String getId_contact() {
		return id_contact;
	}

	public void setId_contact(String id_contact) {
		this.id_contact = id_contact;
	}

	public String getFichepat() {
		return fichepat;
	}

	public void setFichepat(String fichepat) {
		this.fichepat = fichepat;
	}

	public String getId_classe_item() {
		return id_classe_item;
	}

	public void setId_classe_item(String id_classe_item) {
		this.id_classe_item = id_classe_item;
	}

	public String getCodeicpc() {
		return codeicpc;
	}

	public void setCodeicpc(String codeicpc) {
		this.codeicpc = codeicpc;
	}

	public Integer getType_tache() {
		return type_tache;
	}

	public void setType_tache(Integer type_tache) {
		this.type_tache = type_tache;
	}

	public Long getFlag_personnel() {
		return flag_personnel;
	}

	public void setFlag_personnel(Long flag_personnel) {
		this.flag_personnel = flag_personnel;
	}

}
