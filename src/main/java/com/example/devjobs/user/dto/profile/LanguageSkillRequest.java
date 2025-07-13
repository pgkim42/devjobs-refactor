package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.enums.Proficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageSkillRequest {

    @NotBlank(message = "Language is required")
    @Size(max = 50)
    private String language;

    @NotNull(message = "Proficiency is required")
    private Proficiency proficiency;
}
