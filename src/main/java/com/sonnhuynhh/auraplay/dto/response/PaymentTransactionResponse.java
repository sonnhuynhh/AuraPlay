package com.sonnhuynhh.auraplay.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionResponse {
    private UUID id;
    private BigDecimal fiatAmount;
    private Long auraReward;
    private String paymentMethod;
    private String status; // PENDING, SUCCESS, FAILED
    private String transactionCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
