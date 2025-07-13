package com.example.devjobs.user.repository;

import com.example.devjobs.user.entity.IndividualUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualUserRepository extends JpaRepository<IndividualUser, Long> {
}
