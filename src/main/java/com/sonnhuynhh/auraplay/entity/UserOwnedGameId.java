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
    // serialVersionUID là mã định danh của phiên bản lớp
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private Integer gameId;
}
