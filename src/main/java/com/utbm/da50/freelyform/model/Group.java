package com.utbm.da50.freelyform.model;

import com.utbm.da50.freelyform.dto.GroupInput;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Group {
    @Id
    private String id;
    private String name;
    private List<Field> fields;

    public boolean isFieldsEmpty() {
        return fields == null || fields.isEmpty();
    }

    public GroupInput toRest() {
        return new GroupInput(
                id, name, fields.stream().map(Field::toRest).collect(Collectors.toList())
        );
    }
}
