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

import org.springframework.stereotype.Service
import org.taktik.couchdb.id.Identifiable
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.services.external.rest.v2.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v2.dto.filter.chain.FilterChain

@Service
class FilterChainV2Mapper(val filterV2Mapper: FilterV2Mapper) {
    fun <O: Identifiable<String>> map(filterChain: org.taktik.icure.domain.filter.chain.FilterChain<O>): FilterChain<O> =
            FilterChain(filterChain.filter.let { filterV2Mapper.map(it) } as AbstractFilterDto<O>, filterChain.predicate?.let { filterV2Mapper.map(it) })

    fun <O: Identifiable<String>> map(filterChainDto: FilterChain<O>): org.taktik.icure.domain.filter.chain.FilterChain<O> =
            org.taktik.icure.domain.filter.chain.FilterChain(filterChainDto.filter.let { filterV2Mapper.map(it) } as AbstractFilter<O>, filterChainDto.predicate?.let { filterV2Mapper.map(it) })

}
