package org.taktik.icure.entities.base

import org.taktik.icure.entities.Document
import org.taktik.icure.entities.embed.DataAttachment

/**
 * Interface for entities which store part of their data as attachments.
 */
interface HasDataAttachments : StoredDocument {
	/**
	 * All data attachments associated to this entity. The key in this map represents the key of the attachment.
	 * By convention, in entities where there is a main attachment (e.g. [Document]) the key of the main attachment
	 * should be equal to the entity id, in order to avoid accidental collisions.
	 */
	val dataAttachments: Map<String, DataAttachment>
}
