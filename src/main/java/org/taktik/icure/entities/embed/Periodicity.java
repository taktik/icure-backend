package org.taktik.icure.entities.embed;

import org.taktik.icure.entities.base.CodeStub;
import org.taktik.icure.validation.AutoFix;
import org.taktik.icure.validation.ValidCode;

import java.io.Serializable;

public class Periodicity implements Serializable {
    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected CodeStub relatedCode;
    @ValidCode(autoFix = AutoFix.NORMALIZECODE)
    protected CodeStub relatedPeriodicity;

    public CodeStub getRelatedCode() { return relatedCode; }

    public void setRelatedCode(CodeStub relatedCode) { this.relatedCode = relatedCode; }

    public CodeStub getRelatedPeriodicity() { return relatedPeriodicity; }

    public void setRelatedPeriodicity(CodeStub relatedPeriodicity) { this.relatedPeriodicity = relatedPeriodicity; }
}
