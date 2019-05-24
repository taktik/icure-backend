package org.taktik.icure.entities.samv2.embed

import java.io.Serializable
class Company(
        from: Long? = null,
        to: Long? = null,
        var authorisationNr: String? = null,
        var vatNr: Map<String, String>? = null,
        var europeanNr: String? = null,
        var denomination: String? = null,
        var legalForm: String? = null,
        var building: String? = null,
        var streetName: String? = null,
        var streetNum: String? = null,
        var postbox: String? = null,
        var postcode: String? = null,
        var city: String? = null,
        var countryCode: String? = null,
        var phone: String? = null,
        var language: String? = null,
        var website: String? = null
) : DataPeriod(from, to), Serializable