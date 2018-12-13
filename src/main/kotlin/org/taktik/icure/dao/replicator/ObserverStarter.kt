package org.taktik.icure.dao.replicator

import com.hazelcast.spring.context.SpringAware
import org.springframework.beans.factory.annotation.Autowired

import java.io.Serializable

@SpringAware
class ObserverStarter : Runnable, Serializable {
    @Transient
    private var newGroupObserver: NewGroupObserver? = null

    override fun run() {
        newGroupObserver?.ensureObserverStarted()
    }

    @Autowired
    fun setNewGroupObserver(newGroupObserver: NewGroupObserver) {
        this.newGroupObserver = newGroupObserver
    }
}
