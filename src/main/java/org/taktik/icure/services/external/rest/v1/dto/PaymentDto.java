package org.taktik.icure.services.external.rest.v1.dto;

import org.taktik.icure.entities.embed.PaymentType;

public class PaymentDto {

    private long paymentDate;
    private PaymentType paymentType;
    private Double paid;

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }
}
