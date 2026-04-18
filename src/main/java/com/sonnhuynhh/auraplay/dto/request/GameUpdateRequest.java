package com.sonnhuynhh.auraplay.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameUpdateRequest {
    private String title;
    private String descriptionEn;
    private String descriptionVi;
    private Long auraPrice;
    private String thumbnailUrl;
    private String gameUrl;
    private Boolean isPublished;
}
