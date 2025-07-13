package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.LanguageSkill;
import com.example.devjobs.user.entity.enums.Proficiency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageSkillDto {
    private Long id;
    private String language;
    private Proficiency proficiency;

    public static LanguageSkillDto fromEntity(LanguageSkill languageSkill) {
        return LanguageSkillDto.builder()
                .id(languageSkill.getId())
                .language(languageSkill.getLanguage())
                .proficiency(languageSkill.getProficiency())
                .build();
    }
}
