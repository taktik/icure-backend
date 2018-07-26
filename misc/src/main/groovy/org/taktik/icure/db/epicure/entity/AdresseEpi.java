package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdresseEpi {

	private Integer id_;
	private String fichecontact;
	private String nomadr;
	private String prenomadr;
	private String adresseadr;
	private String codeadr;
	private String villeadr;
	private String nihii;
	private String teladr;
	private String tel2adr;
	private String faxadr;
	private String noteadr;
	private String titre;
	private Integer logiciel;
	private Integer support;
	private String libelle;
	private String inami;
	private Integer envoi;
	private String mail;
	private String service;
	private String responsable;
	private String gsm;
	private String datemodifie;
	private String idclasseadr;
	private Integer sexe;
	private Long flag_courrier_envoi;
	private String lien_mexi;
	private String chemin_certificat;
	private String etk1;
	private String etk2;
	private String etk3;
	private String police;
	private String niss;
	private Integer type_niss_nihii;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.nomadr = resultset.getString("nomadr");
		this.prenomadr = resultset.getString("prenomadr");
		this.adresseadr = resultset.getString("adresseadr");
		this.codeadr = resultset.getString("codeadr");
		this.villeadr = resultset.getString("villeadr");
		this.nihii = resultset.getString("nihii");
		this.teladr = resultset.getString("teladr");
		this.tel2adr = resultset.getString("tel2adr");
		this.faxadr = resultset.getString("faxadr");
		this.noteadr = resultset.getString("noteadr");
		this.titre = resultset.getString("titre");
		this.logiciel = resultset.getInt("logiciel");
		this.support = resultset.getInt("support");
		this.libelle = resultset.getString("libelle");
		this.inami = resultset.getString("inami");
		this.envoi = resultset.getInt("envoi");
		this.mail = resultset.getString("mail");
		this.service = resultset.getString("service");
		this.responsable = resultset.getString("responsable");
		this.gsm = resultset.getString("gsm");
		this.datemodifie = resultset.getString("datemodifie");
		this.idclasseadr = resultset.getString("idclasseadr");
		this.sexe = resultset.getInt("sexe");
		this.flag_courrier_envoi = resultset.getLong("flag_courrier_envoi");
		this.lien_mexi = resultset.getString("lien_mexi");
		this.chemin_certificat = resultset.getString("chemin_certificat");
		this.etk1 = resultset.getString("etk1");
		this.etk2 = resultset.getString("etk2");
		this.etk3 = resultset.getString("etk3");
		this.police = resultset.getString("police");
		this.niss = resultset.getString("niss");
		this.type_niss_nihii = resultset.getInt("type_niss_nihii");
	}

	public Integer getId_() {
		return id_;
	}

	public String getFichecontact() {
		return fichecontact;
	}

	public String getNomadr() {
		return nomadr;
	}

	public String getPrenomadr() {
		return prenomadr;
	}

	public String getAdresseadr() {
		return adresseadr;
	}

	public String getCodeadr() {
		return codeadr;
	}

	public String getVilleadr() {
		return villeadr;
	}

	public String getNihii() {
		return nihii;
	}

	public String getTeladr() {
		return teladr;
	}

	public String getTel2adr() {
		return tel2adr;
	}

	public String getFaxadr() {
		return faxadr;
	}

	public String getNoteadr() {
		return noteadr;
	}

	public String getTitre() {
		return titre;
	}

	public Integer getLogiciel() {
		return logiciel;
	}

	public Integer getSupport() {
		return support;
	}

	public String getLibelle() {
		return libelle;
	}

	public String getInami() {
		return inami;
	}

	public Integer getEnvoi() {
		return envoi;
	}

	public String getMail() {
		return mail;
	}

	public String getService() {
		return service;
	}

	public String getResponsable() {
		return responsable;
	}

	public String getGsm() {
		return gsm;
	}

	public String getDatemodifie() {
		return datemodifie;
	}

	public String getIdclasseadr() {
		return idclasseadr;
	}

	public Integer getSexe() {
		return sexe;
	}

	public Long getFlag_courrier_envoi() {
		return flag_courrier_envoi;
	}

	public String getLien_mexi() {
		return lien_mexi;
	}

	public String getChemin_certificat() {
		return chemin_certificat;
	}

	public String getEtk1() {
		return etk1;
	}

	public String getEtk2() {
		return etk2;
	}

	public String getEtk3() {
		return etk3;
	}

	public String getPolice() {
		return police;
	}

	public String getNiss() {
		return niss;
	}

	public Integer getType_niss_nihii() {
		return type_niss_nihii;
	}

}
