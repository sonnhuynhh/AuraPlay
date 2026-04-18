package com.sonnhuynhh.auraplay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameCreateRequest {

    @NotBlank(message = "Game title cannot be blank")
    private String title;

    private String descriptionEn;
    private String descriptionVi;

    @NotNull(message = "Aura price cannot be null")
    private Long auraPrice;

    private String thumbnailUrl;

    @NotBlank(message = "Game URL cannot be blank")
    private String gameUrl;

    private Boolean isPublished;
}
