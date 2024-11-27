package com.utbm.da50.freelyform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrefabFilter {
    private List<String> ids;
    private Boolean withHidden = false;
    private List<String> tags;
    private Integer page = 0; // default to first page
    private Integer size = 10; // default page size
}