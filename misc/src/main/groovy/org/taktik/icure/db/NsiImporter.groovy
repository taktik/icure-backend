package org.taktik.icure.db

import org.ektorp.CouchDbConnector

import org.taktik.icure.constants.Users

import org.taktik.icure.db.epicure.dao.ContactDao

import org.taktik.icure.db.epicure.dao.ElementsoinsDao
import org.taktik.icure.db.epicure.dao.PatientDao
import org.taktik.icure.db.epicure.dao.ProfessiDao
import org.taktik.icure.db.epicure.entity.ContactEpi
import org.taktik.icure.db.epicure.entity.ElementsoinsEpi
import org.taktik.icure.db.epicure.entity.PatientEpi
import org.taktik.icure.db.epicure.entity.ProfessiEpi
import org.taktik.icure.entities.AccessLog
import org.taktik.icure.entities.ClassificationTemplate
import org.taktik.icure.entities.Contact
import org.taktik.icure.entities.Form
import org.taktik.icure.entities.HealthElement
import org.taktik.icure.entities.HealthcareParty
import org.taktik.icure.entities.Invoice
import org.taktik.icure.entities.Message
import org.taktik.icure.entities.Patient
import org.taktik.icure.entities.User
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.AddressType
import org.taktik.icure.entities.embed.Gender
import org.taktik.icure.entities.embed.Insurability
import org.taktik.icure.entities.embed.Telecom
import org.taktik.icure.entities.embed.TelecomType

import java.text.SimpleDateFormat

class NsiImporter {

    protected String DB_PROTOCOL = System.getProperty("dbprotocol") ?: "http"
    protected String DB_HOST = System.getProperty("dbhost") ?: "127.0.0.1"
    protected String DB_PORT = System.getProperty("dbport") ?: 5984
    protected String DEFAULT_KEY_DIR = "/Users/aduchate/Library/icure-cloud/keys"
    protected String DB_NAME = System.getProperty("dbname") ?: "icure"
    protected CouchDbConnector couchdbBase

    private Collection<User> users
    private Collection<HealthcareParty> parties
    private Collection<Patient> patients
    private Map<String, List<Invoice>> invoices
    private Map<String, List<Contact>> contacts
    private Map<String, List<HealthElement>> healthElements
    private Map<String, List<Form>> forms
    private Collection<Message> messages
    private Map<String, Collection<String>> messageDocs
    private Collection<Map> docs
    private Collection<AccessLog> accessLogs

    NsiImporter() {

        users = new ArrayList<>()
        parties = new ArrayList<>()
        patients = new ArrayList<>()
        invoices = new HashMap<>()
        contacts = new HashMap<>()
        healthElements = new HashMap<>()
        forms = new HashMap<>()
        messages = new ArrayList<>()
        messageDocs = new HashMap<>()
        docs = new ArrayList<>()
        accessLogs = new ArrayList<>()
    }

    /*
    private Integer cnvTime2String(Timestamp timestamp) {
        Integer retval;
        if (timestamp != null)
            retval = Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date(timestamp.getTime())))
        return retval
    }
    */

    private Integer cnvTime2String(Long timestamp) {
        Integer retval;
        if (timestamp != null)
            retval = Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date(timestamp)))
        return retval
    }


    private String getListEntry(List<String> lstTypes, Integer index) {
        if (index == -1)
            return null

        if (index < lstTypes.size())
            return lstTypes.get(index)
        else
            System.err.println("Error le code [" + index + "] non trouvé  !!!")
        return index.toString()
    }

    private String getListEntry(List<String> lstTypes, Long index) {
        return getListEntry(lstTypes, index.intValue())
    }

    private String completeStringWith(String sText, String sInfo, String sValeur) {

        if (sValeur == null || "".equals(sValeur))
            return sText
        else if (sText == null)
            return sInfo + " : " + sValeur
        else
            return sText + "\r\n" + sInfo + " : " + sValeur
    }

    private String completeStringWith(String sText, String sInfo, Integer sValeur) {
        return completeStringWith(sText, sInfo, sValeur.toString())
    }

    //private void execute() {
    def execute() {

        // Chargement des codes
        List<String> lstTypePatient = Arrays.asList("Maison", "Appartement", "Studio", "Home privé", "Home cpas", "Logement social", "Hôpital", "Vivant en famille", "Vivant isolé", "Vivant en communauté", "Vivant en maison d'acceuil", "Autre")
        // TODO à compléter
        List<String> lstEtatCivil = Arrays.asList("", "Marié(e)")
        List<String> lstLangue = Arrays.asList("F", "N", "A", "D")

        HashMap<String, String> professiHashMap = new HashMap<String, String>()
        Iterator itrProfessiEpi = new ProfessiDao().getProfessiList().iterator()
        while (itrProfessiEpi.hasNext()) {
            ProfessiEpi professiEpi = itrProfessiEpi.next()
            professiHashMap.put(professiEpi.getFichecontact(), professiEpi.getNomprofessfr())
        }

        HashMap<String, String> hashMapClassificationTemplate = new HashMap<>()
        String sUuidClassificationTemplate;
        String sUuidParentClassificationTemplate;

        Collection<ClassificationTemplate> classificationTemplates = new ArrayList<>();

        ElementsoinsDao elementsoinsDao = new ElementsoinsDao();
        List<ElementsoinsEpi> lstElementsoinsEpi = elementsoinsDao.getElementsoinsList();

        Iterator itrElementsoinsEpi = lstElementsoinsEpi.iterator()
        while (itrElementsoinsEpi.hasNext()) {
            ElementsoinsEpi elementsoinsEpi = itrElementsoinsEpi.next()
            sUuidClassificationTemplate = UUID.randomUUID().toString()
            hashMapClassificationTemplate.put(elementsoinsEpi.getFichecontact(), sUuidClassificationTemplate);
            if (!"".equals(elementsoinsEpi.getId_es_reference()))
                sUuidParentClassificationTemplate = hashMapClassificationTemplate.get(elementsoinsEpi.getId_es_reference())
            else
                sUuidClassificationTemplate = null
            //println(elementsoinsEpi.getNiveau() + "\t" + elementsoinsEpi.getFichecontact() + "\t" + elementsoinsEpi.getReference_fr() + "\t" + sUuidClassificationTemplate + "\t" + elementsoinsEpi.getId_es_reference() + "\t" + sUuidParentClassificationTemplate + "\t" + elementsoinsEpi.getBranche_fr())
            ClassificationTemplate classificationTemplate = new ClassificationTemplate();
            classificationTemplate.setId(sUuidClassificationTemplate);
            if ("".equals(elementsoinsEpi.branche_fr))
                classificationTemplate.setLabel(elementsoinsEpi.getReference_fr());
            else
                classificationTemplate.setLabel(elementsoinsEpi.getBranche_fr());
            if (!sUuidClassificationTemplate.equals(sUuidParentClassificationTemplate))
                classificationTemplate.setParentId(sUuidParentClassificationTemplate);
            // println(classificationTemplate.getLabel());
            classificationTemplates.add(classificationTemplate);
        }
        //couchdbBase.executeBulk(classificationTemplates);

        // Traitement AccessLog
        AccessLog accessLog

        accessLog = new AccessLog()
        accessLog.setId("accessLog Pgm NsiImporter");
        accessLog.setAccessType(AccessLog.USER_ACCESS)
        accessLog.setUser("KTH")
        accessLogs.add(accessLog)

        // Traitement User
        UUID uuidUser = UUID.randomUUID();
        UUID uuidHcp = UUID.randomUUID();
        UUID uuidPat = UUID.randomUUID();

        String idUser1 = "idUserDrK"; // uuidUser.toString();
        String idHcp1 = "idHcpDrK"; // uuidHcp.toString();

        String idUser2 = "idUserDrJ"; // uuidUser.toString();
        String idHcp2 = "idHcpDrJ"; // uuidHcp.toString();


        User user;
        //
        user = new User();
        user.setId(idUser1);
        user.setName("User Thielens")
        user.setHealthcarePartyId(idHcp1)
        user.setLogin("KTH")
        user.setPasswordHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
        user.setType(Users.Type.database)
        user.setStatus(Users.Status.ACTIVE)
        user.setRoles(new HashSet<String>(Arrays.asList("MS_ADMIN", "MS_SECRETARY", "MS_PRACTICIAN")))
        users.add(user)
        //
        user = new User();
        user.setId(idUser2)
        user.setName("User Filardi");
        user.setHealthcarePartyId(idHcp2)
        user.setLogin("JFI")
        user.setPasswordHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08")
        user.setType(Users.Type.database)
        user.setStatus(Users.Status.ACTIVE)
        user.setRoles(new HashSet<String>(Arrays.asList("MS_ADMIN", "MS_PRACTICIAN"))) //"MS_SECRETARY",
        users.add(user)

        // Traitement HealthcareParty
        HealthcareParty healthcareParty;

        healthcareParty = new HealthcareParty();
        healthcareParty.setId(idHcp1);
        healthcareParty.setName("DrThielens")
        healthcareParty.setFirstName("Karl")
        parties.add(healthcareParty)
        //
        healthcareParty = new HealthcareParty();
        healthcareParty.setId(idHcp2);
        healthcareParty.setName("DrFilardi")
        healthcareParty.setFirstName("Julien")
        parties.add(healthcareParty)

        // Lecture des Patients
        PatientDao patientDao = new PatientDao()
        List<PatientEpi> lstPatientEpi = patientDao.getPatientList("THIBEAUX%", "%")
        Iterator itrPatientEpi = lstPatientEpi.iterator()
        while (itrPatientEpi.hasNext()) {
            PatientEpi patientEpi = itrPatientEpi.next()
            Patient patient = new Patient()
            patient.setId(patientEpi.getFichepat())
            patient.setLastName(patientEpi.getNom())
            patient.setFirstName(patientEpi.getPrenom())
            patient.setDateOfBirth(cnvTime2String(patientEpi.getNaissance()));
            switch (patientEpi.getSexe()) {
                case 1:
                    patient.setGender(Gender.female);
                    break
                case 2:
                    patient.setGender(Gender.male);
                    break;
                default:
                    patient.setGender(Gender.unknown);
                    break;
            };

            patient.setNote(completeStringWith(patient.getNote(), "Type de patient", getListEntry(lstTypePatient, patientEpi.getTypepatient())))
            //TODO patientEpi.getReligion()
            patient.setNote(completeStringWith(patient.getNote(), "Etat civil", getListEntry(lstEtatCivil, patientEpi.getEtatcivil())))

            Set<Address> addresses = new HashSet<Address>();
            Address address;
            if (patientEpi.getAdresse() != null) {
                address = new Address()
                address.setAddressType(AddressType.home)
                address.setStreet(patientEpi.getAdresse())
                address.setCity(patientEpi.getVille())
                address.setPostalCode(patientEpi.getCode())
                Set<Telecom> telecoms = new HashSet<Telecom>()
                Telecom telecom;
                if (!"".equals(patientEpi.getTelephone1())) {
                    telecom = new Telecom()
                    telecom.setTelecomType(TelecomType.phone)
                    telecom.setTelecomNumber(patientEpi.getTelephone1())
                    telecoms.add(telecom)
                }
                if (!"".equals(patientEpi.getTelephone2())) {
                    telecom = new Telecom()
                    telecom.setTelecomType(TelecomType.phone)
                    telecom.setTelecomNumber(patientEpi.getTelephone2())
                    telecoms.add(telecom)
                }
                if (!"".equals(patientEpi.getFax())) {
                    telecom = new Telecom()
                    telecom.setTelecomType(TelecomType.fax)
                    telecom.setTelecomNumber(patientEpi.getFax())
                    telecoms.add(telecom)
                }
                if (!"".equals(patientEpi.getFax2())) {
                    telecom = new Telecom()
                    telecom.setTelecomType(TelecomType.fax)
                    telecom.setTelecomNumber(patientEpi.getFax2())
                    telecoms.add(telecom)
                }
                if (!"".equals(patientEpi.getGsm())) {
                    telecom = new Telecom()
                    telecom.setTelecomType(TelecomType.mobile)
                    telecom.setTelecomNumber(patientEpi.getGsm())
                    telecoms.add(telecom)
                }
                if (!"".equals(patientEpi.getMail())) {
                    telecom = new Telecom()
                    telecom.setTelecomType(TelecomType.email)
                    telecom.setTelecomNumber(patientEpi.getMail())
                    telecoms.add(telecom)
                }
                address.setTelecoms(telecoms)
                addresses.add(address)
            }

            if (patientEpi.getAdresse2() != null) {
                address = new Address();
                address.setAddressType(AddressType.work)
                address.setStreet(patientEpi.getAdresse2());
                address.setCity(patientEpi.getVille2());
                address.setPostalCode(patientEpi.getCode2())
                addresses.add(address);
                patient.setAddresses(addresses)
            }

            List<Insurability> lstInsurability = new ArrayList<Insurability>()
            Insurability insurability = new Insurability()
            insurability.setInsuranceId(patientEpi.getMutuelle().toString())
            insurability.setIdentificationNumber(patientEpi.getNummutuelle())
            lstInsurability.add(insurability)
            patient.setInsurabilities(lstInsurability)

            // TODO patientEpi.getSang()

            List<String> languages = new ArrayList<String>()
            String language = getListEntry(lstLangue, patientEpi.getLangue())
            languages.add(language)
            patient.setLanguages(languages)

            // TODO patientEpi.getGestionpatient()

            patient.setDateOfDeath(cnvTime2String(patientEpi.getDatedeces()))
            // TODO patientEpi.getMotifdeces()
            patient.setNote(completeStringWith(patient.getNote(), "Prevenir", patientEpi.getPrevenir()))
            patient.setNote(completeStringWith(patient.getNote(), "Nom epoux", patientEpi.getNomepoux()))

            // TODO patientEpi.getTitre1() --> getTitre5()

            patient.setNote(completeStringWith(patient.getNote(), "getCpas_nation_naissance", patientEpi.getCpas_nation_naissance()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_nation_pere", patientEpi.getCpas_nation_pere()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_acces_soins", patientEpi.getCpas_acces_soins()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_statut_fami", patientEpi.getCpas_statut_fami()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_statut_etude", patientEpi.getCpas_statut_etude()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_statut_social", patientEpi.getCpas_statut_social()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_mutuelle", patientEpi.getCpas_mutuelle()))
            patient.setNote(completeStringWith(patient.getNote(), "getCpas_mutuelle", patientEpi.getCpas_mutuelle()))

            patient.setProfession(professiHashMap.get(patientEpi.getIdprofession(), "?" + patientEpi.getIdprofession()))

            patient.setNote(completeStringWith(patient.getNote(), "Responsable dossier", patientEpi.getResponsable_dossier_libre()))
            patient.setSsin(patientEpi.getNational())

            // TODO  photo

            patients.add(patient);
            //
            List<Contact> lstContactEpicure = new ContactDao().getContactList(patientEpi.getFichepat());
            if (lstContactEpicure.size() > 0) {
                Iterator itrContact = lstContactEpicure.iterator();
                List<HealthElement> lstHealtElement = new ArrayList<Contact>();
                while (itrContact.hasNext()) {
                    ContactEpi contactEpi = itrContact.next();
                    HealthElement healthElement = new HealthElement();
                    healthElement.setId(contactEpi.getFichecontact())
                    healthElement.setAuthor(contactEpi.getAuteur())
                    healthElement.setDescr(contactEpi.getCommentaire())
                    healthElement.setOpeningDate(cnvTime2String(contactEpi.getDateheure_enrg()))
                    lstHealtElement.add(healthElement)
                }
                healthElements.put(patient.getId(), lstHealtElement)
            }
            System.out.println(patientEpi.getFichepat() + "  " + patientEpi.getNom() + "  " + patientEpi.getPrenom() + "  " + cnvTime2String(patientEpi.getNaissance()) + "   lstContact.size=" + lstContactEpicure.size())
            //
        }


        def importer = new Importer()
        importer.doImport(users, parties, patients, invoices, contacts, healthElements, forms, messages, messageDocs,
                docs, accessLogs, classificationTemplates)

    }

    static public void main(String... args) {
        def start = System.currentTimeMillis()
        NsiImporter nsiImporter = new NsiImporter()
        nsiImporter.execute()
        println("\n completed in " + (System.currentTimeMillis() - start) / 1000 + " s.")
    }

}