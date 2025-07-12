package com.example.devjobs.jobcategory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_category")
public class JobCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

    public void updateCategoryName(String categoryName) {
        if (categoryName != null && !categoryName.isBlank()) {
            this.categoryName = categoryName;
        }
    }
}
