package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfessiEpi {

	private Integer id_;
	private String fichecontact;
	private String nomprofessfr;
	private String datemodifie;
	private String idcode;

	public void init(ResultSet resultset) throws SQLException {
		this.id_ = resultset.getRow();
		this.fichecontact = resultset.getString("fichecontact");
		this.nomprofessfr = resultset.getString("nomprofessfr");
		this.datemodifie = resultset.getString("datemodifie");
		this.idcode = resultset.getString("idcode");
	}

	public Integer getId_() {
		return id_;
	}

	public String getFichecontact() {
		return fichecontact;
	}

	public String getNomprofessfr() {
		return nomprofessfr;
	}

	public String getDatemodifie() {
		return datemodifie;
	}

	public String getIdcode() {
		return idcode;
	}

}
