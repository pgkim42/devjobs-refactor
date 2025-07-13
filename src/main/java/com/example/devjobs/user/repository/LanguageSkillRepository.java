package com.example.devjobs.user.repository;

import com.example.devjobs.user.entity.LanguageSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageSkillRepository extends JpaRepository<LanguageSkill, Long> {
}
