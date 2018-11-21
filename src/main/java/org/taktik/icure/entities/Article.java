package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredICureDocument;
import org.taktik.icure.entities.embed.Content;
import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article extends StoredICureDocument {

    private String name;
    private List<Content> content;
    private String classification;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
