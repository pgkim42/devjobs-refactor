package com.example.devjobs.application.dto;

import com.example.devjobs.application.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequestDTO {

    @NotNull
    private ApplicationStatus status;
}
