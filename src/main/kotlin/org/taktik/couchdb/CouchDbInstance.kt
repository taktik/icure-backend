package org.taktik.couchdb

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.http.client.utils.URIBuilder
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.api.Result
import org.eclipse.jetty.client.util.BufferingResponseListener
import org.eclipse.jetty.http.HttpHeader
import org.taktik.couchdb.parser.AsyncDeserializerImpl
import org.taktik.couchdb.parser.PartialJsonParser
import java.net.URI
import java.util.*
import org.h2.value.DataType.readValue
import java.util.concurrent.CompletableFuture


/**
 * @author aduchate on 17/02/2017.
 */
class CouchDbInstance(val httpClient : HttpClient, val baseUrl : URI, val dbName : String, val user : String? = null, val password : String? = null) {

    fun exists() : CompletableFuture<Boolean> {
		val future = CompletableFuture<Boolean>()

        val uri = URIBuilder("${baseUrl.normalize()}/$dbName").build()

		httpClient.newRequest(uri).apply { user?.let { u -> password?.let { p -> header(HttpHeader.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((u + ":" + p).toByteArray())) } } }
			.send(object : BufferingResponseListener() {
				override fun onComplete(it: Result?) {
					if(it == null || it.isFailed) {
						future.completeExceptionally(it!!.failure)
					} else {
						val content = content
						future.complete(content.isNotEmpty() && ObjectMapper().readValue(content, ObjectNode::class.java)?.get("db_name") != null)
					}
				}
			})
		return future
    }

    fun changes(since: String?, changeDetected: (Change) -> Unit, heartBeat:() -> Unit) {
        val parser = PartialJsonParser(AsyncDeserializerImpl(Change::class.java, changeDetected))

        val uri = URIBuilder("${baseUrl.normalize()}/$dbName/_changes")
                .setParameter("feed", "continuous")
                .setParameter("heartbeat", "10000")
                .setParameter("include_docs", "true")
                .apply { since?.let { setParameter("since", since) } }
                .build()
        httpClient.newRequest(uri).apply { user?.let {u -> password?.let { p -> header(HttpHeader.AUTHORIZATION, "Basic "+Base64.getEncoder().encodeToString((u+":"+p).toByteArray())) }}}
                .onResponseContent { response, byteBuffer ->
                    parser.parse(byteBuffer)
                    heartBeat()
                } .send {}

    }
}
