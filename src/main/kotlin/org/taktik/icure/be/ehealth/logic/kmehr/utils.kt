package org.taktik.icure.be.ehealth.logic.kmehr

fun validSsinOrNull(ssin : String?): String? {
    val res : String
    return if(ssin == null) {
        null
    } else {
        res = ssin.replace(" ", "").replace("-", "").replace(".", "").replace("/", "")
        if(res.length == 11) {
            res
        } else {
            null
        }
    }
}

fun validNihiiOrNull(nihii : String?): String? {
    val res : String
    return if(nihii == null) {
        null
    } else {
        res = nihii.replace(" ", "").replace("-", "").replace(".", "").replace("/", "")
        if(res.length == 11) {
            res
        } else {
            null
        }
    }
}
