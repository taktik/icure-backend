package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto;

import java.util.List;

public class MedicalLocationDto extends StoredDto {
    protected String name;
    protected String description;
    protected AddressDto address;
    protected Boolean isGuardPost;
    protected String cbe;
    protected String bic;
    protected String bankAccount;
    protected String nihii;
    protected String ssin;
    protected String responsible;
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

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public Boolean getGuardPost() {
        return isGuardPost;
    }

    public void setGuardPost(Boolean guardPost) {
        isGuardPost = guardPost;
    }

    public String getCbe() {
        return cbe;
    }

    public void setCbe(String cbe) {
        this.cbe = cbe;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getNihii() {
        return nihii;
    }

    public void setNihii(String nihii) {
        this.nihii = nihii;
    }

    public String getSsin() {
        return ssin;
    }

    public void setSsin(String ssin) {
        this.ssin = ssin;
    }

    public List<String> getAgendaIds() { return agendaIds; }

    public void setAgendaIds(List<String> agendaIds) { this.agendaIds = agendaIds; }
}
