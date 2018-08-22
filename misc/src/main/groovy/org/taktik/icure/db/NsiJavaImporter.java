package org.taktik.icure.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.taktik.icure.constants.Users;
import org.taktik.icure.db.epicure.dao.*;
import org.taktik.icure.db.epicure.entity.*;
import org.taktik.icure.entities.AccessLog;
import org.taktik.icure.entities.Classification;
import org.taktik.icure.entities.ClassificationTemplate;
import org.taktik.icure.entities.Contact;
import org.taktik.icure.entities.Form;
import org.taktik.icure.entities.HealthElement;
import org.taktik.icure.entities.HealthcareParty;
import org.taktik.icure.entities.Invoice;
import org.taktik.icure.entities.Message;
import org.taktik.icure.entities.Patient;
import org.taktik.icure.entities.User;
import org.taktik.icure.entities.base.Code;
import org.taktik.icure.entities.embed.*;

import java.text.SimpleDateFormat;
import java.util.*;

class NsiJavaImporter {

    private Collection<User> users;
    private Collection<HealthcareParty> parties;
    private Collection<Patient> patients;
    private Map<String, List<Invoice>> invoices;
    private Map<String, List<Contact>> contacts;
    private Map<String, List<HealthElement>> healthElements;
    private Map<String, List<Form>> forms;
    private Collection<Message> messages;
    private Map<String, Collection<String>> messageDocs;
    private Collection<Map> docs;
    private Collection<AccessLog> accessLogs;
    private Map<String, String> mapChildParent;
    private Map<String, List<Classification>> classifications;
    Collection<ClassificationTemplate> classificationTemplates;
    // Code et parametres
    private List<String> languagesFR = new ArrayList<String>();
    private List<String> lstEtatCivil;
    private List<String> lstLangue;
    private HashMap<String, String> professiHashMap;
    private HashMap<String, Contact_itemEpi> contact_itemHashMap;

    private HashMap<String, String> itemServiceSoap;

    private List<String> lstTypePatient;

    private int statusActif_relevant = 0;
    private int statusActif_irrelevant = 2;
    private int statusInactif = 1;
    private int statusArchive = 3;

    private HashMap<String, ClassificationTemplate> hashMapClassificationTemplate;

    NsiJavaImporter() {

        users = new ArrayList<>();
        parties = new ArrayList<>();
        patients = new ArrayList<>();
        invoices = new HashMap<>();
        contacts = new HashMap<>();
        healthElements = new HashMap<>();
        forms = new HashMap<>();
        messages = new ArrayList<>();
        messageDocs = new HashMap<>();
        docs = new ArrayList<>();
        accessLogs = new ArrayList<>();
        classifications = new HashMap<>();
        mapChildParent = new HashMap<>();
        hashMapClassificationTemplate = new HashMap<>();
        classificationTemplates = new ArrayList<>();

        //
        ObjectMapper objectMapper = new ObjectMapper();
        Object obj;
        //
        languagesFR.add("fr");
        lstTypePatient = Arrays.asList("Maison", "Appartement", "Studio", "Home privé", "Home cpas", "Logement social", "Hôpital", "Vivant en famille", "Vivant isolé", "Vivant en communauté", "Vivant en maison d'acceuil", "Autre");
        lstEtatCivil = Arrays.asList("", "Marié(e)", "Célibataire", "Divorcé(e)", "Séparé(e)", "Veuf(ve)", "Fiancé(e)", "Remarié(e)", "En instace de divorse", "Autre");
        ;
        lstLangue = Arrays.asList("", "F", "N", "A", "D");
        //
        professiHashMap = new HashMap<String, String>();
        Iterator<ProfessiEpi> itrProfessiEpi = new ProfessiDao().getProfessiList().iterator();
        while (itrProfessiEpi.hasNext()) {
            obj = itrProfessiEpi.next();
            ProfessiEpi professiEpi = objectMapper.convertValue(obj, ProfessiEpi.class);
            //ProfessiEpi professiEpi =  itrProfessiEpi.next();
            professiHashMap.put(professiEpi.getFichecontact(), professiEpi.getNomprofessfr());
        }
        //
        contact_itemHashMap = new HashMap<String, Contact_itemEpi>();
        Iterator<Contact_itemEpi> itrContact_itemEpi = new Contact_itemDao().getContact_itemList().iterator();
        while (itrContact_itemEpi.hasNext()) {
            obj = itrContact_itemEpi.next();
            Contact_itemEpi contact_itemEpi = objectMapper.convertValue(obj, Contact_itemEpi.class);
            contact_itemHashMap.put(contact_itemEpi.getFichecontact(), contact_itemEpi);
        }
        //
        itemServiceSoap = new HashMap<String, String>();
        itemServiceSoap.put("1000010100000000001", "O");
        itemServiceSoap.put("1000010100000000002", "O");
        itemServiceSoap.put("1000010100000000004", "O");
        itemServiceSoap.put("1000010100000000005", " ");
        itemServiceSoap.put("1000010100000000006", "P");
        itemServiceSoap.put("1000010100000000007", "P");
        itemServiceSoap.put("1000010100000000008", "P");
        itemServiceSoap.put("1000010100000000009", "P");
        itemServiceSoap.put("1000010100000000010", "P");
        itemServiceSoap.put("1000010100000000011", "P");
        itemServiceSoap.put("1000010100000000012", "P");
        itemServiceSoap.put("1000010100000000013", "O");
        itemServiceSoap.put("1000010100000000014", "P");
        itemServiceSoap.put("1000010100000000015", "P");
        itemServiceSoap.put("1000010100000000016", "P");
        itemServiceSoap.put("1000010100000000017", "P");
        itemServiceSoap.put("1000010100000000018", "P");
        itemServiceSoap.put("1000010100000000019", "P");
        itemServiceSoap.put("1000010100000000020", "P");
        itemServiceSoap.put("1000010100000000021", "P");
        itemServiceSoap.put("1000010100000000022", "P");
        itemServiceSoap.put("1000010100000000023", "S");
        itemServiceSoap.put("1000010100000000024", "O");
        itemServiceSoap.put("1000010100000000025", "M");
        itemServiceSoap.put("1000010100000000026", "A");
        itemServiceSoap.put("1000010100000000027", "A");
        itemServiceSoap.put("1000010100000000028", "A");
        itemServiceSoap.put("1000010100000000029", "A");
        itemServiceSoap.put("1000010100000000030", "A");
        itemServiceSoap.put("1000010100000000031", "A");
        itemServiceSoap.put("1000010100000000032", "P");
        itemServiceSoap.put("1000010100000000033", "P");
        itemServiceSoap.put("1000010100000000034", "A");
        itemServiceSoap.put("1000010100000000035", "P");
        itemServiceSoap.put("1000010100000000036", "M");
        itemServiceSoap.put("1000010100000000037", "O");
        itemServiceSoap.put("1000010100000000039", "O");
        itemServiceSoap.put("1000010100000000040", "O");
        itemServiceSoap.put("1000010100000000041", "O");
        itemServiceSoap.put("1000010100000000042", "P");
        itemServiceSoap.put("1000010100000000044", "O");
        itemServiceSoap.put("1000010100000000045", " ");
        itemServiceSoap.put("1000010100000000046", "P");
        itemServiceSoap.put("1000010100000000049", " ");
        itemServiceSoap.put("1000010100000000050", "O");
        itemServiceSoap.put("1000010100000000051", "P");
        itemServiceSoap.put("1000010100000000052", "P");
        itemServiceSoap.put("1000010100000000053", " ");
        itemServiceSoap.put("1000010100000000054", " ");
        itemServiceSoap.put("1000010100000000055", " ");
        itemServiceSoap.put("1000010100000000056", " ");
        itemServiceSoap.put("1000010100000000057", " ");
        itemServiceSoap.put("1000010100000000058", " ");
        itemServiceSoap.put("1000010100000000059", " ");
        itemServiceSoap.put("1000010100000000060", " ");
        itemServiceSoap.put("1000010100000000061", " ");
        itemServiceSoap.put("1000010100000000070", "P");
        itemServiceSoap.put("1000010100000000071", " ");
        itemServiceSoap.put("1000010100000000072", "P");
        itemServiceSoap.put("1000010100000000073", "P");
        itemServiceSoap.put("1000010100000000074", "P");
        itemServiceSoap.put("1000010100000000075", "P");
        itemServiceSoap.put("1000010100000000076", "O");
        itemServiceSoap.put("1000010100000000077", "O");
        itemServiceSoap.put("1000010100000000078", " ");
        itemServiceSoap.put("1000010100000000079", " ");
        itemServiceSoap.put("1000010100000000080", " ");
        itemServiceSoap.put("1000010100000000081", " ");
        itemServiceSoap.put("1000010100000000082", " ");
        itemServiceSoap.put("1000010100000000083", " ");
        itemServiceSoap.put("1000010100000000084", "O");
        itemServiceSoap.put("1000010100000000085", "O");
        itemServiceSoap.put("1000010100000000086", "O");
        itemServiceSoap.put("1000010100000000087", "O");
        itemServiceSoap.put("1000010100000000088", " ");
        itemServiceSoap.put("1000010100000000089", " ");
        itemServiceSoap.put("1000010100000000090", " ");
        itemServiceSoap.put("1000010100000000091", " ");
        itemServiceSoap.put("1000010100000000092", " ");
        itemServiceSoap.put("1000010100000000093", "M");
        itemServiceSoap.put("1000010100000000094", " ");
        itemServiceSoap.put("1000010100000000095", "O");
        itemServiceSoap.put("1000010100000000096", " ");
        itemServiceSoap.put("1000010100000000099", "O");
        itemServiceSoap.put("1000010100000000100", "P");
        itemServiceSoap.put("1000010100000000101", "P");
        itemServiceSoap.put("1000010100000000102", "P");
        itemServiceSoap.put("1000010100000000103", "P");
        itemServiceSoap.put("1000010100000000104", "P");
        itemServiceSoap.put("1000010100000000105", " ");
        itemServiceSoap.put("1000010100000000106", "P");
        itemServiceSoap.put("1000010100000000107", "O");
        itemServiceSoap.put("1000010100000000108", "P");
        itemServiceSoap.put("1000010100000000109", " ");
        itemServiceSoap.put("1000010100000000110", " ");
        itemServiceSoap.put("1000010100000000111", " ");
        itemServiceSoap.put("1000010100000000112", " ");
        itemServiceSoap.put("1000010100000000113", " ");
        itemServiceSoap.put("1000010100000000114", " ");
        itemServiceSoap.put("1000010100000000115", " ");
        itemServiceSoap.put("1000010100000000116", " ");
        itemServiceSoap.put("1000010100000000117", " ");
        itemServiceSoap.put("1000010100000000118", "P");
        itemServiceSoap.put("1000010100000000119", "O");
        itemServiceSoap.put("1000010100000000120", "O");
        itemServiceSoap.put("1000010100000000123", "N");


    }

    private Integer cnvTimeLongToInt(Long timestamp) {
        Integer retval = null;
        if (timestamp != null)
            retval = Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date(timestamp)));
        return retval;
    }

    private Long cnvTimeLongToString(Long timestamp) {
        Long retval = null;
        if (timestamp != null)
            retval = new Long(new SimpleDateFormat("yyyyMMddkkmmss").format(new Date(timestamp)).toString());

        return retval;
    }

    private String getListEntry(List<String> lstTypes, Integer index) {
        if (index == -1)
            return null;

        if (index < lstTypes.size())
            return lstTypes.get(index);
        else
            System.err.println("Error le code [" + index + "] non trouvé  !!!");
        return index.toString();
    }

    private String getListEntry(List<String> lstTypes, Long index) {
        return getListEntry(lstTypes, index.intValue());
    }

    private String completeStringWith(String sText, String sInfo, String sValeur) {

        if (sValeur == null || "".equals(sValeur))
            return sText;
        else if (sText == null)
            return sInfo + " : " + sValeur;
        else
            return sText + "\r\n" + sInfo + " : " + sValeur;
    }

    private String completeStringWith(String sText, String sInfo, Integer iValeur) {
        if (sInfo.indexOf("Cpas") > 0 && (iValeur == 0 || iValeur == -1 || iValeur == 65535))
            return sText;
        else
            return completeStringWith(sText, sInfo, iValeur.toString());
    }

    private void manuelAccessLog() {
        AccessLog accessLog;

        accessLog = new AccessLog();
        accessLog.setId("accessLog Pgm NsiImporter");
        accessLog.setAccessType(AccessLog.USER_ACCESS);
        accessLog.setUser("KTH");
        accessLogs.add(accessLog);
    }

    private void manuelUserAndHcp() {
        String idUser1 = "idUser_K";
        String idHcp1 = "idHcp_K"; // uuidHcp.toString();
        //
        User user;
        HealthcareParty healthcareParty;
        //
        user = new User();
        user.setId(idUser1);
        user.setName(idUser1);
        user.setHealthcarePartyId(idHcp1);
        user.setLogin("idLogin_K");
        user.setEmail("kth@nsi-sa.be");
        user.setPasswordHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
        user.setType(Users.Type.database);
        user.setStatus(Users.Status.ACTIVE);
        user.setRoles(new HashSet<String>(Arrays.asList("MS_ADMIN", "MS_PRACTICIAN")));// "MS_SECRETARY",
        users.add(user);
        //
        healthcareParty = new HealthcareParty();
        healthcareParty.setId(idHcp1);
        healthcareParty.setName(idHcp1);
        healthcareParty.setLastName("Thielens");
        healthcareParty.setFirstName("Karl");
        healthcareParty.setLanguages(languagesFR);
        parties.add(healthcareParty);
    }

    private void loadUserAndHcp() {
        User user;
        HealthcareParty healthcareParty;
        //
        User1Dao user1Dao = new User1Dao();
        List<User1Epi> lstUser1 = user1Dao.getUser1List();
        ObjectMapper objectMapper = new ObjectMapper();
        Iterator itrUser1Epi = lstUser1.iterator();
        while (itrUser1Epi.hasNext()) {
            Object obj = itrUser1Epi.next();
            User1Epi user1Epi = objectMapper.convertValue(obj, User1Epi.class);
            // User1Epi user1Epi = (User1Epi) itrUser1Epi.next();
            if (user1Epi.getFlag_actif() == 1) {
                user = new User();
                user.setId(user1Epi.getNom());
                user.setName("idUser_" + user1Epi.getId_().toString());
                user.setHealthcarePartyId("idHcp_" + user1Epi.getId_().toString());
                user.setLogin("idLogin_" + user1Epi.getId_().toString());
                user.setEmail(user1Epi.getEmail());
                user.setPasswordHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
                user.setType(Users.Type.database);
                if (user1Epi.getFlag_actif() == 1)
                    user.setStatus(Users.Status.ACTIVE);
                else
                    user.setStatus(Users.Status.DISABLED);
                user.setRoles(new HashSet<String>(Arrays.asList("MS_ADMIN", "MS_PRACTICIAN"))); //"MS_SECRETARY",
                users.add(user);

                healthcareParty = new HealthcareParty();
                healthcareParty.setId("idHcp_" + user1Epi.getId_().toString());
                healthcareParty.setName("idHcp_" + user1Epi.getId_().toString() + "_" + user1Epi.getNom());
                healthcareParty.setLastName(user1Epi.getNom());
                healthcareParty.setFirstName(user1Epi.getPrenom());
                healthcareParty.setLanguages(languagesFR);
                healthcareParty.setNihii(user1Epi.getInami());
                healthcareParty.setSsin(user1Epi.getNational());
                Set<Address> addresses = new HashSet<Address>();
                Address address = new Address();
                address.setAddressType(AddressType.work);
                address.setStreet(user1Epi.getAdresse());
                address.setCity(user1Epi.getVille());
                address.setPostalCode(user1Epi.getCode());
                addresses.add(address);
                healthcareParty.setAddresses(addresses);
                parties.add(healthcareParty);
            }
        }

    }

    private void loadClassificationTemplate() {
        String sUuidClassificationTemplate;
        String sUuidParentClassificationTemplate;

        ElementsoinsDao elementsoinsDao = new ElementsoinsDao();
        List<ElementsoinsEpi> lstElementsoinsEpi = elementsoinsDao.getElementsoinsList();
        ObjectMapper objectMapper = new ObjectMapper();
        Iterator itrElementsoinsEpi = lstElementsoinsEpi.iterator();
        while (itrElementsoinsEpi.hasNext()) {
            Object obj = itrElementsoinsEpi.next();
            ElementsoinsEpi elementsoinsEpi = objectMapper.convertValue(obj, ElementsoinsEpi.class);
            // ElementsoinsEpi elementsoinsEpi = (ElementsoinsEpi) itrElementsoinsEpi.next();
            sUuidClassificationTemplate = UUID.randomUUID().toString();
            // hashMapClassificationTemplate.put(elementsoinsEpi.getFichecontact(), sUuidClassificationTemplate);
            if (!elementsoinsEpi.getFichecontact().equals(elementsoinsEpi.getId_es_reference())) {
                sUuidParentClassificationTemplate = hashMapClassificationTemplate.get(elementsoinsEpi.getId_es_reference()).getId();
                mapChildParent.put(elementsoinsEpi.getFichecontact(), elementsoinsEpi.getId_es_reference());
            } else {
                sUuidParentClassificationTemplate = null;
                mapChildParent.put(elementsoinsEpi.getFichecontact(), null);
            }

            //println(elementsoinsEpi.getNiveau() + "\t" + elementsoinsEpi.getFichecontact() + "\t" + elementsoinsEpi.getReference_fr() + "\t" + sUuidClassificationTemplate + "\t" + elementsoinsEpi.getId_es_reference() + "\t" + sUuidParentClassificationTemplate + "\t" + elementsoinsEpi.getBranche_fr())
            ClassificationTemplate classificationTemplate = new ClassificationTemplate();
            classificationTemplate.setId(sUuidClassificationTemplate);
            if ("".equals(elementsoinsEpi.getBranche_fr()))
                classificationTemplate.setLabel(elementsoinsEpi.getReference_fr());
            else
                classificationTemplate.setLabel(elementsoinsEpi.getBranche_fr());
            if (!sUuidClassificationTemplate.equals(sUuidParentClassificationTemplate))
                classificationTemplate.setParentId(sUuidParentClassificationTemplate);
            // System.out.println(classificationTemplate.getLabel());
            classificationTemplates.add(classificationTemplate);
            hashMapClassificationTemplate.put(elementsoinsEpi.getFichecontact(), classificationTemplate);
        }
    }

    private void traiterClassification(String sIdLevel1, String
            sIdLevel2, List<Classification> lstClassification, HashMap<String, Classification> hashMapClassification) {
        String sUuidClassification;
        String sUuidParentClassification;
        String sIdLevelx;
        ClassificationTemplate classificationTemplateLevelx;
        ClassificationTemplate classificationTemplateLevel1 = hashMapClassificationTemplate.get(sIdLevel1);
        if (classificationTemplateLevel1 != null) {
            ClassificationTemplate classificationTemplateLevel2 = hashMapClassificationTemplate.get(sIdLevel2);
            if (classificationTemplateLevel2 != null) {
                // System.out.println("Level2 : " + classificationTemplateLevel2.getLabel() + " fils de : " + classificationTemplateLevel1.getLabel());
                classificationTemplateLevelx = classificationTemplateLevel2;
                sIdLevelx = sIdLevel2;
            } else {
                // System.out.println("Level1 : " + classificationTemplateLevel1.getLabel());
                classificationTemplateLevelx = classificationTemplateLevel1;
                sIdLevelx = sIdLevel1;
            }
            Classification classification = hashMapClassification.get(sIdLevelx);
            if (classification == null) {
                classification = new Classification();
                sUuidClassification = UUID.randomUUID().toString();
                classification.setId(sUuidClassification);
                classification.setLabel(classificationTemplateLevelx.getLabel());
                classification.setTemplateId(classificationTemplateLevelx.getId());
                String sCodeParentClassificationTemplate = mapChildParent.get(sIdLevelx);
                if (sCodeParentClassificationTemplate != null) {
                    Classification parentClassification = hashMapClassification.get(sCodeParentClassificationTemplate);
                    if (parentClassification == null) {
                        sUuidParentClassification = UUID.randomUUID().toString();
                        // Créer le pére
                        parentClassification = new Classification();
                        parentClassification.setId(sUuidParentClassification);
                        parentClassification.setLabel(hashMapClassificationTemplate.get(sCodeParentClassificationTemplate).getLabel());
                        parentClassification.setTemplateId(hashMapClassificationTemplate.get(sCodeParentClassificationTemplate).getId());
                        lstClassification.add(parentClassification);
                        hashMapClassification.put(sCodeParentClassificationTemplate, parentClassification);
                    } else {
                        sUuidParentClassification = parentClassification.getId();
                    }
                } else {
                    sUuidParentClassification = null;
                }

                classification.setParentId(sUuidParentClassification);
                //
                lstClassification.add(classification);
                hashMapClassification.put(sIdLevelx, classification);
            }
        } else {
            // System.out.println("NON Trouvé " + sIdLevel1);
        }
    }

    private List<Classification> loadElementsoin_patient(String sIdPatient, String sFichePat) {
        List<Classification> lstClassification = new ArrayList<>();
        HashMap<String, Classification> hashMapClassification = new HashMap<>();
        //

        List<Elementsoins_patientEpi> lstElementsoins_patientEpi = new Elementsoins_patientDao().getElementsoins_patientList(sFichePat);
        if (lstElementsoins_patientEpi.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            Iterator itrElementsoins_patientEpi = lstElementsoins_patientEpi.iterator();
            List<HealthElement> lstHealtElement = new ArrayList<HealthElement>();
            while (itrElementsoins_patientEpi.hasNext()) {
                Set<Code> tagsSet = new HashSet<Code>();
                Code code;
                String note = "";
                Object obj = itrElementsoins_patientEpi.next();
                Elementsoins_patientEpi elementsoins_patientEpi = objectMapper.convertValue(obj, Elementsoins_patientEpi.class);
                // Elementsoins_patientEpi elementsoins_patientEpi = (Elementsoins_patientEpi) itrElementsoins_patientEpi.next();
                HealthElement healthElement = new HealthElement();
                healthElement.setId("Es_"+elementsoins_patientEpi.getFichecontact());

                // System.out.println("fichecontact :" + elementsoins_patientEpi.getFichecontact() + " Diag_ant :" + elementsoins_patientEpi.getType_plainte_diag_ant().toString() + " visible :" + elementsoins_patientEpi.getType_visible()().toString() + " certitude :" + elementsoins_patientEpi.getCertitude().toString() + " gravite :" + elementsoins_patientEpi.getGravite().toString() + " temporalite :" + elementsoins_patientEpi.getTemporalite().toString());

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 0 ||
                        elementsoins_patientEpi.getType_plainte_diag_ant() == 1) {
                    traiterClassification(elementsoins_patientEpi.getId_reference(), elementsoins_patientEpi.getIdpat_lien_origine().substring(19), lstClassification, hashMapClassification);
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 2) {  // Plainte  )
                    if (elementsoins_patientEpi.getGravite() == 2) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusArchive);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusInactif);
                        }
                    }

                    if (elementsoins_patientEpi.getGravite() == 1) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusActif_irrelevant);
                            // healthElement.setRelevant(false);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusActif_relevant);
                            // healthElement.setRelevant(true);
                        }
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 3) {  // Diagnostic  )
                    if (elementsoins_patientEpi.getGravite() == 2) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusArchive);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusInactif);
                        }
                    }
                    if (elementsoins_patientEpi.getGravite() == 1) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusActif_irrelevant);
                            // healthElement.setRelevant(false);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                            healthElement.setStatus(statusActif_relevant);
                            // healthElement.setRelevant(true);
                        }
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 4) {  // Antécédent médical  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusInactif);
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 6) {  // Antécédent obstétrical  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusInactif);
                    }
                    note += "Antécédent obstétrical ";
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 7) {  // Antécédent traumatique  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusInactif);
                    }
                    note += "Antécédent thérapeutique ";
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 12) {  // Antécédent therapeutique  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusInactif);
                    }
                    note += "Antécédent thérapeutique ";
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 13) {  // Antécédent divers  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusInactif);
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 5) {  // Antécédent chirurgical  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|healthcareelement|1"));// TODO probleme chirurgical
                        healthElement.setStatus(statusInactif);
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 8) {  // Antécédent père  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusInactif);
                    }
                    note += "antécédent paternel ";
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 9) {  // Antécédent mère  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusInactif);
                    }
                    note += "antécédent materne l";
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 10) {  // Antécédent Grand Parent  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusInactif);
                    }
                    note += "antécédent grand parent ";
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 11) {  // Antécédent familial divers  )
                    if (elementsoins_patientEpi.getType_visible() == 0) {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusArchive);
                    } else {
                        tagsSet.add(new Code("CD-ITEM|familyrisk|1"));
                        healthElement.setStatus(statusInactif);
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 15) {  // Allergie  )
                    if (elementsoins_patientEpi.getGravite() == 2) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|allergy|1"));
                            healthElement.setStatus(statusArchive);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|allergy|1"));
                            healthElement.setStatus(statusInactif);
                        }
                    }

                    if (elementsoins_patientEpi.getGravite() == 1) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|allergy|1"));
                            healthElement.setStatus(statusActif_irrelevant);
                            // healthElement.setRelevant(false);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|allergy|1"));
                            healthElement.setStatus(statusActif_relevant);
                            // healthElement.setRelevant(true);
                        }
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 16) {  // Intolerance  )
                    if (elementsoins_patientEpi.getGravite() == 2) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|adr|1"));
                            healthElement.setStatus(statusArchive);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|adr|1"));
                            healthElement.setStatus(statusInactif);
                        }
                    }

                    if (elementsoins_patientEpi.getGravite() == 1) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            tagsSet.add(new Code("CD-ITEM|adr|1"));
                            healthElement.setStatus(statusActif_irrelevant);
                            // healthElement.setRelevant(false);
                        } else {
                            tagsSet.add(new Code("CD-ITEM|adr|1"));
                            healthElement.setStatus(statusActif_relevant);
                            // healthElement.setRelevant(true);
                        }
                    }
                }

                if (elementsoins_patientEpi.getType_plainte_diag_ant() == 17) {  // Facteur de risque   )
                    if (elementsoins_patientEpi.getGravite() == 2) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            if (elementsoins_patientEpi.getType_risque_social() == 0) {
                                tagsSet.add(new Code("CD-ITEM|risk|1"));
                                healthElement.setStatus(statusArchive);
                            } else {
                                tagsSet.add(new Code("CD-ITEM|socialrisk|1"));
                                healthElement.setStatus(statusArchive);
                            }
                        } else {
                            if (elementsoins_patientEpi.getType_visible() == 0) {
                                tagsSet.add(new Code("CD-ITEM|risk|1"));
                                healthElement.setStatus(statusInactif);
                            } else {
                                tagsSet.add(new Code("CD-ITEM|socialrisk|1"));
                                healthElement.setStatus(statusInactif);
                            }
                        }
                    }

                    if (elementsoins_patientEpi.getGravite() == 1) {
                        if (elementsoins_patientEpi.getType_visible() == 0) {
                            if (elementsoins_patientEpi.getType_risque_social() == 0) {
                                tagsSet.add(new Code("CD-ITEM|risk|1"));
                                healthElement.setStatus(statusActif_irrelevant);
                                // healthElement.setRelevant(false);
                            } else {
                                tagsSet.add(new Code("CD-ITEM|socialrisk|1"));
                                healthElement.setStatus(statusActif_irrelevant);
                                // healthElement.setRelevant(false);
                            }
                        } else {
                            if (elementsoins_patientEpi.getType_risque_social() == 0) {
                                tagsSet.add(new Code("CD-ITEM|risk|1"));
                                healthElement.setStatus(statusActif_relevant);
                                // healthElement.setRelevant(true);
                            } else {
                                tagsSet.add(new Code("CD-ITEM|socialrisk|1"));
                                healthElement.setStatus(statusActif_relevant);
                                // healthElement.setRelevant(true);
                            }
                        }
                    }
                }

                if (elementsoins_patientEpi.getType_risque_pulmonaire() == 1)
                    note += "FR pulmonaire ";
                if (elementsoins_patientEpi.getType_risque_cancer() == 1)
                    note += "FR cancer ";
                if (elementsoins_patientEpi.getType_risque_osteo() == 1)
                    note += "FR oestéoporose ";
                if (elementsoins_patientEpi.getType_risque_cardio() == 1)
                    note += "FR cardio-vasculaire ";
                if (elementsoins_patientEpi.getType_risque_prof() == 1)
                    note += "FR professionnel ";
                if (elementsoins_patientEpi.getType_risque_diabete() == 1)
                    note += "FR diabète ";

                if (elementsoins_patientEpi.getCertitude() == 2) {
                    tagsSet.add(new Code("CD-CERTAINTY|proven|1"));
                }
                if (elementsoins_patientEpi.getCertitude() == 3) {
                    tagsSet.add(new Code("CD-CERTAINTY|probable|1"));
                }
                if (elementsoins_patientEpi.getCertitude() == 4) {
                    tagsSet.add(new Code("CD-CERTAINTY|unprobable|1"));
                }
                if (elementsoins_patientEpi.getGravite() == 3) {
                    tagsSet.add(new Code("CD-CERTAINTY|excluded|1"));
                }

                if (elementsoins_patientEpi.getTemporalite() == 2) {
                    tagsSet.add(new Code("CD-TEMPORALITY|acute|1"));
                }
                if (elementsoins_patientEpi.getTemporalite() == 3) {
                    tagsSet.add(new Code("CD-TEMPORALITY|subacute|1"));
                }
                if (elementsoins_patientEpi.getTemporalite() == 4) {
                    tagsSet.add(new Code("CD-TEMPORALITY|chronic|1"));
                }

                // tagsSet.add(new Code("CD-SEVERITY|normal|1"))

                if (!"".equals(elementsoins_patientEpi.getBranche_fr())) {
                    note = elementsoins_patientEpi.getBranche_fr() + " " + note;
                } else {
                    if (!"".equals(elementsoins_patientEpi.getReference_fr())) {
                        note = elementsoins_patientEpi.getReference_fr() + " " + note;
                    } else {
                        note = "A retirer " + note;
                    }
                }

                healthElement.setTags(tagsSet);

                healthElement.setAuthor(elementsoins_patientEpi.getAuteur());
                healthElement.setClosingDate(elementsoins_patientEpi.getDatefin());

                Set<Code> codeSet = new HashSet<Code>();

                if (!"".equals(elementsoins_patientEpi.getCodeibui())) {
                    codeSet.add(new Code("BE-THESAURUS|" + elementsoins_patientEpi.getCodeibui() + "|3.1.0"));
                }
                if (!"".equals(elementsoins_patientEpi.getCodeicpc())) {
                    codeSet.add(new Code("ICPC|" + elementsoins_patientEpi.getCodeicpc() + "|2"));
                }
                if (!"".equals(elementsoins_patientEpi.getCodeicd())) {
                    codeSet.add(new Code("ICD|" + elementsoins_patientEpi.getCodeicd() + "|10"));
                }

                healthElement.setCodes(codeSet);
                healthElement.setOpeningDate(elementsoins_patientEpi.getDate_apparition());
                //TODO __/__2017
                // healthElement.setOpeningDate(elementsoins_patientEpi.getDate_demarche())

                healthElement.setDescr(note);
                String sPlanOfAction = elementsoins_patientEpi.getDemarche_fr();
                if ( !"".equals(sPlanOfAction)) {
                    List<PlanOfAction> lstPlanOfAction = new ArrayList<PlanOfAction>();
                    PlanOfAction planOfAction = new PlanOfAction();
                    planOfAction.setId(UUID.randomUUID().toString());
                    planOfAction.setDescr(sPlanOfAction);
                    lstPlanOfAction.add(planOfAction);
                    healthElement.setPlansOfAction(lstPlanOfAction);
                }
                // TOTO why healthElement.setHealthElementId(UUID.randomUUID().toString());
                healthElement.setHealthElementId("Es_Uk_"+elementsoins_patientEpi.getFichecontact());

                lstHealtElement.add(healthElement);
            }
            healthElements.put(sIdPatient, lstHealtElement);
            System.out.println("\tlstHealtElement:" + lstHealtElement.size());
            classifications.put(sIdPatient, lstClassification);
            System.out.println("\tlstClassification:" + lstClassification.size());
        }
        return lstClassification;
    }

    private List<Contact> loadContact(String sIdPatient, String sFichePat) {
        List<Contact> lstContact = new ArrayList<>();
        //
        Service_esDao service_esDao = new Service_esDao(); // Gestion d'un cache par Patient
        ServiceDao serviceDao = new ServiceDao(); // Gestion d'un cache par Patient
        ;                //
        List<ContactEpi> lstContactEpi = new ContactDao().getContactList(sFichePat);
        if (lstContactEpi.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            Iterator itrContactEpi = lstContactEpi.iterator();
            while (itrContactEpi.hasNext()) {
                Code code;
                String note = "";
                Object obj = itrContactEpi.next();
                ContactEpi contactEpi = objectMapper.convertValue(obj, ContactEpi.class);
                // Elementsoins_patientEpi elementsoins_patientEpi = (Elementsoins_patientEpi) itrElementsoins_patientEpi.next();
                Contact contact = new Contact();
                contact.setId("Contact_" + contactEpi.getFichecontact());
                contact.setOpeningDate(cnvTimeLongToString(contactEpi.getDateheure_valeur())); // TODO à vérifier
                contact.setAuthor(contactEpi.getAuteur());
                contact.setResponsible(contactEpi.getAuteur());
                contact.setDescr(contactEpi.getCommentaire());
                contact.setCreated(contactEpi.getDateheure_valeur());
                //
                Set<SubContact> tagsSubContact = new HashSet<SubContact>();
                SubContact subContact = new SubContact();
                subContact.setId("SubContact_" + contactEpi.getFichecontact());
                subContact.setAuthor(contactEpi.getAuteur());
                subContact.setDescr(contactEpi.getCommentaire());
                subContact.setFormId("FormId_" + contactEpi.getFichecontact());
                //
                Set<Service> tagsService = new HashSet<Service>();
                //
                List<Service_esEpi> lstService_esEpi = service_esDao.getService_esList(contactEpi.getFichepat(), contactEpi.getFichecontact());
                Iterator itrService_esEpi = lstService_esEpi.iterator();
                while (itrService_esEpi.hasNext()) {
                    Service_esEpi service_esEpi = (Service_esEpi) itrService_esEpi.next();
                    Service service = new Service();
                    service.setId("Service_" + service_esEpi.getId_service());
                    service.setIndex(service_esEpi.getId_().longValue());
                    service.setFormId("FormId_" + contactEpi.getFichecontact());
                    //
                    List<ServiceEpi> lstServiceEpi = serviceDao.getServiceList(contactEpi.getFichepat(), service_esEpi.getId_service());
                    Iterator itrServiceEpi = lstServiceEpi.iterator();
                    Set<Code> tagsSet = new HashSet<Code>();
                    while (itrServiceEpi.hasNext()) {
                        ServiceEpi serviceEpi = (ServiceEpi) itrServiceEpi.next();
                        Contact_itemEpi contact_itemEpi = contact_itemHashMap.get(serviceEpi.getId_classe_item());
                        String sLibelle_fr = "";
                        if (contact_itemEpi != null)
                            sLibelle_fr = contact_itemEpi.getLibelle_fr()+" : ";
                        service.setComment(sLibelle_fr + serviceEpi.getCommentaire());
                        String sSoapType = itemServiceSoap.get(serviceEpi.getId_classe_item());
                        if ("M".equals(sSoapType)) {
                            tagsSet.add(new Code("SOAP|xxx|1")); // TODO
                        }
                        if ("S".equals(sSoapType)) {
                            tagsSet.add(new Code("SOAP|Subjective|1"));
                        }
                        if ("O".equals(sSoapType)) {
                            tagsSet.add(new Code("SOAP|Objective|1"));
                        }
                        if ("A".equals(sSoapType)) {
                            tagsSet.add(new Code("SOAP|Assessment|1"));
                        }
                        if ("P".equals(sSoapType)) {
                            tagsSet.add(new Code("SOAP|Plan|1"));
                        }
                        if ("N".equals(sSoapType)) {
                            tagsSet.add(new Code("SOAP|xxx|1")); // TODO
                        }
                        service.setTags(tagsSet);
                        Set<String> tagsHealthElements = new HashSet<String>();
                        tagsHealthElements.add("Es_Uk_"+ service_esEpi.getId_es_pat());
                        service.setHealthElementsIds( tagsHealthElements);
                    }
                    //
                    tagsService.add(service);
                }
                tagsSubContact.add(subContact);
                //
                contact.setSubContacts(tagsSubContact);
                contact.setServices(tagsService);
                //
                lstContact.add(contact);
            }
            contacts.put(sIdPatient, lstContact);
            System.out.println("\tlstContact:" + lstContact.size());
        }
        return lstContact;
    }

    public void traiterPatient(PatientEpi patientEpi) {

        System.out.println("PATIENT : " + patientEpi.getFichepat() + "  " + patientEpi.getNom() + "  " + patientEpi.getPrenom() +
                "  " + (patientEpi.getNaissance().toString()));

        Patient patient = new Patient();
        patient.setId(patientEpi.getFichepat());
        patient.setLastName(patientEpi.getNom());
        patient.setFirstName(patientEpi.getPrenom());
        patient.setDateOfBirth(cnvTimeLongToInt(patientEpi.getNaissance()));
        switch (patientEpi.getSexe()) {
            case 1:
                patient.setGender(Gender.female);
                break;
            case 2:
                patient.setGender(Gender.male);
                break;
            default:
                patient.setGender(Gender.unknown);
                break;
        }

        patient.setNote(completeStringWith(patient.getNote(), "Type de patient", getListEntry(lstTypePatient, patientEpi.getTypepatient())));
        //TODO patientEpi.getReligion()
        patient.setNote(completeStringWith(patient.getNote(), "Etat civil", getListEntry(lstEtatCivil, patientEpi.getEtatcivil())));
        patient.setExternalId(patientEpi.getFichepat());
        Set<Address> addresses = new HashSet<Address>();
        Address address;
        if (patientEpi.getAdresse() != null) {
            address = new Address();
            address.setAddressType(AddressType.home);
            address.setStreet(patientEpi.getAdresse());
            address.setCity(patientEpi.getVille());
            address.setPostalCode(patientEpi.getCode());
            Set<Telecom> telecoms = new HashSet<Telecom>();
            Telecom telecom;
            if (!"".equals(patientEpi.getTelephone1())) {
                telecom = new Telecom();
                telecom.setTelecomType(TelecomType.phone);
                telecom.setTelecomNumber(patientEpi.getTelephone1());
                telecoms.add(telecom);
            }
            if (!"".equals(patientEpi.getTelephone2())) {
                telecom = new Telecom();
                telecom.setTelecomType(TelecomType.phone);
                telecom.setTelecomNumber(patientEpi.getTelephone2());
                telecoms.add(telecom);
            }
            if (!"".equals(patientEpi.getFax())) {
                telecom = new Telecom();
                telecom.setTelecomType(TelecomType.fax);
                telecom.setTelecomNumber(patientEpi.getFax());
                telecoms.add(telecom);
            }
            if (!"".equals(patientEpi.getFax2())) {
                telecom = new Telecom();
                telecom.setTelecomType(TelecomType.fax);
                telecom.setTelecomNumber(patientEpi.getFax2());
                telecoms.add(telecom);
            }
            if (!"".equals(patientEpi.getGsm())) {
                telecom = new Telecom();
                telecom.setTelecomType(TelecomType.mobile);
                telecom.setTelecomNumber(patientEpi.getGsm());
                telecoms.add(telecom);
            }
            if (!"".equals(patientEpi.getMail())) {
                telecom = new Telecom();
                telecom.setTelecomType(TelecomType.email);
                telecom.setTelecomNumber(patientEpi.getMail());
                telecoms.add(telecom);
            }
            address.setTelecoms(telecoms);
            addresses.add(address);
        }

        if (!"".equals(patientEpi.getAdresse2())) {
            address = new Address();
            address.setAddressType(AddressType.work);
            address.setStreet(patientEpi.getAdresse2());
            address.setCity(patientEpi.getVille2());
            address.setPostalCode(patientEpi.getCode2());
            addresses.add(address);
        }
        patient.setAddresses(addresses);

        List<Insurability> lstInsurability = new ArrayList<Insurability>();
        Insurability insurability = new Insurability();
        insurability.setInsuranceId(patientEpi.getMutuelle().toString());
        insurability.setIdentificationNumber(patientEpi.getNummutuelle());
        lstInsurability.add(insurability);
        patient.setInsurabilities(lstInsurability);

        // TODO patientEpi.getSang()

        List<String> languages = new ArrayList<String>();
        String language = getListEntry(lstLangue, patientEpi.getLangue());
        languages.add(language);
        patient.setLanguages(languages);

        // TODO patientEpi.getGestionpatient()

        patient.setDateOfDeath(cnvTimeLongToInt(patientEpi.getDatedeces()));
        // TODO patientEpi.getMotifdeces()
        patient.setNote(completeStringWith(patient.getNote(), "Prevenir", patientEpi.getPrevenir()));
        patient.setNote(completeStringWith(patient.getNote(), "Nom epoux", patientEpi.getNomepoux()));

        // TODO patientEpi.getTitre1() --> getTitre5()

        patient.setNote(completeStringWith(patient.getNote(), "getCpas_nation_naissance", patientEpi.getCpas_nation_naissance()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_nation_pere", patientEpi.getCpas_nation_pere()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_acces_soins", patientEpi.getCpas_acces_soins()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_statut_fami", patientEpi.getCpas_statut_fami()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_statut_etude", patientEpi.getCpas_statut_etude()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_statut_social", patientEpi.getCpas_statut_social()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_tabac", patientEpi.getCpas_tabac()));
        patient.setNote(completeStringWith(patient.getNote(), "getCpas_mutuelle", patientEpi.getCpas_mutuelle()));

        // TODO professions
        String sProfessi = professiHashMap.get(patientEpi.getIdprofession());
        if (sProfessi == null)
            sProfessi = "?" + patientEpi.getIdprofession();

        patient.setProfession(sProfessi);

        patient.setNote(completeStringWith(patient.getNote(), "Responsable dossier", patientEpi.getResponsable_dossier_libre()));
        patient.setSsin(patientEpi.getNational());

        // TODO  photo

        patients.add(patient);
        //
        List<Classification> lstClassification = loadElementsoin_patient(patient.getId(), patientEpi.getFichepat());
        //
        List<Contact> lstContact = loadContact(patient.getId(), patientEpi.getFichepat());
        //
    }

    private void loadPatient(String sNom, String sPrenom) {
        PatientDao patientDao = new PatientDao();
        List<PatientEpi> lstPatientEpi = patientDao.getPatientList(sNom, sPrenom);
        ObjectMapper objectMapper = new ObjectMapper();
        Iterator itrPatientEpi = lstPatientEpi.iterator();
        while (itrPatientEpi.hasNext()) {
            Object obj = itrPatientEpi.next();
            PatientEpi patientEpi = objectMapper.convertValue(obj, PatientEpi.class);
            // PatientEpi patientEpi = (PatientEpi) itrPatientEpi.next();
            traiterPatient(patientEpi);
        }
    }

    public void execute() {

        boolean bDoIt = true;

        StdImporter importer = new StdImporter();
        if (bDoIt) importer.doRemoveALL();
        {
            manuelAccessLog();
            manuelUserAndHcp();
            loadUserAndHcp();

            if (bDoIt) importer.doImportUsersAndHcp(users, parties);
        }
        {
            loadClassificationTemplate();
            if (bDoIt) importer.doImportClassificationTemplates(classificationTemplates);
        }
        {
            loadPatient("THIBEAUX", "%"); // "DAVID CHRISTIAN"  "LUCIE"
            if (bDoIt) importer.doImportPatient(patients, healthElements, classifications, contacts);
        }

        if (false) {
            patients = new ArrayList<>();
            healthElements = new HashMap<>();
            classifications = new HashMap<>();
            contacts = new HashMap<>();
            {
                loadPatient("VAN BEV%", "%");
                if (bDoIt) importer.doImportPatient(patients, healthElements, classifications, contacts);
            }
        }
        //importer.doImport(users, parties, patients, invoices, contacts, healthElements, forms, messages, messageDocs,
        //        docs, accessLogs, classificationTemplates, classifications);

    }

    static public void main(String[] args) {
        long start = System.currentTimeMillis();
        NsiJavaImporter nsiJavaImporter = new NsiJavaImporter();
        nsiJavaImporter.execute();
        System.out.println("\n Java reprise completed in " + (System.currentTimeMillis() - start) / 1000 + " s.");
    }

}