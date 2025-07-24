package com.example.devjobs.jobposting.repository;

import com.example.devjobs.jobposting.entity.JobPosting;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.devjobs.jobposting.entity.QJobPosting.jobPosting;
import static com.example.devjobs.user.entity.QCompanyUser.companyUser;

public class JobPostingRepositoryImpl implements JobPostingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public JobPostingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<JobPosting> search(
            String keyword, String location, Integer minSalary, Integer maxSalary,
            Integer minExperience, Integer maxExperience, Long jobCategoryId, Pageable pageable) {

        List<JobPosting> content = queryFactory
                .selectFrom(jobPosting)
                .leftJoin(jobPosting.companyUser, companyUser).fetchJoin()
                .where(
                        keywordContains(keyword),
                        locationContains(location),
                        salaryGoe(minSalary),
                        salaryLoe(maxSalary),
                        experienceGoe(minExperience),
                        experienceLoe(maxExperience),
                        jobCategoryIdEq(jobCategoryId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(jobPosting.createDate.desc())
                .fetch();

        long total = queryFactory
                .selectFrom(jobPosting)
                .where(
                        keywordContains(keyword),
                        locationContains(location),
                        salaryGoe(minSalary),
                        salaryLoe(maxSalary),
                        experienceGoe(minExperience),
                        experienceLoe(maxExperience),
                        jobCategoryIdEq(jobCategoryId)
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? 
            jobPosting.title.containsIgnoreCase(keyword)
                .or(jobPosting.content.containsIgnoreCase(keyword))
                .or(jobPosting.companyUser.companyName.containsIgnoreCase(keyword)) : null;
    }

    private BooleanExpression locationContains(String location) {
        return StringUtils.hasText(location) ? jobPosting.workLocation.containsIgnoreCase(location) : null;
    }

    private BooleanExpression salaryGoe(Integer minSalary) {
        return minSalary != null ? jobPosting.salary.goe(minSalary) : null;
    }

    private BooleanExpression salaryLoe(Integer maxSalary) {
        return maxSalary != null ? jobPosting.salary.loe(maxSalary) : null;
    }

    private BooleanExpression experienceGoe(Integer minExperience) {
        return minExperience != null ? jobPosting.requiredExperienceYears.goe(minExperience) : null;
    }

    private BooleanExpression experienceLoe(Integer maxExperience) {
        return maxExperience != null ? jobPosting.requiredExperienceYears.loe(maxExperience) : null;
    }

    private BooleanExpression jobCategoryIdEq(Long jobCategoryId) {
        return jobCategoryId != null ? jobPosting.jobCategory.id.eq(jobCategoryId) : null;
    }
}
