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

package org.taktik.icure.services.external.rest.v2.controllers.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asynclogic.KeywordLogic
import org.taktik.icure.services.external.rest.v2.dto.KeywordDto
import org.taktik.icure.services.external.rest.v2.dto.ListOfIdsDto
import org.taktik.icure.services.external.rest.v2.mapper.KeywordV2Mapper
import org.taktik.icure.utils.injectReactorContext
import reactor.core.publisher.Flux

@RestController("keywordControllerV2")
@RequestMapping("/rest/v2/keyword")
@Tag(name = "keyword")
class KeywordController(private val keywordLogic: KeywordLogic, private val keywordV2Mapper: KeywordV2Mapper) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Create a keyword with the current user", description = "Returns an instance of created keyword.")
    @PostMapping
    fun createKeyword(@RequestBody c: KeywordDto) = mono {
        keywordLogic.createKeyword(keywordV2Mapper.map(c))?.let { keywordV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Keyword creation failed.")
    }

    @Operation(summary = "Get a keyword")
    @GetMapping("/{keywordId}")
    fun getKeyword(@PathVariable keywordId: String) = mono {
        keywordLogic.getKeyword(keywordId)?.let { keywordV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Getting keyword failed. Possible reasons: no such keyword exists, or server error. Please try again or read the server log.")
    }

    @Operation(summary = "Get keywords by user")
    @GetMapping("/byUser/{userId}")
    fun getKeywordsByUser(@PathVariable userId: String) =
            keywordLogic.getKeywordsByUser(userId).let { it.map { c -> keywordV2Mapper.map(c) } }.injectReactorContext()

    @Operation(summary = "Gets all keywords")
    @GetMapping
    fun getKeywords(): Flux<KeywordDto> {
        return keywordLogic.getEntities().map { c -> keywordV2Mapper.map(c) }.injectReactorContext()
    }

    @Operation(summary = "Delete keywords.", description = "Response is a set containing the ID's of deleted keywords.")
    @PostMapping("/delete/batch")
    fun deleteKeywords(@RequestBody keywordIds: ListOfIdsDto): Flux<DocIdentifier> {
        return keywordIds.ids.takeIf { it.isNotEmpty() }
                ?.let { ids ->
                    try {
                        keywordLogic.deleteEntities(ids.toSet()).injectReactorContext()
                    }
                    catch (e: java.lang.Exception) {
                        throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message).also { logger.error(it.message) }
                    }
                }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A required query parameter was not specified for this request.").also { logger.error(it.message) }
    }

    @Operation(summary = "Modify a keyword", description = "Returns the modified keyword.")
    @PutMapping
    fun modifyKeyword(@RequestBody keywordDto: KeywordDto) = mono {
        keywordLogic.modifyKeyword(keywordV2Mapper.map(keywordDto))
        keywordLogic.getKeyword(keywordDto.id)?.let { keywordV2Mapper.map(it) }
                ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Keyword modification failed.")
    }
}
