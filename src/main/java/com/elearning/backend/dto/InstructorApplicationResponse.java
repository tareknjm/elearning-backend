package com.elearning.backend.dto;

import com.elearning.backend.model.InstructorApplication;
import lombok.Data;

@Data
public class InstructorApplicationResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String speciality;
    private String experience;
    private String motivation;
    private String linkedinUrl;
    private String educationLevel;
    private String status;
    private String adminNote;
    private String appliedAt;
    private String reviewedAt;

    public static InstructorApplicationResponse fromEntity(
            InstructorApplication app) {
        InstructorApplicationResponse r = new InstructorApplicationResponse();
        r.setId(app.getId());
        r.setFullName(app.getFullName());
        r.setEmail(app.getEmail());
        r.setPhone(app.getPhone());
        r.setSpeciality(app.getSpeciality());
        r.setExperience(app.getExperience());
        r.setMotivation(app.getMotivation());
        r.setLinkedinUrl(app.getLinkedinUrl());
        r.setEducationLevel(app.getEducationLevel());
        r.setStatus(app.getStatus().name());
        r.setAdminNote(app.getAdminNote());
        r.setAppliedAt(app.getAppliedAt() != null
                ? app.getAppliedAt().toLocalDate().toString() : "");
        r.setReviewedAt(app.getReviewedAt() != null
                ? app.getReviewedAt().toLocalDate().toString() : "");
        return r;
    }
}