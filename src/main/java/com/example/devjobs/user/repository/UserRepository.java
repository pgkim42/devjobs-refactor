package com.example.devjobs.user.repository;

import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.entity.IndividualUser;
import com.example.devjobs.user.entity.CompanyUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM IndividualUser u")
    Page<IndividualUser> findAllIndividualUsers(Pageable pageable);
    
    @Query("SELECT u FROM IndividualUser u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.loginId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<IndividualUser> findIndividualUsersBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM CompanyUser u")
    Page<CompanyUser> findAllCompanyUsers(Pageable pageable);
    
    @Query("SELECT u FROM CompanyUser u WHERE " +
           "LOWER(u.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.loginId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CompanyUser> findCompanyUsersBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM IndividualUser u WHERE u.id = :userId")
    Optional<IndividualUser> findIndividualUserById(@Param("userId") Long userId);
    
    @Query("SELECT u FROM CompanyUser u WHERE u.id = :userId")
    Optional<CompanyUser> findCompanyUserById(@Param("userId") Long userId);
}
