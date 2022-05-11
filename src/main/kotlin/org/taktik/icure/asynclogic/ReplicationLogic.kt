package org.taktik.icure.asynclogic

import java.net.URI
import org.taktik.couchdb.ReplicatorResponse
import org.taktik.couchdb.entity.ReplicatorDocument

interface ReplicationLogic {
	suspend fun createOneTimeReplicationDoc(
		sourceUrl: URI,
		sourceUsername: String,
		sourcePassword: String,
		targetUrl: URI,
		targetUsername: String,
		targetPassword: String,
		id: String?
	): ReplicatorResponse

	suspend fun createContinuousReplicationDoc(
		sourceUrl: URI,
		sourceUsername: String,
		sourcePassword: String,
		targetUrl: URI,
		targetUsername: String,
		targetPassword: String,
		id: String?
	): ReplicatorResponse

	suspend fun deleteReplicationDoc(docId: String): ReplicatorResponse
	suspend fun listReplicationDocs(): List<ReplicatorDocument>
}
