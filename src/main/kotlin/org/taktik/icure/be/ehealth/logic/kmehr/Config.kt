package org.taktik.icure.be.ehealth.logic.kmehr

import javax.xml.datatype.XMLGregorianCalendar

data class Config(var _kmehrId: String? = null, var date: XMLGregorianCalendar? = null, var time: XMLGregorianCalendar? = null, var soft: Software? = null, var clinicalSummaryType: String? = null, var defaultLanguage: String? = null, var format:Format? = null) {
    data class Software(val name : String, val version : String)
    enum class Format {
        KMEHR,
        SUMEHR,
        SMF,
        PMF,
        MEDEX
    }
}

