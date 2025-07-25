package com.example.devjobs.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDTO {

    @NotNull
    private Long jobPostingId;
}
