/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils.beans;

import de.danielbechler.diff.node.Node;

/**
 * Defines the interface implemented by classes used by custom annotations for merging beans. <br>
 * Classes implementing this interface can be used by the {@link org.taktik.icure.utils.beans.annotations.ImplementedBy} annotation to provide the
 * merging logic.
 *
 * @see MergeLogic
 */
public interface MergeLogic {
	/**
	 * Merges the leader instance into the receiver instance. <br>
	 * The specified node objects support the process by providing the differences between the leader, the receiver
	 * and the original instances.<br>
	 */
	<T> void merge(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode);
}
