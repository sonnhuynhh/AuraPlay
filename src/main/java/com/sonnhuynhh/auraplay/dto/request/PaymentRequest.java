package com.sonnhuynhh.auraplay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentRequest {

    @NotNull(message = "Package ID is required")
    private Integer packageId;

    @NotBlank(message = "Currency is required")
    private String currency; // VND, USD

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // Momo, ZaloPay, VNPay
}
