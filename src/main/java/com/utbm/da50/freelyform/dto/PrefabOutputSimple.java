package com.utbm.da50.freelyform.dto;

import com.utbm.da50.freelyform.model.Prefab;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrefabOutputSimple implements PrefabOutput{
    private String id;
    private String name;
    private String description;
    private String[] tags;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updateAd;
}
