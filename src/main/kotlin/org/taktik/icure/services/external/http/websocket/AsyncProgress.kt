package org.taktik.icure.services.external.http.websocket;

import java.io.IOException;

public interface AsyncProgress {
	void progress(Double progress) throws IOException;
}
