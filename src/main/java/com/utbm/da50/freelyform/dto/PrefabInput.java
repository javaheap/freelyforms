package com.utbm.da50.freelyform.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.utbm.da50.freelyform.model.Prefab;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@JsonPropertyOrder({ "id", "name", "description", "tags", "groups" })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrefabInput {
    @NonNull
    private String name;
    private String description;
    private String[] tags;
    private Boolean isActive = true;
    private List<GroupInput> groups;

    public Prefab toPrefab() {
        return Prefab.builder()
                .name(name)
                .description(description)
                .tags(tags)
                .isActive(isActive)
                .groups(groups.stream().map(GroupInput::toGroup).collect(Collectors.toList()))
                .build();
    }
}
