package org.taktik.icure.exceptions

/**
 * Exception thrown if there is an error with the storage of an attachment to object storage.
 */
class ObjectStoreException(msg: String, cause: Throwable? = null): ICureException(msg, cause)
