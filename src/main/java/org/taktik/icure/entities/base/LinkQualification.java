package org.taktik.icure.entities.base;

//narrower means that the linked codes have a narrower interpretation
//sequence means that the linked codes are a sequence of codes that are part of the current code
public enum LinkQualification {
	exact, narrower, broader, approximate, sequence
}
