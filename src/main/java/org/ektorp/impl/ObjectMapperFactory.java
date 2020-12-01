package org.ektorp.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author henrik lundgren
 *
 */
public interface ObjectMapperFactory {
	ObjectMapper createObjectMapper();
}
