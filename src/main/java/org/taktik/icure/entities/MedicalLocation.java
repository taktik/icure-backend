package org.taktik.icure.entities;


import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Address;

public class MedicalLocation extends StoredDocument {
    protected String name;
    protected String description;
    protected String responsible;

    protected Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }
}
