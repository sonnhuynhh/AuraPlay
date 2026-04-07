package com.sonnhuynhh.auraplay.entity;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOwnedGameId implements Serializable {
    private UUID userId;
    private Integer gameId;
}
