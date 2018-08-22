package org.taktik.icure.db.epicure.entity;

import org.taktik.icure.db.epicure.Tools;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Elementsoins_patientEpi {

    private Integer id_;
    private String fichecontact; // +++ a récupérer pour créer les ES : si codé
    // : c'est un ES, si non codé, c'est une
    // classification
    private String fichepat;
    private String reference_fr;
    private String branche_fr;
    private String demarche_fr;
    private String id_reference;
    private String id_branche;
    private String id_demarche;
    private Integer niveau;
    private String auteur;
    // private String datemodifie;
    private Long datefin;
    private Integer typecode; // +++ C'est grâce à cela si on sait si c'est un
    // ES ou une classe
    private String codeibui;
    private Long type_risque_social;
    private Long type_risque_pulmonaire;
    private Long type_risque_cancer;
    private Long type_risque_osteo;
    private Long type_risque_cardio;
    private Long type_risque_prof;
    private Long type_risque_autre;
    private Integer type_ps_es; // +++ sert à quoi ??
    private Integer gravite;
    private Long activite;
    private Integer temporalite;
    private Integer signifiance;
    private Integer certitude;
    private String commentaire;
    private String idpat_lien_origine;
    // private String lien_demarche_canevas;
    private Long date_apparition;
    private String codeicpc;
    private String codeicd;
    private Long type_visible;
    private Integer type_plainte_diag_ant;
    private Integer type_allergie;
    private Long date_demarche;
    private Long type_risque_diabete;
    private String id_service;
    // private String etiologie;
    // private String morphologie;
    private Integer lateralisation;
    // private String localisation;
    // private Long flag_prolonger;
    private Long falgexport;

    public void init(ResultSet resultset) throws SQLException {
        this.id_ = resultset.getRow();
        this.fichecontact = resultset.getString("fichecontact");
        this.fichepat = resultset.getString("fichepat");
        this.reference_fr = resultset.getString("reference_fr");
        this.branche_fr = resultset.getString("branche_fr");
        this.demarche_fr = resultset.getString("demarche_fr");
        this.id_reference = resultset.getString("id_reference");
        this.id_branche = resultset.getString("id_branche");
        this.id_demarche = resultset.getString("id_demarche");
        this.niveau = resultset.getInt("niveau");
        this.auteur = resultset.getString("auteur");
        this.datefin = Tools.cnvTime2Long(resultset, "datefin");
        this.typecode = resultset.getInt("typecode");
        this.codeibui = resultset.getString("codeibui");
        this.type_risque_social = resultset.getLong("type_risque_social");
        this.type_risque_pulmonaire = resultset.getLong("type_risque_pulmonaire");
        this.type_risque_cancer = resultset.getLong("type_risque_cancer");
        this.type_risque_osteo = resultset.getLong("type_risque_osteo");
        this.type_risque_cardio = resultset.getLong("type_risque_cardio");
        this.type_risque_prof = resultset.getLong("type_risque_prof");
        this.type_risque_autre = resultset.getLong("type_risque_autre");
        this.type_ps_es = resultset.getInt("type_ps_es");
        this.gravite = resultset.getInt("gravite");
        this.activite = resultset.getLong("activite");
        this.temporalite = resultset.getInt("temporalite");
        this.signifiance = resultset.getInt("signifiance");
        this.certitude = resultset.getInt("certitude");
        this.commentaire = resultset.getString("commentaire");
        this.idpat_lien_origine = resultset.getString("idpat_lien_origine");
        this.date_apparition = Tools.cnvTime2Long(resultset, "date_apparition");
        this.codeicpc = resultset.getString("codeicpc");
        this.codeicd = resultset.getString("codeicd");
        this.type_visible = resultset.getLong("type_visible");
        this.type_plainte_diag_ant = resultset.getInt("type_plainte_diag_ant");
        this.type_allergie = resultset.getInt("type_allergie");
        this.date_demarche = Tools.cnvTime2Long(resultset, "date_demarche");
        this.type_risque_diabete = resultset.getLong("type_risque_diabete");
        this.id_service = resultset.getString("id_service");
        this.lateralisation = resultset.getInt("lateralisation");
        this.falgexport = resultset.getLong("falgexport");
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

    public String getReference_fr() {
        return reference_fr;
    }

    public void setReference_fr(String reference_fr) {
        this.reference_fr = reference_fr;
    }

    public String getBranche_fr() {
        return branche_fr;
    }

    public void setBranche_fr(String branche_fr) {
        this.branche_fr = branche_fr;
    }

    public String getDemarche_fr() {
        return demarche_fr;
    }

    public void setDemarche_fr(String demarche_fr) {
        this.demarche_fr = demarche_fr;
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

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public Long getDatefin() {
        return datefin;
    }

    public void setDatefin(Long datefin) {
        this.datefin = datefin;
    }

    public Integer getTypecode() {
        return typecode;
    }

    public void setTypecode(Integer typecode) {
        this.typecode = typecode;
    }

    public String getCodeibui() {
        return codeibui;
    }

    public void setCodeibui(String codeibui) {
        this.codeibui = codeibui;
    }

    public Long getType_risque_social() {
        return type_risque_social;
    }

    public void setType_risque_social(Long type_risque_social) {
        this.type_risque_social = type_risque_social;
    }

    public Long getType_risque_pulmonaire() {
        return type_risque_pulmonaire;
    }

    public void setType_risque_pulmonaire(Long type_risque_pulmonaire) {
        this.type_risque_pulmonaire = type_risque_pulmonaire;
    }

    public Long getType_risque_cancer() {
        return type_risque_cancer;
    }

    public void setType_risque_cancer(Long type_risque_cancer) {
        this.type_risque_cancer = type_risque_cancer;
    }

    public Long getType_risque_osteo() {
        return type_risque_osteo;
    }

    public void setType_risque_osteo(Long type_risque_osteo) {
        this.type_risque_osteo = type_risque_osteo;
    }

    public Long getType_risque_cardio() {
        return type_risque_cardio;
    }

    public void setType_risque_cardio(Long type_risque_cardio) {
        this.type_risque_cardio = type_risque_cardio;
    }

    public Long getType_risque_prof() {
        return type_risque_prof;
    }

    public void setType_risque_prof(Long type_risque_prof) {
        this.type_risque_prof = type_risque_prof;
    }

    public Long getType_risque_autre() {
        return type_risque_autre;
    }

    public void setType_risque_autre(Long type_risque_autre) {
        this.type_risque_autre = type_risque_autre;
    }

    public Integer getType_ps_es() {
        return type_ps_es;
    }

    public void setType_ps_es(Integer type_ps_es) {
        this.type_ps_es = type_ps_es;
    }

    public Integer getGravite() {
        return gravite;
    }

    public void setGravite(Integer gravite) {
        this.gravite = gravite;
    }

    public Long getActivite() {
        return activite;
    }

    public void setActivite(Long activite) {
        this.activite = activite;
    }

    public Integer getTemporalite() {
        return temporalite;
    }

    public void setTemporalite(Integer temporalite) {
        this.temporalite = temporalite;
    }

    public Integer getSignifiance() {
        return signifiance;
    }

    public void setSignifiance(Integer signifiance) {
        this.signifiance = signifiance;
    }

    public Integer getCertitude() {
        return certitude;
    }

    public void setCertitude(Integer certitude) {
        this.certitude = certitude;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getIdpat_lien_origine() {
        return idpat_lien_origine;
    }

    public void setIdpat_lien_origine(String idpat_lien_origine) {
        this.idpat_lien_origine = idpat_lien_origine;
    }

    public Long getDate_apparition() {
        return date_apparition;
    }

    public void setDate_apparition(Long date_apparition) {
        this.date_apparition = date_apparition;
    }

    public String getCodeicpc() {
        return codeicpc;
    }

    public void setCodeicpc(String codeicpc) {
        this.codeicpc = codeicpc;
    }

    public String getCodeicd() {
        return codeicd;
    }

    public void setCodeicd(String codeicd) {
        this.codeicd = codeicd;
    }

    public Long getType_visible() {
        return type_visible;
    }

    public void setType_visible(Long type_visible) {
        this.type_visible = type_visible;
    }

    public Integer getType_plainte_diag_ant() {
        return type_plainte_diag_ant;
    }

    public void setType_plainte_diag_ant(Integer type_plainte_diag_ant) {
        this.type_plainte_diag_ant = type_plainte_diag_ant;
    }

    public Integer getType_allergie() {
        return type_allergie;
    }

    public void setType_allergie(Integer type_allergie) {
        this.type_allergie = type_allergie;
    }

    public Long getDate_demarche() {
        return date_demarche;
    }

    public void setDate_demarche(Long date_demarche) {
        this.date_demarche = date_demarche;
    }

    public Long getType_risque_diabete() {
        return type_risque_diabete;
    }

    public void setType_risque_diabete(Long type_risque_diabete) {
        this.type_risque_diabete = type_risque_diabete;
    }

    public String getId_service() {
        return id_service;
    }

    public void setId_service(String id_service) {
        this.id_service = id_service;
    }

    public Integer getLateralisation() {
        return lateralisation;
    }

    public void setLateralisation(Integer lateralisation) {
        this.lateralisation = lateralisation;
    }

    public Long getFalgexport() {
        return falgexport;
    }

    public void setFalgexport(Long falgexport) {
        this.falgexport = falgexport;
    }

}
