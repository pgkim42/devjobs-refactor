package com.example.devjobs.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_users")
@DiscriminatorValue("ADMIN")
public class AdminUser extends User {
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "admin_level")
    private Integer adminLevel;
}