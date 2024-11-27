package com.utbm.da50.freelyform.dto;

import com.utbm.da50.freelyform.model.Group;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInput {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private List<FieldInput> fields;

    public Group toGroup(){
        return new Group(
                id,
                name,
                fields.stream().map(FieldInput::toField).collect(Collectors.toList())
        );
    }
}