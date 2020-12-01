package org.taktik.icure.exceptions;

public class PersistenceException extends RuntimeException {
	public PersistenceException()
	{
		super();
	}

	public PersistenceException(String message){
		super(message);
	}

	public PersistenceException(String message, Throwable t){
		super(message, t);
	}
}
