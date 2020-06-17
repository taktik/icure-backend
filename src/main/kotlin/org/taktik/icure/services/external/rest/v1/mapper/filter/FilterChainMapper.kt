package org.taktik.icure.services.external.rest.v1.mapper.filter

import org.springframework.stereotype.Service
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.v1.dto.filter.FilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain

@Service
class FilterChainMapper(val filterMapper: FilterMapper) {
    fun <O: Identifiable<String>> map(filterChain: org.taktik.icure.dto.filter.chain.FilterChain<O>): FilterChain<O> =
            FilterChain(filterMapper.map(filterChain.filter) as FilterDto<O>, filterChain.predicate?.let { filterMapper.map(it) })

    fun <O: Identifiable<String>> map(filterChainDto: FilterChain<O>): org.taktik.icure.dto.filter.chain.FilterChain<O> =
            org.taktik.icure.dto.filter.chain.FilterChain(filterMapper.map(filterChainDto.filter) as org.taktik.icure.dto.filter.Filter<String, O>, filterChainDto.predicate?.let { filterMapper.map(it) })

}
