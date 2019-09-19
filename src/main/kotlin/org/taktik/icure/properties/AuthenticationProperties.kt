package org.taktik.icure.properties

import com.sun.org.apache.xpath.internal.operations.Bool
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("icure.authentication")
data class AuthenticationProperties(
    var local:Boolean = false
)
