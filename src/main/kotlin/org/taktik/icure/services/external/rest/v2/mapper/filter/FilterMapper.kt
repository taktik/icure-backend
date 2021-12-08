/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v2.mapper.filter

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.domain.filter.impl.predicate.AndPredicate
import org.taktik.icure.domain.filter.impl.predicate.KeyValuePredicate
import org.taktik.icure.domain.filter.impl.predicate.NotPredicate
import org.taktik.icure.domain.filter.impl.predicate.OrPredicate
import org.taktik.icure.domain.filter.predicate.Predicate
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.ComplementFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.ConstantFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.IntersectionFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.UnionFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.code.CodeByRegionTypeLabelLanguageFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.contact.ContactByHcPartyPatientTagCodeDateFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.contact.ContactByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.contact.ContactByServiceIdsFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.healthelement.HealthElementByHcPartyTagCodeFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.invoice.InvoiceByHcPartyCodeDateFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyAndActiveFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyAndExternalIdFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyAndIdentifiersFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyAndSsinFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyAndSsinsFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyDateOfBirthBetweenFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyDateOfBirthFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyGenderEducationProfession
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyNameContainsFuzzyFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByHcPartyNameFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.patient.PatientByIdsFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.service.ServiceByContactsAndSubcontactsFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.service.ServiceByHcPartyIdentifiersFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.service.ServiceByHcPartyTagCodeDateFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.service.ServiceBySecretForeignKeys

@Mapper(componentModel = "spring", uses = [], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class FilterV2Mapper {
    abstract fun map(filterDto: CodeByRegionTypeLabelLanguageFilter): org.taktik.icure.domain.filter.impl.code.CodeByRegionTypeLabelLanguageFilter
    abstract fun map(filterDto: ContactByHcPartyPatientTagCodeDateFilter): org.taktik.icure.domain.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter
    abstract fun map(filterDto: ContactByHcPartyTagCodeDateFilter): org.taktik.icure.domain.filter.impl.contact.ContactByHcPartyTagCodeDateFilter
    abstract fun map(filterDto: ContactByServiceIdsFilter): org.taktik.icure.domain.filter.impl.contact.ContactByServiceIdsFilter
    abstract fun map(filterDto: HealthElementByHcPartyTagCodeFilter): org.taktik.icure.domain.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter
    abstract fun map(filterDto: InvoiceByHcPartyCodeDateFilter): org.taktik.icure.domain.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter
    abstract fun map(filterDto: PatientByHcPartyAndActiveFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndActiveFilter
    abstract fun map(filterDto: PatientByHcPartyAndExternalIdFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndExternalIdFilter
    abstract fun map(filterDto: PatientByHcPartyAndSsinFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndSsinFilter
    abstract fun map(filterDto: PatientByHcPartyAndSsinsFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndSsinsFilter
    abstract fun map(filterDto: PatientByHcPartyDateOfBirthBetweenFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter
    abstract fun map(filterDto: PatientByHcPartyDateOfBirthFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyDateOfBirthFilter
    abstract fun map(filterDto: PatientByHcPartyFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyFilter
    abstract fun map(filterDto: PatientByHcPartyGenderEducationProfession): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyGenderEducationProfession
    abstract fun map(filterDto: PatientByHcPartyNameContainsFuzzyFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter
    abstract fun map(filterDto: PatientByHcPartyNameFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyNameFilter
    abstract fun map(filterDto: PatientByHcPartyAndIdentifiersFilter): org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndIdentifiersFilter
    abstract fun map(filterDto: PatientByIdsFilter): org.taktik.icure.domain.filter.impl.patient.PatientByIdsFilter
    abstract fun map(filterDto: ServiceByContactsAndSubcontactsFilter): org.taktik.icure.domain.filter.impl.service.ServiceByContactsAndSubcontactsFilter
    abstract fun map(filterDto: ServiceByHcPartyTagCodeDateFilter): org.taktik.icure.domain.filter.impl.service.ServiceByHcPartyTagCodeDateFilter
    abstract fun map(filterDto: ServiceByHcPartyIdentifiersFilter): org.taktik.icure.domain.filter.impl.service.ServiceByHcPartyIdentifiersFilter
    abstract fun map(filterDto: ServiceBySecretForeignKeys): org.taktik.icure.domain.filter.impl.service.ServiceBySecretForeignKeys
    fun <O: Identifiable<String>>map(filterDto: UnionFilter<O>): org.taktik.icure.domain.filter.impl.UnionFilter<O> {
        val filters: List<AbstractFilter<O>> = filterDto.filters.map { map(it) as AbstractFilter<O> }
        return org.taktik.icure.domain.filter.impl.UnionFilter(
                desc = filterDto.desc,
                filters = filters
        )
    }
    fun <O: Identifiable<String>>map(filterDto: IntersectionFilter<O>): org.taktik.icure.domain.filter.impl.IntersectionFilter<O> {
        val filters: List<AbstractFilter<O>> = filterDto.filters.map { map(it) as AbstractFilter<O> }
        return org.taktik.icure.domain.filter.impl.IntersectionFilter(
                desc = filterDto.desc,
                filters = filters
        )
    }
    fun <O: Identifiable<String>>map(filterDto: ComplementFilter<O>): org.taktik.icure.domain.filter.impl.ComplementFilter<O> {
        return org.taktik.icure.domain.filter.impl.ComplementFilter(
                desc = filterDto.desc,
                subSet = map(filterDto.subSet) as AbstractFilter<O>,
                superSet = map(filterDto.superSet) as  AbstractFilter<O>
        )
    }
    fun <O: Identifiable<String>>map(filterDto: ConstantFilter<O>): org.taktik.icure.domain.filter.impl.ConstantFilter<O> {
        return org.taktik.icure.domain.filter.impl.ConstantFilter(
                desc = filterDto.desc,
                constant = filterDto.constant
        )
    }

    fun map(filterDto: AbstractFilterDto<*>): AbstractFilter<*> {
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
            is PatientByHcPartyAndIdentifiersFilter -> map(filterDto)
            is PatientByIdsFilter -> map(filterDto)
            is ServiceByContactsAndSubcontactsFilter -> map(filterDto)
            is ServiceByHcPartyTagCodeDateFilter -> map(filterDto)
            is ServiceByHcPartyIdentifiersFilter -> map(filterDto)
            is ServiceBySecretForeignKeys -> map(filterDto)
            is UnionFilter -> map(filterDto)
            is IntersectionFilter -> map(filterDto)
            is ComplementFilter -> map(filterDto)
            is ConstantFilter -> map(filterDto)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    abstract fun map(filter: org.taktik.icure.domain.filter.impl.code.CodeByRegionTypeLabelLanguageFilter): CodeByRegionTypeLabelLanguageFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter): ContactByHcPartyPatientTagCodeDateFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.contact.ContactByHcPartyTagCodeDateFilter): ContactByHcPartyTagCodeDateFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.contact.ContactByServiceIdsFilter): ContactByServiceIdsFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter): HealthElementByHcPartyTagCodeFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter): InvoiceByHcPartyCodeDateFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndActiveFilter): PatientByHcPartyAndActiveFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndExternalIdFilter): PatientByHcPartyAndExternalIdFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndSsinFilter): PatientByHcPartyAndSsinFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndSsinsFilter): PatientByHcPartyAndSsinsFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter): PatientByHcPartyDateOfBirthBetweenFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyDateOfBirthFilter): PatientByHcPartyDateOfBirthFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyFilter): PatientByHcPartyFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyGenderEducationProfession): PatientByHcPartyGenderEducationProfession
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter): PatientByHcPartyNameContainsFuzzyFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyNameFilter): PatientByHcPartyNameFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndIdentifiersFilter): PatientByHcPartyAndIdentifiersFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.patient.PatientByIdsFilter): PatientByIdsFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.service.ServiceByContactsAndSubcontactsFilter): ServiceByContactsAndSubcontactsFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.service.ServiceByHcPartyTagCodeDateFilter): ServiceByHcPartyTagCodeDateFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.service.ServiceByHcPartyIdentifiersFilter): ServiceByHcPartyIdentifiersFilter
    abstract fun map(filter: org.taktik.icure.domain.filter.impl.service.ServiceBySecretForeignKeys): ServiceBySecretForeignKeys
    fun <O: Identifiable<String>>map(filterDto: org.taktik.icure.domain.filter.impl.UnionFilter<O>): UnionFilter<O> {
        val filters: List<AbstractFilterDto<O>> = filterDto.filters.map { map(it) as AbstractFilterDto<O> }
        return UnionFilter(
                desc = filterDto.desc,
                filters = filters
        )
    }
    fun <O: Identifiable<String>>map(filterDto: org.taktik.icure.domain.filter.impl.IntersectionFilter<O>): IntersectionFilter<O> {
        val filters: List<AbstractFilterDto<O>> = filterDto.filters.map { map(it) as AbstractFilterDto<O> }
        return IntersectionFilter(
                desc = filterDto.desc,
                filters = filters
        )
    }
    fun <O: Identifiable<String>>map(filterDto: org.taktik.icure.domain.filter.impl.ComplementFilter<O>): ComplementFilter<O> {
        return ComplementFilter(
                desc = filterDto.desc,
                subSet = map(filterDto.subSet) as AbstractFilterDto<O>,
                superSet = map(filterDto.superSet) as  AbstractFilterDto<O>
        )
    }
    fun <O: Identifiable<String>>map(filterDto: org.taktik.icure.domain.filter.impl.ConstantFilter<O>): ConstantFilter<O> {
        return ConstantFilter(
                desc = filterDto.desc,
                constant = filterDto.constant
        )
    }

    fun map(filter: AbstractFilter<*>): AbstractFilterDto<*> {
        return when (filter) {
            is org.taktik.icure.domain.filter.impl.code.CodeByRegionTypeLabelLanguageFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.contact.ContactByHcPartyPatientTagCodeDateFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.contact.ContactByHcPartyTagCodeDateFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.contact.ContactByServiceIdsFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.healthelement.HealthElementByHcPartyTagCodeFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.invoice.InvoiceByHcPartyCodeDateFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndActiveFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndExternalIdFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndSsinFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndSsinsFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyDateOfBirthBetweenFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyDateOfBirthFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyGenderEducationProfession -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyNameContainsFuzzyFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyNameFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByHcPartyAndIdentifiersFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.patient.PatientByIdsFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.service.ServiceByContactsAndSubcontactsFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.service.ServiceByHcPartyTagCodeDateFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.service.ServiceByHcPartyIdentifiersFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.service.ServiceBySecretForeignKeys -> map(filter)
            is org.taktik.icure.domain.filter.impl.UnionFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.IntersectionFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.ComplementFilter -> map(filter)
            is org.taktik.icure.domain.filter.impl.ConstantFilter -> map(filter)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    abstract fun map(predicate: OrPredicate): org.taktik.icure.services.external.rest.v2.dto.filter.predicate.OrPredicate
    abstract fun map(predicate: AndPredicate): org.taktik.icure.services.external.rest.v2.dto.filter.predicate.AndPredicate
    abstract fun map(predicate: NotPredicate): org.taktik.icure.services.external.rest.v2.dto.filter.predicate.NotPredicate
    abstract fun map(predicate: KeyValuePredicate): org.taktik.icure.services.external.rest.v2.dto.filter.predicate.KeyValuePredicate

    fun map(predicate: Predicate): org.taktik.icure.services.external.rest.v2.dto.filter.predicate.Predicate {
        return when(predicate) {
            is OrPredicate -> map(predicate)
            is AndPredicate -> map(predicate)
            is NotPredicate -> map(predicate)
            is KeyValuePredicate -> map(predicate)
            else -> throw IllegalArgumentException("Unsupported filter class")
        }
    }

    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v2.dto.filter.predicate.OrPredicate): OrPredicate
    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v2.dto.filter.predicate.AndPredicate): AndPredicate
    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v2.dto.filter.predicate.NotPredicate): NotPredicate
    abstract fun map(predicateDto: org.taktik.icure.services.external.rest.v2.dto.filter.predicate.KeyValuePredicate): KeyValuePredicate

    fun map(predicateDto: org.taktik.icure.services.external.rest.v2.dto.filter.predicate.Predicate): Predicate {
        return when(predicateDto) {
            is org.taktik.icure.services.external.rest.v2.dto.filter.predicate.OrPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v2.dto.filter.predicate.AndPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v2.dto.filter.predicate.NotPredicate -> map(predicateDto)
            is org.taktik.icure.services.external.rest.v2.dto.filter.predicate.KeyValuePredicate -> map(predicateDto)
            else -> throw IllegalArgumentException("Unsupported predicate class")
        }
    }

}
