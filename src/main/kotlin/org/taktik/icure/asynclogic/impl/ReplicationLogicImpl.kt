package org.taktik.icure.asynclogic.impl

import java.net.URI
import org.springframework.stereotype.Service
import org.taktik.couchdb.ReplicatorResponse
import org.taktik.couchdb.entity.ReplicateCommand
import org.taktik.couchdb.entity.ReplicationStats
import org.taktik.couchdb.entity.ReplicatorDocument
import org.taktik.icure.asyncdao.ICureDAO
import org.taktik.icure.asynclogic.ReplicationLogic

@Service
class ReplicationLogicImpl(private val iCureDAO: ICureDAO) : ReplicationLogic {
	override suspend fun createOneTimeReplicationDoc(sourceUrl: URI, sourceUsername: String, sourcePassword: String, targetUrl: URI, targetUsername: String, targetPassword: String, id: String?): ReplicatorResponse {
		val command = ReplicateCommand.oneTime(sourceUrl, sourceUsername, sourcePassword, targetUrl, targetUsername, targetPassword, id)
		return iCureDAO.replicate(command)
	}

	override suspend fun createContinuousReplicationDoc(sourceUrl: URI, sourceUsername: String, sourcePassword: String, targetUrl: URI, targetUsername: String, targetPassword: String, id: String?): ReplicatorResponse {
		val command = ReplicateCommand.continuous(sourceUrl, sourceUsername, sourcePassword, targetUrl, targetUsername, targetPassword, id)
		return iCureDAO.replicate(command)
	}

	override suspend fun deleteReplicationDoc(docId: String): ReplicatorResponse = iCureDAO.deleteReplicatorDoc(docId)

	override suspend fun listReplicationDocs(): List<ReplicatorDocument> {
		val schedulerDocs = iCureDAO.getSchedulerDocs().docs
		return schedulerDocs
			.filter { it.database?.contains("replicator") ?: false }
			.map { doc ->
				ReplicatorDocument(
					id = doc.docId!!,
					rev = null,
					revsInfo = null,
					source = ReplicateCommand.Remote(url = doc.source!!),
					target = ReplicateCommand.Remote(url = doc.target!!),
					continuous = doc.id?.contains("continuous") ?: false,
					errorCount = doc.errorCount,
					replicationState = "${doc.state?.name}|${if (doc.state?.healthy == true) "healthy" else "unhealthy"}|${if (doc.state?.terminal == true) "terminal" else "non-terminal"}",
					replicationStateTime = doc.lastUpdated,
					replicationStats = ReplicationStats(
						revisionsChecked = doc.info?.revisionsChecked,
						missingRevisionsFound = doc.info?.missingRevisionsFound,
						docsRead = doc.info?.docsRead,
						docsWritten = doc.info?.docsWritten,
						changesPending = doc.info?.changesPending,
						docWriteFailures = doc.info?.docWriteFailures,
						checkpointedSourceSeq = doc.info?.checkpointedSourceSeq,
						startTime = doc.startTime,
						error = doc.info?.error
					)
				)
			}
	}
}
