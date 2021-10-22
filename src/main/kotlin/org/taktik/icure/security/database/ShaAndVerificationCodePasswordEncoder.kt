/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package org.taktik.icure.security.database

import org.springframework.security.crypto.password.MessageDigestPasswordEncoder

class ShaAndVerificationCodePasswordEncoder(algorithm: String?) : MessageDigestPasswordEncoder(algorithm) {
    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return super.matches(rawPassword, encodedPassword) || super.matches(
            rawPassword.toString().split("\\|").toTypedArray()[0], encodedPassword
        )
    }
}
