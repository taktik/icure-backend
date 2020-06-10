package org.taktik.icure.services.external.rest.v1.mapper.filter

import org.mapstruct.Mapper
import org.taktik.icure.dto.filter.Filter
import org.taktik.icure.dto.filter.predicate.AndPredicate
import org.taktik.icure.dto.filter.predicate.KeyValuePredicate
import org.taktik.icure.dto.filter.predicate.NotPredicate
import org.taktik.icure.dto.filter.predicate.OrPredicate
import org.taktik.icure.dto.filter.predicate.Predicate
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain
import org.taktik.icure.services.external.rest.v1.dto.filter.code.CodeByRegionTypeLabelLanguageFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.contact.ContactByServiceIdsFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndActiveFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndExternalIdFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndSsinFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyAndSsinsFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyDateOfBirthFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyGenderEducationProfession
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByHcPartyNameFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.patient.PatientByIdsFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByContactsAndSubcontactsFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.rest.v1.dto.filter.service.ServiceBySecretForeignKeys

interface FilterMapper {
    fun <O : Identifiable<String>> map(filterChainDto: FilterChain): org.taktik.icure.dto.filter.chain.FilterChain<O>
    fun <O : Identifiable<String>> map(accessLog: org.taktik.icure.dto.filter.chain.FilterChain<O>): FilterChain

    fun map(filterDto: CodeByRegionTypeLabelLanguageFilter): org.taktik.icure.dto.filter.impl.code.CodeByRegionTypeLabelLanguageFilter
    fun map(filterDto: ContactByHcPartyPatientTagCodeDateFilter): org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter
    fun map(filterDto: ContactByHcPartyTagCodeDateFilter): org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyTagCodeDateFilter
    fun map(filterDto: ContactByServiceIdsFilter): org.taktik.icure.dto.filter.impl.contact.ContactByServiceIdsFilter
    fun map(filterDto: HealthElementByHcPartyTagCodeFilter): org.taktik.icure.dto.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter
    fun map(filterDto: InvoiceByHcPartyCodeDateFilter): org.taktik.icure.dto.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter
    fun map(filterDto: PatientByHcPartyAndActiveFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndActiveFilter
    fun map(filterDto: PatientByHcPartyAndExternalIdFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndExternalIdFilter
    fun map(filterDto: PatientByHcPartyAndSsinFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinFilter
    fun map(filterDto: PatientByHcPartyAndSsinsFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinsFilter
    fun map(filterDto: PatientByHcPartyDateOfBirthBetweenFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter
    fun map(filterDto: PatientByHcPartyDateOfBirthFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthFilter
    fun map(filterDto: PatientByHcPartyFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyFilter
    fun map(filterDto: PatientByHcPartyGenderEducationProfession): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyGenderEducationProfession
    fun map(filterDto: PatientByHcPartyNameContainsFuzzyFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter
    fun map(filterDto: PatientByHcPartyNameFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameFilter
    fun map(filterDto: PatientByIdsFilter): org.taktik.icure.dto.filter.impl.patient.PatientByIdsFilter
    fun map(filterDto: ServiceByContactsAndSubcontactsFilter): org.taktik.icure.dto.filter.impl.service.ServiceByContactsAndSubcontactsFilter
    fun map(filterDto: ServiceByHcPartyTagCodeDateFilter): org.taktik.icure.dto.filter.impl.service.ServiceByHcPartyTagCodeDateFilter
    fun map(filterDto: ServiceBySecretForeignKeys): org.taktik.icure.dto.filter.impl.service.ServiceBySecretForeignKeys

    fun map(filterDto: FilterDto<*>): Filter<String, *> {
        return when (filterDto) {
            is CodeByRegionTypeLabelLanguageFilter -> map(filterDto)
            is ContactByHcPartyPatientTagCodeDateFilter -> map(filterDto)
            is ContactByHcPartyTagCodeDateFilter -> map(filterDto)
            is ContactByServiceIdsFilter -> map(filterDto)
            is HealthElementByHcPartyTagCodeFilter -> map(filterDto)
            is InvoiceByHcPartyCodeDateFilter -> map(filterDto)
            is PatientByHcPartyAndActiveFilter -> map(filterDto)
            is PatientByHcPartyAndExternalIdFilter -> map(filterDto)
            is PatientByHcPartyAndSsinFilter -> map(filterDto)
            is PatientByHcPartyAndSsinsFilter -> map(filterDto)
            is PatientByHcPartyDateOfBirthBetweenFilter -> map(filterDto)
            is PatientByHcPartyDateOfBirthFilter -> map(filterDto)
            is PatientByHcPartyFilter -> map(filterDto)
            is PatientByHcPartyGenderEducationProfession -> map(filterDto)
            is PatientByHcPartyNameContainsFuzzyFilter -> map(filterDto)
            is PatientByHcPartyNameFilter -> map(filterDto)
            is PatientByIdsFilter -> map(filterDto)
            is ServiceByContactsAndSubcontactsFilter -> map(filterDto)
            is ServiceByHcPartyTagCodeDateFilter -> map(filterDto)
            is ServiceBySecretForeignKeys -> map(filterDto)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    fun map(filter: org.taktik.icure.dto.filter.impl.code.CodeByRegionTypeLabelLanguageFilter): CodeByRegionTypeLabelLanguageFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter): ContactByHcPartyPatientTagCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyTagCodeDateFilter): ContactByHcPartyTagCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.contact.ContactByServiceIdsFilter): ContactByServiceIdsFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter): HealthElementByHcPartyTagCodeFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter): InvoiceByHcPartyCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndActiveFilter): PatientByHcPartyAndActiveFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndExternalIdFilter): PatientByHcPartyAndExternalIdFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinFilter): PatientByHcPartyAndSsinFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinsFilter): PatientByHcPartyAndSsinsFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter): PatientByHcPartyDateOfBirthBetweenFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthFilter): PatientByHcPartyDateOfBirthFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyFilter): PatientByHcPartyFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyGenderEducationProfession): PatientByHcPartyGenderEducationProfession
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter): PatientByHcPartyNameContainsFuzzyFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameFilter): PatientByHcPartyNameFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByIdsFilter): PatientByIdsFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.service.ServiceByContactsAndSubcontactsFilter): ServiceByContactsAndSubcontactsFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.service.ServiceByHcPartyTagCodeDateFilter): ServiceByHcPartyTagCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.impl.service.ServiceBySecretForeignKeys): ServiceBySecretForeignKeys

    fun map(filter: Filter<String, *>): FilterDto<*> {
        return when (filter) {
            is org.taktik.icure.dto.filter.impl.code.CodeByRegionTypeLabelLanguageFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyTagCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.contact.ContactByServiceIdsFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndActiveFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndExternalIdFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinsFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyGenderEducationProfession -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.patient.PatientByIdsFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.service.ServiceByContactsAndSubcontactsFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.service.ServiceByHcPartyTagCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.impl.service.ServiceBySecretForeignKeys -> map(filter)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    fun map(predicate: OrPredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate
    fun map(predicate: AndPredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate
    fun map(predicate: NotPredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate
    fun map(predicate: KeyValuePredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate

    fun map(predicate: Predicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate {
        return when(predicate) {
            is OrPredicate -> map(predicate)
            is AndPredicate -> map(predicate)
            is NotPredicate -> map(predicate)
            is KeyValuePredicate -> map(predicate)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate): OrPredicate
    fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate): AndPredicate
    fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate): NotPredicate
    fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate): KeyValuePredicate

    fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate): Predicate {
        return when(predicateDto) {
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate -> map(predicateDto)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

}
