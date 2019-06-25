package org.taktik.icure.entities;


import org.taktik.icure.entities.base.StoredDocument;
import org.taktik.icure.entities.embed.Address;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MedicalLocation extends StoredDocument {
    protected String name;
    protected String description;
    protected String responsible;
    protected Boolean isGuardPost;
    protected String cbe;
    protected String bic;
    protected String bankAccount;
    protected String nihii;
    protected String ssin;
    protected Address address;
    protected List<String> agendaIds;

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

    public @Nullable Boolean getGuardPost() {
        return isGuardPost;
    }

    public void setGuardPost(Boolean guardPost) {
        isGuardPost = guardPost;
    }

    public @Nullable String getCbe() {
        return cbe;
    }

    public void setCbe(String cbe) {
        this.cbe = cbe;
    }

    public @Nullable String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public @Nullable String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public @Nullable String getNihii() {
        return nihii;
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
    }

    public @Nullable String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public List<String> getAgendaIds() { return agendaIds; }

    public void setAgendaIds(List<String> agendaIds) { this.agendaIds = agendaIds; }
}
