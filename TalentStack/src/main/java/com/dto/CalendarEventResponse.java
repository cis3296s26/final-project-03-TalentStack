package com.talentstack.api.dto;

import java.time.LocalDateTime;

public record CalendarEventResponse(
        String eventType,
        LocalDateTime eventAt,
        Long savedJobId,
        String title,
        String company,
        String applicationStatus,
        String detail

) {
}