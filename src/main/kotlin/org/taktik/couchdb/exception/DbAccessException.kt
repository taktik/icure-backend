package org.taktik.couchdb.exception

/**
 *
 * @author henrik lundgren
 */
open class DbAccessException : RuntimeException {
    constructor(t: Throwable?) : super(t) {}
    constructor(message: String?) : super(message) {}
    constructor() {}
}
