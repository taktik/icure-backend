package org.taktik.icure.db.epicure.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Contact_itemEpi {

	private Integer id_;
	private String fichecontact;
	// private String datemodifie;
	private String libelle_fr;
	// private Long flag_transf_courrier;
	// private Long flag_saisie_obligatoire;
	private Integer type_saisie;
	private Integer type_sous_saisie;
	// private String unite;
	// private Long flag_epicure;
	private Integer type_contact; // +++ pour trouver les items perso
	// // Objet OLEvaleur_binaire;
	// private String valeur_rtf_fr;
	// private Long flag_bilan_suivre;
	// private Integer categorie_bilan_contact;
	// private Integer categorie_bilan_sang;
	private String id_document; // +++ lien vers un doc word
	// private Integer id_code_constance;
	// private Long type_defaut;
	// private Long flag_substitution;
	// private String codification;
	private String chemin_canevas; // +++ lien vers fichier externe
	private String id_item_classe; // +++ lien ID vers le fichier CONTACT_CLASSE
	// // Objet OLEimage_bouton;
	// private Long flag_export;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.libelle_fr = resultset.getString("libelle_fr");
		this.type_saisie = resultset.getInt("type_saisie");
		this.type_sous_saisie = resultset.getInt("type_sous_saisie");
		this.type_contact = resultset.getInt("type_contact");
		this.id_document = resultset.getString("id_document");
		this.chemin_canevas = resultset.getString("chemin_canevas");
		this.id_item_classe = resultset.getString("id_item_classe");
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

	public String getLibelle_fr() {
		return libelle_fr;
	}

	public void setLibelle_fr(String libelle_fr) {
		this.libelle_fr = libelle_fr;
	}

	public Integer getType_saisie() {
		return type_saisie;
	}

	public void setType_saisie(Integer type_saisie) {
		this.type_saisie = type_saisie;
	}

	public Integer getType_sous_saisie() {
		return type_sous_saisie;
	}

	public void setType_sous_saisie(Integer type_sous_saisie) {
		this.type_sous_saisie = type_sous_saisie;
	}

	public Integer getType_contact() {
		return type_contact;
	}

	public void setType_contact(Integer type_contact) {
		this.type_contact = type_contact;
	}

	public String getId_document() {
		return id_document;
	}

	public void setId_document(String id_document) {
		this.id_document = id_document;
	}

	public String getChemin_canevas() {
		return chemin_canevas;
	}

	public void setChemin_canevas(String chemin_canevas) {
		this.chemin_canevas = chemin_canevas;
	}

	public String getId_item_classe() {
		return id_item_classe;
	}

	public void setId_item_classe(String id_item_classe) {
		this.id_item_classe = id_item_classe;
	}

}
