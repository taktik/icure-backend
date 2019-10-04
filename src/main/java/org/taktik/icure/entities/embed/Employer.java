package org.taktik.icure.entities.embed;

import java.io.Serializable;
import java.util.Objects;

public class Employer implements Serializable {
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
        Employer employer = (Employer) o;
        return Objects.equals(name, employer.name) &&
                Objects.equals(addresse, employer.addresse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, addresse);
    }
}
