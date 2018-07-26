package org.taktik.icure.db

import org.taktik.icure.db.Importer
import org.taktik.icure.entities.*
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.Gender

class NsiTestImporter {
    static public void main(String... args) {

        def importer = new Importer()

        Collection<User> users
        Collection<HealthcareParty> parties
        Collection<Patient> patients
        Map<String, List<Invoice>> invoices
        Map<String, List<Contact>> contacts
        Map<String, List<HealthElement>> healthElements
        Map<String, List<Form>> forms
        Collection<Message> messages
        Map<String, Collection<String>> messageDocs
        Collection<Map> docs
        Collection<AccessLog> accessLogs

        accessLogs = new ArrayList<>()
        AccessLog accessLog = new AccessLog()
        accessLog.setId("accessLog Pgm NsiImporter");
        accessLog.setAccessType(AccessLog.USER_ACCESS)
        accessLog.setUser("KTH")
        accessLogs.add(accessLog)


        UUID uuidUser = UUID.randomUUID();
        UUID uuidHcp = UUID.randomUUID();
        UUID uuidPat = UUID.randomUUID();

        users = new ArrayList<>()
        User user;

        user = new User();
        user.setId(uuidUser.toString());
        user.setName("User Thielens");
        user.setHealthcarePartyId(uuidHcp.toString())
        user.setLogin("KTH")
        user.setPasswordHash("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08");
        users.add(user)

        parties = new ArrayList<>()

        HealthcareParty healthcareParty;

        healthcareParty = new HealthcareParty();
        healthcareParty.setId(uuidHcp.toString())
        healthcareParty.setName("DrThielens")
        healthcareParty.setFirstName("Karl")
        parties.add(healthcareParty)

        patients = new ArrayList<>()


        Patient patient = new Patient()
        patient.setId(uuidPat.toString());
        patient.setLastName("MrThielens");
        patient.setFirstName("Karl");
        patient.setGender(Gender.male);
        Set<Address> addresses = new HashSet<>();
        Address address;
        address = new Address();
        address.setStreet("Rue de Bruxelles");
        address.setCity("Awans");
        addresses.add(address);
        patient.setAddresses(addresses);
        patients.add(patient);

        invoices = new HashMap<>()
        contacts = new HashMap<>()
        healthElements = new HashMap<>()
        forms = new HashMap<>()
        messages = new ArrayList<>()
        messageDocs = new HashMap<>()
        docs = new ArrayList<>()

        importer.doImport(users, parties, patients, invoices, contacts, healthElements, forms, messages, messageDocs,
                docs, accessLogs)

        println " Process completed "
    }

}