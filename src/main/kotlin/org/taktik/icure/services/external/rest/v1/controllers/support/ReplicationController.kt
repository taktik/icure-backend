package org.taktik.icure.services.external.rest.v1.controllers.support

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.*
import org.taktik.icure.asynclogic.ReplicationLogic
import org.taktik.icure.services.external.rest.v1.dto.ReplicateCommandDto
import java.net.URI

@ExperimentalCoroutinesApi
@RestController
@RequestMapping("/rest/v1/replication")
@Tag(name = "replication")
class ReplicationController(
        private val replicationLogic: ReplicationLogic
) {
    @Operation(summary = "Get replication documents", description = "Get all replication infos and states")
    @GetMapping("/docs")
    fun getReplicationDocs() = mono {
        replicationLogic.listReplicationDocs()
    }

    @Operation(summary = "Create one time replication document", description = "Create a document to start a one time replication")
    @PostMapping("/onetime")
    fun createOneTimeReplicationDoc(@RequestBody command: ReplicateCommandDto) = mono {
        replicationLogic.createOneTimeReplicationDoc(
                sourceUrl = URI(command.sourceUrl),
                sourceUsername = command.sourceUsername,
                sourcePassword = command.sourcePassword,
                targetUrl = URI(command.targetUrl),
                targetUsername = command.targetUsername,
                targetPassword = command.targetPassword,
                id = command.id
        )
    }

    @Operation(summary = "Create continuous replication document", description = "Create a document to start a continuous replication")
    @PostMapping("/continuous")
    fun createContinuousReplicationDoc(@RequestBody command: ReplicateCommandDto) = mono {
        replicationLogic.createContinuousReplicationDoc(
                sourceUrl = URI(command.sourceUrl),
                sourceUsername = command.sourceUsername,
                sourcePassword = command.sourcePassword,
                targetUrl = URI(command.targetUrl),
                targetUsername = command.targetUsername,
                targetPassword = command.targetPassword,
                id = command.id
        )
    }

    @Operation(summary = "Delete replication document to stop it", description = "DocId is the id provided by a replicator document from replication/docs")
    @PostMapping("/stop/{docId}")
    fun deleteReplicationDoc(@PathVariable docId: String) = mono {
        replicationLogic.deleteReplicationDoc(docId)
    }
}
