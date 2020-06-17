package org.taktik.icure.services.external.rest.v1.mapper.filter

import com.github.pozo.KotlinBuilder
import org.mapstruct.InjectionStrategy
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
import org.taktik.icure.services.external.rest.v1.mapper.PropertyTypeMapper
import org.taktik.icure.services.external.rest.v1.mapper.base.CodeStubMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.DocumentGroupMapper
import org.taktik.icure.services.external.rest.v1.mapper.embed.TypedValueMapper


abstract class FilterMapper {
    abstract fun map(filterDto: CodeByRegionTypeLabelLanguageFilter): org.taktik.icure.dto.filter.impl.code.CodeByRegionTypeLabelLanguageFilter
    abstract fun map(filterDto: ContactByHcPartyPatientTagCodeDateFilter): org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter
    abstract fun map(filterDto: ContactByHcPartyTagCodeDateFilter): org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyTagCodeDateFilter
    abstract fun map(filterDto: ContactByServiceIdsFilter): org.taktik.icure.dto.filter.impl.contact.ContactByServiceIdsFilter
    abstract fun map(filterDto: HealthElementByHcPartyTagCodeFilter): org.taktik.icure.dto.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter
    abstract fun map(filterDto: InvoiceByHcPartyCodeDateFilter): org.taktik.icure.dto.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter
    abstract fun map(filterDto: PatientByHcPartyAndActiveFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndActiveFilter
    abstract fun map(filterDto: PatientByHcPartyAndExternalIdFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndExternalIdFilter
    abstract fun map(filterDto: PatientByHcPartyAndSsinFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinFilter
    abstract fun map(filterDto: PatientByHcPartyAndSsinsFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinsFilter
    abstract fun map(filterDto: PatientByHcPartyDateOfBirthBetweenFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter
    abstract fun map(filterDto: PatientByHcPartyDateOfBirthFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthFilter
    abstract fun map(filterDto: PatientByHcPartyFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyFilter
    abstract fun map(filterDto: PatientByHcPartyGenderEducationProfession): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyGenderEducationProfession
    abstract fun map(filterDto: PatientByHcPartyNameContainsFuzzyFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter
    abstract fun map(filterDto: PatientByHcPartyNameFilter): org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameFilter
    abstract fun map(filterDto: PatientByIdsFilter): org.taktik.icure.dto.filter.impl.patient.PatientByIdsFilter
    abstract fun map(filterDto: ServiceByContactsAndSubcontactsFilter): org.taktik.icure.dto.filter.impl.service.ServiceByContactsAndSubcontactsFilter
    abstract fun map(filterDto: ServiceByHcPartyTagCodeDateFilter): org.taktik.icure.dto.filter.impl.service.ServiceByHcPartyTagCodeDateFilter
    abstract fun map(filterDto: ServiceBySecretForeignKeys): org.taktik.icure.dto.filter.impl.service.ServiceBySecretForeignKeys

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

    abstract fun map(filter: org.taktik.icure.dto.filter.impl.code.CodeByRegionTypeLabelLanguageFilter): CodeByRegionTypeLabelLanguageFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter): ContactByHcPartyPatientTagCodeDateFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.contact.ContactByHcPartyTagCodeDateFilter): ContactByHcPartyTagCodeDateFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.contact.ContactByServiceIdsFilter): ContactByServiceIdsFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter): HealthElementByHcPartyTagCodeFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter): InvoiceByHcPartyCodeDateFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndActiveFilter): PatientByHcPartyAndActiveFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndExternalIdFilter): PatientByHcPartyAndExternalIdFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinFilter): PatientByHcPartyAndSsinFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyAndSsinsFilter): PatientByHcPartyAndSsinsFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter): PatientByHcPartyDateOfBirthBetweenFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyDateOfBirthFilter): PatientByHcPartyDateOfBirthFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyFilter): PatientByHcPartyFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyGenderEducationProfession): PatientByHcPartyGenderEducationProfession
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter): PatientByHcPartyNameContainsFuzzyFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByHcPartyNameFilter): PatientByHcPartyNameFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.patient.PatientByIdsFilter): PatientByIdsFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.service.ServiceByContactsAndSubcontactsFilter): ServiceByContactsAndSubcontactsFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.service.ServiceByHcPartyTagCodeDateFilter): ServiceByHcPartyTagCodeDateFilter
    abstract fun map(filter: org.taktik.icure.dto.filter.impl.service.ServiceBySecretForeignKeys): ServiceBySecretForeignKeys

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

    abstract fun map(predicate: OrPredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate
    abstract fun map(predicate: AndPredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate
    abstract fun map(predicate: NotPredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate
    abstract fun map(predicate: KeyValuePredicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate

    fun map(predicate: Predicate): org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate {
        return when(predicate) {
            is OrPredicate -> map(predicate)
            is AndPredicate -> map(predicate)
            is NotPredicate -> map(predicate)
            is KeyValuePredicate -> map(predicate)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate): OrPredicate
    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate): AndPredicate
    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate): NotPredicate
    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate): KeyValuePredicate

    fun map(predicateDto: org.taktik.icure.services.external.rest.v1.dto.filter.predicate.Predicate): Predicate {
        return when(predicateDto) {
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.OrPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.AndPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.NotPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v1.dto.filter.predicate.KeyValuePredicate -> map(predicateDto)
            else -> throw IllegalArgumentException("Unsupported predicate class")
        }
    }

}
