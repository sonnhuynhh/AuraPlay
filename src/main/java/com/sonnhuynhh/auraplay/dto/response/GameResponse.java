package com.sonnhuynhh.auraplay.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    private Integer id;
    private String title;
    private String descriptionEn;
    private String descriptionVi;
    private Long auraPrice;
    private String thumbnailUrl;
    private String gameUrl;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
