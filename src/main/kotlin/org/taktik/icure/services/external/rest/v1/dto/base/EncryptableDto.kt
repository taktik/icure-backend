package org.taktik.icure.services.external.rest.v1.dto.base

import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto

interface EncryptableDto {
    //Those are typically filled in the contacts
    //Used when we want to find all contacts for a specific patient
    //These keys are in clear. You can have several to partition the medical document space
    val secretForeignKeys: Set<String>

    //Used when we want to find the patient for this contact
    //These keys are the public patient ids encrypted using the hcParty keys.
    val cryptedForeignKeys: Map<String, Set<DelegationDto>>

    //When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    //The responsible is thus always in the delegations as well
    val delegations: Map<String, Set<DelegationDto>>

    //When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db)
    //He/she encrypts it using his own AES exchange key and stores it as a delegation
    val encryptionKeys: Map<String, Set<DelegationDto>>

    val encryptedSelf: String?
}
