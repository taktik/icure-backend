package org.taktik.icure.dao.replicator;

import com.hazelcast.spring.context.SpringAware;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@SpringAware
public class ObserverStarter implements Runnable, Serializable {
	private transient NewGroupObserver newGroupObserver;

	@Override
	public void run() {
		if (newGroupObserver != null) { newGroupObserver.ensureObserverStarted(); }
	}

	@Autowired
	public void setNewGroupObserver(NewGroupObserver newGroupObserver) {
		this.newGroupObserver = newGroupObserver;
	}
}
