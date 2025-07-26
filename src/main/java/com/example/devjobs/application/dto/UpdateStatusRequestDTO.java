package com.example.devjobs.application.dto;

import com.example.devjobs.application.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequestDTO(
    @NotNull ApplicationStatus status
) {}
