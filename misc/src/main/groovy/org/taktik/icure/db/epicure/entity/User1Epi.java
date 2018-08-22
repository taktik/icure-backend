package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User1Epi {

	private Integer id_;
	private String signature; // +++
	private String nom; // +++
	private String prenom; // +++
	private String inami; // +++
	private String adresse; // +++
	private String ville; // +++
	private String code; // +++
	private String telephone; // +++
	private String fax; // +++
	private String email; // +++
	private String password; // +++
	private Long flag_responsable; // +++
	private Long flag_actif; // +++
	private String national; // +++
	private String chemin_certificat; // +++
	private String chaine_secret_certificat; // +++

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.signature = resultset.getString("signature");
		this.nom = resultset.getString("nom");
		this.prenom = resultset.getString("prenom");
		this.inami = resultset.getString("inami");
		this.adresse = resultset.getString("adresse");
		this.ville = resultset.getString("ville");
		this.code = resultset.getString("code");
		this.telephone = resultset.getString("telephone");
		this.fax = resultset.getString("fax");
		this.email = resultset.getString("email");
		this.password = resultset.getString("password");
		this.flag_responsable = resultset.getLong("flag_responsable");
		this.flag_actif = resultset.getLong("flag_actif");
		this.national = resultset.getString("national");
		this.chemin_certificat = resultset.getString("chemin_certificat");
		this.chaine_secret_certificat = resultset.getString("chaine_secret_certificat");
	}

	public Integer getId_() {
		return id_;
	}

	public void setId_(Integer id_) {
		this.id_ = id_;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getInami() {
		return inami;
	}

	public void setInami(String inami) {
		this.inami = inami;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getFlag_responsable() {
		return flag_responsable;
	}

	public void setFlag_responsable(Long flag_responsable) {
		this.flag_responsable = flag_responsable;
	}

	public Long getFlag_actif() {
		return flag_actif;
	}

	public void setFlag_actif(Long flag_actif) {
		this.flag_actif = flag_actif;
	}

	public String getNational() {
		return national;
	}

	public void setNational(String national) {
		this.national = national;
	}

	public String getChemin_certificat() {
		return chemin_certificat;
	}

	public void setChemin_certificat(String chemin_certificat) {
		this.chemin_certificat = chemin_certificat;
	}

	public String getChaine_secret_certificat() {
		return chaine_secret_certificat;
	}

	public void setChaine_secret_certificat(String chaine_secret_certificat) {
		this.chaine_secret_certificat = chaine_secret_certificat;
	}

}
