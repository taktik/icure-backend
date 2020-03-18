package org.ektorp.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 *
 * @author henrik lundgren
 *
 */
public class StdObjectMapperFactory implements ObjectMapperFactory {
	private ObjectMapper instance;
    public synchronized ObjectMapper createObjectMapper() {
		if (instance == null) {
			instance = new ObjectMapper();
            instance.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
		return instance;
	}
}
