package org.taktik.icure.entities.embed

import com.github.pozo.KotlinBuilder
import org.taktik.icure.entities.base.CodeStub
import java.io.Serializable

@KotlinBuilder
data class AdministrationQuantity(
        val quantity: Double? = null,
        val administrationUnit: CodeStub? = null, //CD-ADMINISTRATIONUNIT
        val unit: String? = null //Should be null
) : Serializable {
    override fun toString(): String {
        return String.format("%f %s", quantity, if (administrationUnit != null) administrationUnit.code else unit)
    }
}
