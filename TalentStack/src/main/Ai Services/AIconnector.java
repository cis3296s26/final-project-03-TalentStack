package com.talentstack.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.talentstack.dto.InterviewScheduleRequest;
import com.talentstack.service.InterviewScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class AIconnector {

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<JsonNode> getSchedule(@RequestBody InterviewScheduleRequest request) {
        JsonNode schedule = interviewScheduleService.generateInterviewSchedule(
                request.getJobTitle(),
                request.getCompany(),
                request.getInterviewDate(),
                request.getSkills(),
                request.getExperienceLevel()
        );
        return ResponseEntity.ok(schedule);
    }
}

