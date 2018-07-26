package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactEpi {

	private Integer id_;
	private String fichecontact;
	private String datemodifie;
	private String fichepat;
	private String auteur;
	private Long dateheure_enrg;
	private Long dateheure_valeur;
	private String canevas_item;
	private Integer type_contact;
	private Long version;
	private String auteur_modifier;
	private String commentaire;
	private Long flag_valider;
	private Long flag_export;
	private Integer flag_importance;
	private Integer type_sous_contact;
	private Long flag_visible;
	private Long flag_lien;
	private Long flagservicevisible;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.datemodifie = resultset.getString("datemodifie");
		this.fichepat = resultset.getString("fichepat");
		this.auteur = resultset.getString("auteur");
		this.dateheure_enrg = Tools.cnvTime2Long(resultset, "dateheure_enrg");
		this.dateheure_valeur = Tools.cnvTime2Long(resultset, "dateheure_valeur");
		this.canevas_item = resultset.getString("canevas_item");
		this.type_contact = resultset.getInt("type_contact");
		this.version = resultset.getLong("version");
		this.auteur_modifier = resultset.getString("auteur_modifier");
		this.commentaire = resultset.getString("commentaire");
		this.flag_valider = resultset.getLong("flag_valider");
		this.flag_export = resultset.getLong("flag_export");
		this.flag_importance = resultset.getInt("flag_importance");
		this.type_sous_contact = resultset.getInt("type_sous_contact");
		this.flag_visible = resultset.getLong("flag_visible");
		this.flag_lien = resultset.getLong("flag_lien");
		this.flagservicevisible = resultset.getLong("flagservicevisible");
	}

	public Integer getId_() {
		return id_;
	}

	public String getFichecontact() {
		return fichecontact;
	}

	public String getDatemodifie() {
		return datemodifie;
	}

	public String getFichepat() {
		return fichepat;
	}

	public String getAuteur() {
		return auteur;
	}

	public Long getDateheure_enrg() {
		return dateheure_enrg;
	}

	public Long getDateheure_valeur() {
		return dateheure_valeur;
	}

	public String getCanevas_item() {
		return canevas_item;
	}

	public Integer getType_contact() {
		return type_contact;
	}

	public Long getVersion() {
		return version;
	}

	public String getAuteur_modifier() {
		return auteur_modifier;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public Long getFlag_valider() {
		return flag_valider;
	}

	public Long getFlag_export() {
		return flag_export;
	}

	public Integer getFlag_importance() {
		return flag_importance;
	}

	public Integer getType_sous_contact() {
		return type_sous_contact;
	}

	public Long getFlag_visible() {
		return flag_visible;
	}

	public Long getFlag_lien() {
		return flag_lien;
	}

	public Long getFlagservicevisible() {
		return flagservicevisible;
	}

}
