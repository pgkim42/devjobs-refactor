package com.example.devjobs.application.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationRequestDTO(
    @NotNull Long jobPostingId
) {}
