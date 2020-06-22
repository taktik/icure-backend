package org.taktik.icure.entities

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder
import org.ektorp.Attachment
import org.taktik.icure.entities.base.Named
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Address
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

@KotlinBuilder
data class MedicalLocation(
        @JsonProperty("_id") override val id: String,
        @JsonProperty("_rev") override val rev: String? = null,
        @JsonProperty("deleted") override val deletionDate: Long? = null,

        override val name: String? = null,
        val description: String? = null,
        val responsible: String? = null,
        val guardPost: Boolean? = null,
        val cbe: String? = null,
        val bic: String? = null,
        val bankAccount: String? = null,
        val nihii: String? = null,
        val ssin: String? = null,
        val address: Address? = null,
        val agendaIds: Set<String> = setOf(),

        @JsonProperty("_attachments") override val attachments: Map<String, Attachment>? = null,
        @JsonProperty("_revs_info") override val revisionsInfo: List<RevisionInfo>? = null,
        @JsonProperty("_conflicts") override val conflicts: List<String>? = null,
        @JsonProperty("rev_history") override val revHistory: Map<String, String>? = null,
        @JsonProperty("java_type") override val _type: String = MedicalLocation::class.qualifiedName!!
) : StoredDocument, Named {
    companion object : DynamicInitializer<MedicalLocation>

    fun merge(other: MedicalLocation) = MedicalLocation(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: MedicalLocation) = super.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "description" to (this.description ?: other.description),
            "responsible" to (this.responsible ?: other.responsible),
            "guardPost" to (this.guardPost ?: other.guardPost),
            "cbe" to (this.cbe ?: other.cbe),
            "bic" to (this.bic ?: other.bic),
            "bankAccount" to (this.bankAccount ?: other.bankAccount),
            "nihii" to (this.nihii ?: other.nihii),
            "ssin" to (this.ssin ?: other.ssin),
            "address" to (this.address ?: other.address),
            "agendaIds" to (other.agendaIds + this.agendaIds)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
