package org.taktik.icure.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoCloseInputStream extends FilterInputStream {
    public NoCloseInputStream(InputStream is) {
        super(is);
    }

    @Override
    public void close() throws IOException {
        // Ignore. Use doClose() to close
    }

    public void doClose() throws IOException {
        super.close();
    }
}
