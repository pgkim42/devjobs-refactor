package com.example.devjobs.jobposting.entity.enums;

public enum JobPostingStatus {
    ACTIVE("활성"),      // 활성 상태 (지원 가능)
    CLOSED("마감"),      // 마감 상태 (기한 종료)
    CANCELLED("취소"),   // 취소됨
    FILLED("채용완료");  // 채용 완료
    
    private final String description;
    
    JobPostingStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}