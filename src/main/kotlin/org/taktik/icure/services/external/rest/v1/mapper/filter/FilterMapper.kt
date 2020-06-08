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

@Mapper(componentModel = "spring")
interface FilterMapper {
    fun <O : Identifiable<String>> map(filterChainDto: FilterChain): org.taktik.icure.dto.filter.chain.FilterChain<O>
    fun <O : Identifiable<String>> map(accessLog: org.taktik.icure.dto.filter.chain.FilterChain<O>): FilterChain

    fun map(filterDto: CodeByRegionTypeLabelLanguageFilter): org.taktik.icure.dto.filter.code.CodeByRegionTypeLabelLanguageFilter
    fun map(filterDto: ContactByHcPartyPatientTagCodeDateFilter): org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter
    fun map(filterDto: ContactByHcPartyTagCodeDateFilter): org.taktik.icure.dto.filter.contact.ContactByHcPartyTagCodeDateFilter
    fun map(filterDto: ContactByServiceIdsFilter): org.taktik.icure.dto.filter.contact.ContactByServiceIdsFilter
    fun map(filterDto: HealthElementByHcPartyTagCodeFilter): org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter
    fun map(filterDto: InvoiceByHcPartyCodeDateFilter): org.taktik.icure.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter
    fun map(filterDto: PatientByHcPartyAndActiveFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyAndActiveFilter
    fun map(filterDto: PatientByHcPartyAndExternalIdFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyAndExternalIdFilter
    fun map(filterDto: PatientByHcPartyAndSsinFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinFilter
    fun map(filterDto: PatientByHcPartyAndSsinsFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinsFilter
    fun map(filterDto: PatientByHcPartyDateOfBirthBetweenFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter
    fun map(filterDto: PatientByHcPartyDateOfBirthFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthFilter
    fun map(filterDto: PatientByHcPartyFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyFilter
    fun map(filterDto: PatientByHcPartyGenderEducationProfession): org.taktik.icure.dto.filter.patient.PatientByHcPartyGenderEducationProfession
    fun map(filterDto: PatientByHcPartyNameContainsFuzzyFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter
    fun map(filterDto: PatientByHcPartyNameFilter): org.taktik.icure.dto.filter.patient.PatientByHcPartyNameFilter
    fun map(filterDto: PatientByIdsFilter): org.taktik.icure.dto.filter.patient.PatientByIdsFilter
    fun map(filterDto: ServiceByContactsAndSubcontactsFilter): org.taktik.icure.dto.filter.service.ServiceByContactsAndSubcontactsFilter
    fun map(filterDto: ServiceByHcPartyTagCodeDateFilter): org.taktik.icure.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
    fun map(filterDto: ServiceBySecretForeignKeys): org.taktik.icure.dto.filter.service.ServiceBySecretForeignKeys

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

    fun map(filter: org.taktik.icure.dto.filter.code.CodeByRegionTypeLabelLanguageFilter): CodeByRegionTypeLabelLanguageFilter
    fun map(filter: org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter): ContactByHcPartyPatientTagCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.contact.ContactByHcPartyTagCodeDateFilter): ContactByHcPartyTagCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.contact.ContactByServiceIdsFilter): ContactByServiceIdsFilter
    fun map(filter: org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter): HealthElementByHcPartyTagCodeFilter
    fun map(filter: org.taktik.icure.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter): InvoiceByHcPartyCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyAndActiveFilter): PatientByHcPartyAndActiveFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyAndExternalIdFilter): PatientByHcPartyAndExternalIdFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinFilter): PatientByHcPartyAndSsinFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinsFilter): PatientByHcPartyAndSsinsFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter): PatientByHcPartyDateOfBirthBetweenFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthFilter): PatientByHcPartyDateOfBirthFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyFilter): PatientByHcPartyFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyGenderEducationProfession): PatientByHcPartyGenderEducationProfession
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter): PatientByHcPartyNameContainsFuzzyFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByHcPartyNameFilter): PatientByHcPartyNameFilter
    fun map(filter: org.taktik.icure.dto.filter.patient.PatientByIdsFilter): PatientByIdsFilter
    fun map(filter: org.taktik.icure.dto.filter.service.ServiceByContactsAndSubcontactsFilter): ServiceByContactsAndSubcontactsFilter
    fun map(filter: org.taktik.icure.dto.filter.service.ServiceByHcPartyTagCodeDateFilter): ServiceByHcPartyTagCodeDateFilter
    fun map(filter: org.taktik.icure.dto.filter.service.ServiceBySecretForeignKeys): ServiceBySecretForeignKeys

    fun map(filter: Filter<String, *>): FilterDto<*> {
        return when (filter) {
            is org.taktik.icure.dto.filter.code.CodeByRegionTypeLabelLanguageFilter -> map(filter)
            is org.taktik.icure.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.contact.ContactByHcPartyTagCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.contact.ContactByServiceIdsFilter -> map(filter)
            is org.taktik.icure.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter -> map(filter)
            is org.taktik.icure.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyAndActiveFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyAndExternalIdFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyAndSsinsFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyDateOfBirthFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyGenderEducationProfession -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByHcPartyNameFilter -> map(filter)
            is org.taktik.icure.dto.filter.patient.PatientByIdsFilter -> map(filter)
            is org.taktik.icure.dto.filter.service.ServiceByContactsAndSubcontactsFilter -> map(filter)
            is org.taktik.icure.dto.filter.service.ServiceByHcPartyTagCodeDateFilter -> map(filter)
            is org.taktik.icure.dto.filter.service.ServiceBySecretForeignKeys -> map(filter)
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
