package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.embed.ContentDto;

import java.util.List;

public class ArticleDto extends IcureDto {

    private String name;
    private List<ContentDto> content;
    private String classification;

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ContentDto> getContent() {
        return content;
    }

    public void setContent(List<ContentDto> content) {
        this.content = content;
    }
}
