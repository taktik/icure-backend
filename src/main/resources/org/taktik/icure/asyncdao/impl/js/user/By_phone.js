function (doc) {
    if (doc.java_type === 'org.taktik.icure.entities.User' && !doc.deleted && doc.mobilePhone) {
        var m = doc.mobilePhone.trim();

        var nationalCodeRegex = /^\+(1|2[07]|2[1234569][0-9]|3[0123469]|3[578][0_9]|4[013-9]|42[0-9]|5[1-8]|5[09][0-9]|6[0-6]|6[789][0-9]|7[0-9]|8[1246]|8[0578][0-9]|9[80-5]|9[679][0-9])/
        var fullNormalized = m[0] === '+' ? '+' + m.replace(/[^0-9]/g, '') : m.replace(/[^0-9]/g, '')
        var noNationalCode = fullNormalized.replace(nationalCodeRegex, '')
        var zeroPrefixedNoNationalCode = "0" + noNationalCode

        emit(fullNormalized, null)
        emit(noNationalCode, null)
        emit(zeroPrefixedNoNationalCode, null)
    }
}
