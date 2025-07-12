package com.example.devjobs.application.repository;

import com.example.devjobs.application.entity.Application;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.user.entity.IndividualUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByJobPostingAndIndividualUser(JobPosting jobPosting, IndividualUser individualUser);
    List<Application> findByIndividualUser(IndividualUser individualUser);
    List<Application> findByJobPosting(JobPosting jobPosting);
}