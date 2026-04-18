package com.sonnhuynhh.auraplay.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserUpdateRequest {
    // Có thể null nếu không muốn đổi
    private String username;
    
    // Có thể null nếu không muốn đổi
    private String email;
}
