package org.taktik.icure.services.external.rest.v1.dto.embed;

import org.taktik.icure.entities.embed.Address;

import java.io.Serializable;
import java.util.Objects;

public class EmployerDto implements Serializable {
    private String name;
    private Address addresse;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Address getAddresse() { return addresse; }

    public void setAddresse(Address addresse) { this.addresse = addresse; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployerDto employer = (EmployerDto) o;
        return Objects.equals(name, employer.name) &&
                Objects.equals(addresse, employer.addresse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, addresse);
    }
}
