package org.taktik.icure.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.authentication")
data class AuthenticationProperties(
    var local:Boolean = false
)
