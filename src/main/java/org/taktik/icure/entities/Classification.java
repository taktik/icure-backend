
package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.validation.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Classification extends ClassificationTemplate {

    @NotNull
    protected String templateId;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
