package org.taktik.icure.db.epicure.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.taktik.icure.db.epicure.Tools;

public class ContactEpi {

	private Integer id_;
	private String fichecontact;
	// private String datemodifie;
	private String fichepat;
	private String auteur;
	// private Long dateheure_enrg;
	private Long dateheure_valeur;
	// private String canevas_item;
	private Integer type_contact;
	// private Long version;
	// private String auteur_modifier;
	private String commentaire; // +++ important à récupérer : cela doit être le
								// contenu de la box dans la time line
	// private Long flag_valider;
	private Long flag_export;
	private Integer flag_importance;
	private Integer type_sous_contact; // +++ mais seulement dans les types
										// existants dans Medispring
	// private Long flag_visible;
	// private Long flag_lien;
	private Long flagservicevisible; // +++ si coché = visible QUE DE SON AUTEUR

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.fichepat = resultset.getString("fichepat");
		this.auteur = resultset.getString("auteur");
		this.dateheure_valeur = Tools.cnvTime2Long(resultset, "dateheure_valeur");
		this.type_contact = resultset.getInt("type_contact");
		this.commentaire = resultset.getString("commentaire");
		this.flag_export = resultset.getLong("flag_export");
		this.flag_importance = resultset.getInt("flag_importance");
		this.type_sous_contact = resultset.getInt("type_sous_contact");
		this.flagservicevisible = resultset.getLong("flagservicevisible");
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

	public String getFichepat() {
		return fichepat;
	}

	public void setFichepat(String fichepat) {
		this.fichepat = fichepat;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public Long getDateheure_valeur() {
		return dateheure_valeur;
	}

	public void setDateheure_valeur(Long dateheure_valeur) {
		this.dateheure_valeur = dateheure_valeur;
	}

	public Integer getType_contact() {
		return type_contact;
	}

	public void setType_contact(Integer type_contact) {
		this.type_contact = type_contact;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Long getFlag_export() {
		return flag_export;
	}

	public void setFlag_export(Long flag_export) {
		this.flag_export = flag_export;
	}

	public Integer getFlag_importance() {
		return flag_importance;
	}

	public void setFlag_importance(Integer flag_importance) {
		this.flag_importance = flag_importance;
	}

	public Integer getType_sous_contact() {
		return type_sous_contact;
	}

	public void setType_sous_contact(Integer type_sous_contact) {
		this.type_sous_contact = type_sous_contact;
	}

	public Long getFlagservicevisible() {
		return flagservicevisible;
	}

	public void setFlagservicevisible(Long flagservicevisible) {
		this.flagservicevisible = flagservicevisible;
	}

}
