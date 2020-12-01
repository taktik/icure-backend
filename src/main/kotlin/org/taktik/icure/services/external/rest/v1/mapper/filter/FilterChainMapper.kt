package org.taktik.icure.services.external.rest.v1.mapper.filter

import org.springframework.stereotype.Service
import org.taktik.icure.domain.filter.AbstractFilter
import org.taktik.icure.entities.base.Identifiable
import org.taktik.icure.services.external.rest.v1.dto.filter.AbstractFilterDto
import org.taktik.icure.services.external.rest.v1.dto.filter.chain.FilterChain

@Service
class FilterChainMapper(val filterMapper: FilterMapper) {
    fun <O: Identifiable<String>> map(filterChain: org.taktik.icure.domain.filter.chain.FilterChain<O>): FilterChain<O> =
            FilterChain(filterChain.filter.let { filterMapper.map(it) } as AbstractFilterDto<O>, filterChain.predicate?.let { filterMapper.map(it) })

    fun <O: Identifiable<String>> map(filterChainDto: FilterChain<O>): org.taktik.icure.domain.filter.chain.FilterChain<O> =
            org.taktik.icure.domain.filter.chain.FilterChain(filterChainDto.filter.let { filterMapper.map(it) } as AbstractFilter<O>, filterChainDto.predicate?.let { filterMapper.map(it) })

}
