package com.elearning.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InstructorApplicationRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String speciality;

    @NotBlank
    private String experience;

    @NotBlank
    private String motivation;

    private String linkedinUrl;

    @NotBlank
    private String educationLevel;
}