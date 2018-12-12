package org.taktik.icure.dao.replicator

import org.eclipse.jetty.util.ssl.SslContextFactory
import org.taktik.icure.entities.Group

import java.util.concurrent.CompletableFuture

/**
 * @author Bernard Paulus - 13/03/2017
 */
interface FilteredReplicator {
    fun startReplication(group: Group, sslContextFactory: SslContextFactory): CompletableFuture<Boolean>
}
