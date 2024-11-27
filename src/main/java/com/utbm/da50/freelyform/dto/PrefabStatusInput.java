package com.utbm.da50.freelyform.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonPropertyOrder({ "active" })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrefabStatusInput {
    private Boolean active = true;
}
