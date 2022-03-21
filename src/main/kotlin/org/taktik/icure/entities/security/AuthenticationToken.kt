package org.taktik.icure.entities.security

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.pozo.KotlinBuilder
import org.taktik.icure.utils.InstantDeserializer
import org.taktik.icure.utils.InstantSerializer
import org.taktik.icure.utils.between
import java.io.Serializable
import java.time.Instant

/**
 * Token used for inter-applications authentication. Always as a period of validity before to expire
 * @property token Encrypted token
 * @property creationTime Validity starting time of the token
 * @property validity Token validity in seconds. If no validity is passed, then the token never expires. (Retro compatibility for applicationTokens)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class AuthenticationToken(
        val token: String,

        @JsonSerialize(using = InstantSerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonDeserialize(using = InstantDeserializer::class)
        val creationTime: Instant = Instant.now(),

        val validity: Long = 3600
) : Cloneable, Serializable {
    companion object {
        const val LONG_LIVING_TOKEN_VALIDITY = -1L
    }

    @JsonIgnore
    fun isExpired() : Boolean = if (validity == LONG_LIVING_TOKEN_VALIDITY) false else !Instant.now().between(creationTime, creationTime.plusSeconds(validity))
}

