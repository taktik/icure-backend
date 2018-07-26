package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ElementsoinsEpi {

	private Integer id_;
	private String fichecontact;
	private String reference_fr;
	private String branche_fr;
	private Integer niveau;
	private String id_es_reference;
	// private String datemodifie;
	// private Integer typecode;
	// private Long ibui;
	// private String icp2;
	// private String icd10;
	// private Integer type_allergie;
	// private Long type_risque_autre;
	// private Long type_risque_social;
	// private Long type_risque_prof;
	// private Long type_risque_pulmonaire;
	// private Long type_risque_osteo;
	// private Long type_risque_cancer;
	// private Long type_risque_cardio;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.reference_fr = resultset.getString("reference_fr");
		this.branche_fr = resultset.getString("branche_fr");
		this.niveau = resultset.getInt("niveau");
		this.id_es_reference = resultset.getString("id_es_reference");
	}

	public Integer getId_() {
		return id_;
	}

	public String getFichecontact() {
		return fichecontact;
	}

	public String getReference_fr() {
		return reference_fr;
	}

	public String getBranche_fr() {
		return branche_fr;
	}

	public Integer getNiveau() {
		return niveau;
	}

	public String getId_es_reference() {
		return id_es_reference;
	}

}
