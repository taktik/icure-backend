package org.taktik.couchdb

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.http.client.utils.URIBuilder
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.api.Response
import org.eclipse.jetty.client.api.Result
import org.eclipse.jetty.client.util.BufferingResponseListener
import org.eclipse.jetty.http.HttpHeader
import org.slf4j.LoggerFactory
import org.taktik.couchdb.parser.AsyncDeserializerImpl
import org.taktik.couchdb.parser.PartialJsonParser
import java.net.URI
import java.util.Base64
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit


/**
 * @author aduchate on 17/02/2017.
 */
class CouchDbInstance(val httpClient: HttpClient,
                      val baseUrl: URI,
                      val dbName: String,
                      val user: String? = null,
                      val password: String? = null) {
    private val log = LoggerFactory.getLogger(CouchDbInstance::class.java)

    fun exists(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        val uri = URIBuilder("${baseUrl.normalize()}/$dbName").build()

        try {
            val request = httpClient.newRequest(uri).timeout(5, TimeUnit.SECONDS)
                .apply { user?.let { u -> password?.let { p -> header(HttpHeader.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(("$u:$p").toByteArray())) } } }

            request.send(object : BufferingResponseListener() {
               override fun onComplete(it: Result?) {
                    try {
                        if (it == null || it.isFailed) {
                            future.completeExceptionally(it!!.failure)
                        } else {
                            val content = content
                            future.complete(content.isNotEmpty() && ObjectMapper().readValue(content, ObjectNode::class.java)?.get("db_name") != null)
                        }
                    } catch (e: Exception) {
                        future.completeExceptionally(e)
                    }
                }

                override fun onFailure(response: Response?, failure: Throwable?) {
                    log.info("Failure for ${uri}")

                    future.completeExceptionally(failure)
                }
            })
        } catch (e: Exception) {
            future.completeExceptionally(e)
        }
        return future
    }

    fun changes(since: String?, changeDetected: (Change) -> Unit, heartBeat: () -> Unit) : CompletableFuture<Unit> {
        val future = CompletableFuture<Unit>()

        val parser = PartialJsonParser(AsyncDeserializerImpl(Change::class.java, changeDetected))

        val uri = URIBuilder("${baseUrl.normalize()}/$dbName/_changes")
            .setParameter("feed", "continuous")
            .setParameter("heartbeat", "10000")
            .setParameter("include_docs", "true")
            .apply { since?.let { setParameter("since", since) } }
            .build()
        log.info("URI: $uri: start request")
        httpClient.newRequest(uri)
            .apply {
                user?.let { u -> password?.let { p -> header(HttpHeader.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(("$u:$p").toByteArray())) } }
                idleTimeout(60, TimeUnit.SECONDS)
                timeout(60, TimeUnit.DAYS)
            }
            .onResponseContent { _, byteBuffer ->
                log.debug("URI: "+uri+": "+byteBuffer.remaining())
                parser.parse(byteBuffer)
                heartBeat()
            }
            .onResponseSuccess {
                future.complete(Unit)
            }
            .onResponseFailure { _, failure ->
                log.error("URI: $uri: request failed with user $user:$password", failure)
                future.completeExceptionally(failure)
            }
            .onRequestFailure { _, failure ->
                log.error("URI: $uri: request failed ", failure)
                future.completeExceptionally(failure)
            }.send {}
        return future
    }
}
