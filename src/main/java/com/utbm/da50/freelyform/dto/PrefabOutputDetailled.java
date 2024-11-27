package com.utbm.da50.freelyform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrefabOutputDetailled extends PrefabInput implements PrefabOutput{
    private String id;
    private Boolean isAlreadyAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PrefabOutputDetailled(String id, String name, String description,
                                 LocalDateTime createdAt, LocalDateTime updatedAt,
                                 String[] tags, List<GroupInput> groups, Boolean isActive, Boolean isAlreadyAnswered) {
        super(name, description, tags, isActive, groups);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAlreadyAnswered = isAlreadyAnswered;
    }
}
