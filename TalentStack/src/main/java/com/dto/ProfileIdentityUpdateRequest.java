package com.talentstack.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileIdentityUpdateRequest(
        @NotBlank @Email @Size(max = 45) String email,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @Min(0) Integer age,
        @Size(max = 45) String location,
        @Min(0) Integer profileStrength,
        @Size(max = 500) String about,
        @Size(max = 500) String skills,
        @Size(max = 45) String tagline,
        @Min(0) Integer applicationsNum,
        @Min(0) Integer interviewsNum


) {
}
