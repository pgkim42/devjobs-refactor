package com.example.devjobs.jobposting.service;

import com.example.devjobs.jobposting.dto.JobPostingDTO;
import com.example.devjobs.jobposting.entity.JobPosting;
import com.example.devjobs.jobposting.repository.JobPostingRepository;
import com.example.devjobs.kakaomap.service.KakaoMapService;
import com.example.devjobs.user.entity.CompanyUser;
import com.example.devjobs.user.entity.User;
import com.example.devjobs.user.repository.UserRepository;
import com.example.devjobs.util.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    private final KakaoMapService kakaoMapService;

    @Override
    public long countAllJobPostings() {
        return jobPostingRepository.count();
    }

    @Override
    public long countActiveJobPostings() {
        return jobPostingRepository.countByPostingStatus(true);
    }

    @Transactional
    @Override
    public int register(JobPostingDTO dto, MultipartFile uploadFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginId = authentication.getName();

        User user = userRepository.findByLoginId(currentLoginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (!(user instanceof CompanyUser)) {
            throw new IllegalArgumentException("채용 공고는 기업 회원만 등록할 수 있습니다.");
        }
        CompanyUser companyUser = (CompanyUser) user;

        JobPosting jobPosting = dtoToEntity(dto);
        jobPosting.setCompanyUser(companyUser);

        updateCoordinates(jobPosting);

        if (uploadFile != null && !uploadFile.isEmpty()) {
            String imgPath = fileUtil.fileUpload(uploadFile, "jobposting");
            jobPosting.setImgPath(imgPath);
            jobPosting.setImgFileName(uploadFile.getOriginalFilename());
        }

        JobPosting saved = jobPostingRepository.save(jobPosting);
        return saved.getJobCode();
    }

    @Override
    public List<JobPostingDTO> getList() {
        return jobPostingRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobPostingDTO read(Integer jobCode) {
        return jobPostingRepository.findById(jobCode)
                .map(this::entityToDto)
                .orElse(null);
    }

    @Transactional
    @Override
    public void modify(Integer jobCode, JobPostingDTO dto, MultipartFile uploadFile) {
        JobPosting entity = jobPostingRepository.findById(jobCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 JobPosting 코드가 존재하지 않습니��."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginId = authentication.getName();
        if (!entity.getCompanyUser().getLoginId().equals(currentLoginId)) {
            throw new SecurityException("작성자만 게시글을 수정할 수 있습니다.");
        }

        // DTO에서 받은 데이터로 엔티티 업데이트
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getContent() != null) entity.setContent(dto.getContent());
        if (dto.getRecruitJob() != null) entity.setRecruitJob(dto.getRecruitJob());
        if (dto.getRecruitField() != 0) entity.setRecruitField(dto.getRecruitField());
        if (dto.getSalary() != null) entity.setSalary(dto.getSalary());
        entity.setPostingStatus(dto.isPostingStatus());
        if (dto.getWorkExperience() != null) entity.setWorkExperience(dto.getWorkExperience());
        if (dto.getTag() != null) entity.setTag(dto.getTag());
        if (dto.getJobCategory() != null) entity.setJobCategory(dto.getJobCategory());
        if (dto.getSkill() != null) entity.setSkill(dto.getSkill());
        if (dto.getPostingDeadline() != null) entity.setPostingDeadline(dto.getPostingDeadline());
        entity.setUpdateDate(LocalDateTime.now());

        if (dto.getAddress() != null && !dto.getAddress().equals(entity.getAddress())) {
            entity.setAddress(dto.getAddress());
            updateCoordinates(entity);
        }

        if (uploadFile != null && !uploadFile.isEmpty()) {
            String fileName = fileUtil.fileUpload(uploadFile, "jobposting");
            entity.setImgPath(fileName);
            entity.setImgFileName(uploadFile.getOriginalFilename());
        }

        jobPostingRepository.save(entity);
    }

    @Transactional
    @Override
    public void remove(Integer jobCode) {
        JobPosting entity = jobPostingRepository.findById(jobCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 JobPosting 코드가 존재하지 않습니다."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginId = authentication.getName();
        User loggedInUser = userRepository.findByLoginId(currentLoginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        if (!entity.getCompanyUser().getLoginId().equals(currentLoginId) && !loggedInUser.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("작성자 또는 관리자만 게시글을 삭제할 수 있습니다.");
        }

        jobPostingRepository.deleteById(jobCode);
    }

    @Override
    public List<String> getCompanyNamesFromJobPostings() {
        return jobPostingRepository.findAll().stream()
                .map(jp -> jp.getCompanyUser().getCompanyName())
                .distinct()
                .collect(Collectors.toList());
    }

    private JobPosting dtoToEntity(JobPostingDTO dto) {
        return JobPosting.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .recruitJob(dto.getRecruitJob())
                .recruitField(dto.getRecruitField())
                .salary(dto.getSalary())
                .postingDeadline(dto.getPostingDeadline())
                .postingStatus(dto.isPostingStatus())
                .workExperience(dto.getWorkExperience())
                .tag(dto.getTag())
                .jobCategory(dto.getJobCategory())
                .skill(dto.getSkill())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    private JobPostingDTO entityToDto(JobPosting entity) {
        CompanyUser company = entity.getCompanyUser();
        return JobPostingDTO.builder()
                .jobCode(entity.getJobCode())
                .title(entity.getTitle())
                .content(entity.getContent())
                .recruitJob(entity.getRecruitJob())
                .recruitField(entity.getRecruitField())
                .salary(entity.getSalary())
                .postingDate(entity.getCreateDate())
                .postingDeadline(entity.getPostingDeadline())
                .postingStatus(entity.isPostingStatus())
                .workExperience(entity.getWorkExperience())
                .tag(entity.getTag())
                .jobCategory(entity.getJobCategory())
                .imgFileName(entity.getImgFileName())
                .imgPath(entity.getImgPath())
                .skill(entity.getSkill())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .userCode(String.valueOf(company.getId()))
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .build();
    }

    private void updateCoordinates(JobPosting jobPosting) {
        if (jobPosting.getAddress() != null) {
            try {
                String coordinatesJson = kakaoMapService.getCoordinates(jobPosting.getAddress());
                Map<String, Object> coordinates = parseCoordinates(coordinatesJson);
                jobPosting.setLatitude((Double) coordinates.get("latitude"));
                jobPosting.setLongitude((Double) coordinates.get("longitude"));
            } catch (Exception e) {
                jobPosting.setLatitude(null);
                jobPosting.setLongitude(null);
            }
        }
    }

    private Map<String, Object> parseCoordinates(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> result = new HashMap<>();
        try {
            JsonNode node = objectMapper.readTree(json);
            result.put("latitude", node.get("documents").get(0).get("y").asDouble());
            result.put("longitude", node.get("documents").get(0).get("x").asDouble());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
