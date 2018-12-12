package org.taktik.couchdb.parser

/**
 * treat startObject, endObject, ... events from Json parser to build the corresponding java object
 *
 * @author Bernard Paulus - 27/02/2017
 */
interface AsyncDeserializer : FluentValueStart {
    fun endObject()
    fun endArray()
    fun clean()
    fun isClean(): Boolean
    fun getCurrentBuildPathString(): String
    fun addField(fieldName: String): FluentValueStart
}