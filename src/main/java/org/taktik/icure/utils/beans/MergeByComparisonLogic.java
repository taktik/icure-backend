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
import org.taktik.icure.utils.beans.annotations.MergeByComparison;

public class MergeByComparisonLogic implements MergeLogic {

	@Override
	public <T> void merge(T original, T leader, T receiver, Node leaderOriginalNode, Node receiverOriginalNode, Node leaderReceiverNode) {

		if (Comparable.class.isAssignableFrom(Nodes.getTypeOrWrappedPrimitiveType(leaderReceiverNode))) {

			Object mergeReceiverValue = leaderReceiverNode.get(receiver);
			Object mergeRequesterValue = leaderReceiverNode.get(leader);

			Comparable mergeRequesterComparableValue = (Comparable) mergeRequesterValue;
			Comparable mergeReceiverComparableValue = (Comparable) mergeReceiverValue;

			MergeByComparison mergeByComparisonAnnotation = leaderReceiverNode.getPropertyAnnotation(MergeByComparison.class);
			if (mergeByComparisonAnnotation.retains().equals(MergeByComparison.Retains.GREATER)) {
				//noinspection unchecked
				if (mergeRequesterComparableValue.compareTo(mergeReceiverComparableValue) > 0) {
					leaderReceiverNode.set(receiver, mergeRequesterValue);
				}
			} else {
				//noinspection unchecked
				if (mergeRequesterComparableValue.compareTo(mergeReceiverComparableValue) < 0) {
					leaderReceiverNode.set(receiver, mergeRequesterValue);
				}
			}

		} else {
			throw new RuntimeException("Value of type " + leaderReceiverNode.getType() + " does not implement Comparable");
		}
	}
}
