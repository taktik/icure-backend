package org.taktik.icure.db

import com.fasterxml.jackson.databind.ObjectMapper
import org.ektorp.CouchDbInstance
import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.taktik.icure.db.json.JsonImport
import org.taktik.icure.entities.Patient

class JsonImporter extends Importer {
    static void main(String... args) {
        new JsonImporter().scan(args)
    }

    void scan(String... args) {
        ObjectMapper mapper = new ObjectMapper()

        JsonImport jsonImport = mapper.readValue(new File(args[-1]), JsonImport.class)
        jsonImport.patients.each {
            if (!it.id) {
                it.id = idg.newGUID()
            }
            if (!it.created) {
                it.created = System.currentTimeMillis()
            }
            if (!it.modified) {
                it.created = System.currentTimeMillis()
            }
            if (!it.author) {
                it.author = "ec118556-44c2-4cd7-9413-694cf4d7a8ad"
            }
            if (!it.responsible) {
                it.responsible = "e287efa2-ed33-40ae-944f-23d383941cda"
            }
            if (!it.delegations) {
                it.delegations = ["c278c6ab-9dd3-4671-9cc5-805b989f3944": new HashSet<>(),
                                  "ccf7c148-df3f-4433-99b8-fc475e5025d6": new HashSet<>(),
                                  "c34939ba-1a5f-4c94-9642-8ec2581c2972": new HashSet<>(),
                                  "1a9fee7f-90b3-48a2-9722-2b73a0cd8c2c": new HashSet<>(),
                                  "4bd28f8d-112e-4533-9c64-8e800e6c3d64": new HashSet<>(),
                                  "759e62ff-5162-41f3-9b8d-00206bad1801": new HashSet<>(),
                                  "ff993d68-c533-4767-90e8-43d3cdeebd1a": new HashSet<>(),
                                  "716d4888-248b-4a28-9c2d-4ead3d5bb3b9": new HashSet<>(),
                                  "a5bee4c9-8d00-418d-99d7-a8b48e0b89c2": new HashSet<>(),
                                  "403d8398-35ad-474d-92d4-2263125e75c7": new HashSet<>(),
                                  "4cd5988e-e9de-439d-910b-f16ea752d2f0": new HashSet<>(),
                                  "63fe4cd8-0051-4be2-9f31-e04ac7f91756": new HashSet<>(),
                                  "8fee67da-cb82-431a-91a0-cfafb173b60f": new HashSet<>(),
                                  "74b626a6-953e-4435-9e89-24399fede194": new HashSet<>(),
                                  "bdf56ed0-b59c-4bc1-9a89-0526a0ddb63a": new HashSet<>(),
                                  "7980d0f6-bbe0-466b-98d0-9daf484f787f": new HashSet<>(),
                                  "0d2a7c4b-f77a-46b3-9ea7-e235eba690d1": new HashSet<>(),
                                  "f262d0d8-8d21-410a-984e-cf175aaf98af": new HashSet<>(),
                                  "138c3da2-1163-4388-9765-f6a4018fbb7e": new HashSet<>(),
                                  "825ab73f-b87a-476b-9e69-24b5ab946f64": new HashSet<>(),
                                  "c39b45fe-029f-43e2-96b2-94d84e2c0727": new HashSet<>(),
                                  "3b18765a-9184-47c9-94cb-9785ca2ce278": new HashSet<>(),
                                  "9fef9111-01a6-48a9-9cef-4f9161987bbc": new HashSet<>(),
                                  "8e63dc3a-ed69-4ae5-9570-abc4a035e639": new HashSet<>(),
                                  "e287efa2-ed33-40ae-944f-23d383941cda": new HashSet<>(),
                                  "fe2a0bca-2e91-4de1-9f8b-1abd4a64052b": new HashSet<>(),
                                  "f4779d58-4fd7-402c-958d-fa5f97b4ca94": new HashSet<>(),
                                  "8c81fd43-5d66-410b-966a-802cf93e8f58": new HashSet<>(),
                                  "f699a6b3-0d0b-4a5a-97c8-5a45a06e45d7": new HashSet<>()
                ]
            }
        }
        //doImport([],[], jsonImport.patients, [:], [:], [:], [:], [], [:], [], [])

        HttpClient httpClient = new StdHttpClient.Builder().socketTimeout(120000).connectionTimeout(120000).url("https://couch.icure.cloud:443").username("cmw-bd91ef17-8f3f-48bc-b97c-bb2840c4c3b2").password("9e719aac-5e10-4e8f-968a-72b51fcba194").build()
        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient)

        couchdbPatient = dbInstance.createConnector('icure-cmw-bd91ef17-8f3f-48bc-b97c-bb2840c4c3b2-patient', false)
        jsonImport.patients.collate(50).each {
            couchdbPatient.executeBulk(it)
        }
    }
}