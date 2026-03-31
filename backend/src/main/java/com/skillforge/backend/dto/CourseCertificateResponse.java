package com.skillforge.backend.dto;

import java.time.LocalDateTime;

public class CourseCertificateResponse {

    private final Long id;
    private final String studentName;
    private final String studentEmail;
    private final String topic;
    private final String certificateCode;
    private final Integer completedLessons;
    private final Integer totalLessons;
    private final LocalDateTime issuedAt;

    public CourseCertificateResponse(Long id,
                                     String studentName,
                                     String studentEmail,
                                     String topic,
                                     String certificateCode,
                                     Integer completedLessons,
                                     Integer totalLessons,
                                     LocalDateTime issuedAt) {
        this.id = id;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.topic = topic;
        this.certificateCode = certificateCode;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getTopic() {
        return topic;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public Integer getCompletedLessons() {
        return completedLessons;
    }

    public Integer getTotalLessons() {
        return totalLessons;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}
