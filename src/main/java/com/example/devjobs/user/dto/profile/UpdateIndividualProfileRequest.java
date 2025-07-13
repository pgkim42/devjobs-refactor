package com.example.devjobs.user.dto.profile;

import com.example.devjobs.user.entity.enums.WorkStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIndividualProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 50)
    private String name;

    @Size(max = 20)
    private String phoneNumber;

    private String address;

    @URL
    private String portfolioUrl;

    @Size(max = 200)
    private String headline;

    private WorkStatus workStatus;
}
