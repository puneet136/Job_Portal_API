package com.api.Job_Portal.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter@Setter
public class JobPostDTO {

    private String title;
    private String description;
    private String location = "Not_Specified";
    private String skills;
    private Double salary;
    private String employerName;
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;


    // Constructors
    public JobPostDTO() {}

    public JobPostDTO(Long id, String title, String description, String location, String skills, Double salary,
                      String employerName, Long categoryId, String categoryName, String categoryDescription) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.skills = skills;
        this.salary = salary;
        this.employerName = employerName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }
}

