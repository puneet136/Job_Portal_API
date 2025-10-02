package com.api.Job_Portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;

    // Getters and Setters

    // Constructors
    public CategoryDTO() {}

    public CategoryDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

