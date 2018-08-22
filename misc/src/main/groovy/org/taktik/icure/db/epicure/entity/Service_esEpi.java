package org.taktik.icure.db.epicure.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Service_esEpi{ 

private Integer id_;
private String fichecontact; 
// private String datemodifie; 
private String id_service; 
private String id_contact; 
private String id_es_pat; 
private String fichepat; 
private String id_reference; 
private String id_branche; 
private String id_demarche; 

public void init(ResultSet resultset) throws SQLException { 
this.id_ = resultset.getRow();
this.fichecontact = resultset.getString ("fichecontact");
this.id_service = resultset.getString ("id_service");
this.id_contact = resultset.getString ("id_contact");
this.id_es_pat = resultset.getString ("id_es_pat");
this.fichepat = resultset.getString ("fichepat");
this.id_reference = resultset.getString ("id_reference");
this.id_branche = resultset.getString ("id_branche");
this.id_demarche = resultset.getString ("id_demarche");
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

public String getId_service() {
	return id_service;
}

public void setId_service(String id_service) {
	this.id_service = id_service;
}

public String getId_contact() {
	return id_contact;
}

public void setId_contact(String id_contact) {
	this.id_contact = id_contact;
}

public String getId_es_pat() {
	return id_es_pat;
}

public void setId_es_pat(String id_es_pat) {
	this.id_es_pat = id_es_pat;
}

public String getFichepat() {
	return fichepat;
}

public void setFichepat(String fichepat) {
	this.fichepat = fichepat;
}

public String getId_reference() {
	return id_reference;
}

public void setId_reference(String id_reference) {
	this.id_reference = id_reference;
}

public String getId_branche() {
	return id_branche;
}

public void setId_branche(String id_branche) {
	this.id_branche = id_branche;
}

public String getId_demarche() {
	return id_demarche;
}

public void setId_demarche(String id_demarche) {
	this.id_demarche = id_demarche;
}

}
