package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.CodeStub;

import java.util.HashSet;
import java.util.Set;

public class Periodicity{
    protected Set<CodeStub> relatedCode = new HashSet<>();
    protected Set<CodeStub> periodicity = new HashSet<>();
}
