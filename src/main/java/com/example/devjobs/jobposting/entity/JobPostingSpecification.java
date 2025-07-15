package com.example.devjobs.jobposting.entity;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class JobPostingSpecification {

    /**
     * N+1 문제를 해결하기 위해 CompanyUser를 Fetch Join하는 Specification을 반환합니다.
     * @return Specification<JobPosting>
     */
    private static Specification<JobPosting> fetchCompanyUser() {
        return (root, query, criteriaBuilder) -> {
            // Count 쿼리에서는 Fetch Join을 수행하지 않도록 하여 불필요한 조인을 방지합니다.
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("companyUser", JoinType.LEFT);
            }
            return criteriaBuilder.conjunction(); // 항상 참인 조건을 반환하여 다른 조건과 AND로 연결될 수 있도록 합니다.
        };
    }

    /**
     * 키워드로 제목 또는 내용을 검색하는 Specification을 반환합니다.
     * @param keyword 검색할 키워드
     * @return Specification<JobPosting>
     */
    private static Specification<JobPosting> containsKeyword(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("content"), "%" + keyword + "%")
                );
    }

    /**
     * 근무지(location)로 검색하는 Specification을 반환합니다.
     * @param location 검색할 근무지
     * @return Specification<JobPosting>
     */
    private static Specification<JobPosting> equalsLocation(String location) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("workLocation"), location);
    }

    /**
     * 검색 조건들을 조합하여 최종 Specification을 생성합니다.
     * @param keyword 키워드
     * @param location 근무지
     * @return Specification<JobPosting>
     */
    public static Specification<JobPosting> search(String keyword, String location) {
        // 항상 참인 기본 Specification에 fetchCompanyUser를 먼저 적용합니다.
        Specification<JobPosting> spec = fetchCompanyUser();

        if (StringUtils.hasText(keyword)) {
            spec = spec.and(containsKeyword(keyword));
        }
        if (StringUtils.hasText(location)) {
            spec = spec.and(equalsLocation(location));
        }

        return spec;
    }
}
