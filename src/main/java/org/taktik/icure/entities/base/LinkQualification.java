package org.taktik.icure.entities.base;

//narrower means that the linked codes have a narrower interpretation
//parent means that the linked code(s) is the parent of this code
//sequence means that the linked codes are a sequence of codes that are part of the current code

//When creating a link, we encourage creating single direction links. The reverse link can be found through a view
//Favour parent over child as it is better (for conflicts) to change 5 different documents once instead of changing 5 times the same document

public enum LinkQualification {
	exact, narrower, broader, approximate, sequence, parent, child, relatedCode, linkedPackage
}
