package com.example.devjobs.jobposting.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingDTO {

    private Integer jobCode;
    private String title;
    private String content;
    private String recruitJob;
    private int recruitField;
    private String salary;
    private LocalDateTime postingDate;
    private LocalDateTime postingDeadline;
    private boolean postingStatus;
    private Integer workExperience;
    private String tag;
    private String jobCategory;
    private MultipartFile uploadFile;
    private String imgFileName;
    private String imgPath;
    private String companyName;
    private String companyAddress;
    private Integer companyProfileCode;
    private String skill;
    private String userCode;
    private String address;
    private Double latitude;
    private Double longitude;

}
