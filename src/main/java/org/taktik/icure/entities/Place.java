package org.taktik.icure.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Address;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Place extends StoredDocument implements Serializable {

    private String name;
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
