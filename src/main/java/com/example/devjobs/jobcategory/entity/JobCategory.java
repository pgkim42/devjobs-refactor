package com.example.devjobs.jobcategory.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_category")
public class JobCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_code")
    private Integer categoryCode;

    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;

}
