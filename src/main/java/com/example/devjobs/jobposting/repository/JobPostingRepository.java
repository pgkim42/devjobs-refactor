package com.example.devjobs.jobposting.repository;

import com.example.devjobs.jobposting.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    /**
     * N+1 문제를 해결하기 위해 Fetch Join을 사용하여 채용 공고와 회사 정보를 함께 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 페이징 처리된 채용 공고 목록 (회사 정보 포함)
     */
    @Query(value = "SELECT jp FROM JobPosting jp JOIN FETCH jp.companyUser",
           countQuery = "SELECT COUNT(jp) FROM JobPosting jp")
    Page<JobPosting> findAllWithCompanyUser(Pageable pageable);
}
