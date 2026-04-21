package com.talentstack.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UpdateInterviewDateRequest(
        @NotNull LocalDateTime interviewAt
) {
}