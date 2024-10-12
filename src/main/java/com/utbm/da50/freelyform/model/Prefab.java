package com.utbm.da50.freelyform.model;

import com.utbm.da50.freelyform.dto.PrefabOutput;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Make the all-args constructor private
@NoArgsConstructor
@Document(collection = "prefabs")
@Builder
public class Prefab {
    @Id
    private String id;
    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    private String[] tags;
    @Setter
    private List<Group> groups;

    @CreatedDate
    @Setter
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Prefab(String name, String description, String[] tags, List<Group> groups) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.groups = groups;
    }

    public PrefabOutput toRest() {
        return new PrefabOutput(
                id,
                name,
                description,
                createdAt,
                updatedAt,
                tags,
                groups.stream().map(Group::toRest).collect(Collectors.toList())
        );
    }
}
