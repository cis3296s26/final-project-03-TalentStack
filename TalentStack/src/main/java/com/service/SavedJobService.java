package com.talentstack.api.service;

import com.talentstack.api.dto.CalendarEventResponse;
import com.talentstack.api.dto.SaveJobRequest;
import com.talentstack.api.dto.SavedJobResponse;
import com.talentstack.api.model.SavedJob;
import com.talentstack.api.model.User;
import com.talentstack.api.repo.SavedJobRepository;
import com.talentstack.api.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class SavedJobService {

    private static final String DEFAULT_STATUS = "SAVED";

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;

    public SavedJobService(SavedJobRepository savedJobRepository, UserRepository userRepository) {
        this.savedJobRepository = savedJobRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SavedJobResponse saveJob(Long userId, SaveJobRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        SavedJob savedJob = findExisting(userId, request.id()).orElseGet(SavedJob::new);
        savedJob.setUser(user);
        savedJob.setExternalJobId(request.id());
        savedJob.setTitle(request.title().trim());
        savedJob.setCompany(trimToNull(request.company()));
        savedJob.setLocation(trimToNull(request.location()));
        savedJob.setRedirectUrl(trimToNull(request.redirectUrl()));
        savedJob.setSource(request.source().trim());

        if (savedJob.getApplicationStatus() == null) {
            savedJob.setApplicationStatus(DEFAULT_STATUS);
        }
        if (request.applicationStatus() != null && !request.applicationStatus().isBlank()) {
            savedJob.setApplicationStatus(request.applicationStatus().trim());
        }
        savedJob.setStatusUpdatedAt(LocalDateTime.now());
        SavedJob persisted = savedJobRepository.save(savedJob);
        return toResponse(persisted);
    }

    public List<SavedJobResponse> getSavedJobs(Long userId) {
        return savedJobRepository.findByUserUserIdOrderBySavedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SavedJobResponse updateApplicationStatus(Long userId, Long savedJobId, String applicationStatus) {
        SavedJob savedJob = savedJobRepository.findById(savedJobId)
                .orElseThrow(() -> new IllegalArgumentException("Saved job not found: " + savedJobId));

        if (!savedJob.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Saved job not found: " + savedJobId);
        }

        savedJob.setApplicationStatus(applicationStatus.trim());
        savedJob.setStatusUpdatedAt(LocalDateTime.now());
        return toResponse(savedJobRepository.save(savedJob));
    }

    @Transactional
    public SavedJobResponse updateInterviewDate(Long userId, Long savedJobId, LocalDateTime interviewAt) {
        SavedJob savedJob = savedJobRepository.findById(savedJobId)
                .orElseThrow(() -> new IllegalArgumentException("Saved job not found: " + savedJobId));

        if (!savedJob.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Saved job not found: " + savedJobId);
        }

        savedJob.setInterviewAt(interviewAt);
        return toResponse(savedJobRepository.save(savedJob));
    }

    public List<CalendarEventResponse> getCalendarEvents(Long userId) {
        List<SavedJob> jobs = savedJobRepository.findByUserUserIdOrderBySavedAtDesc(userId);
        List<CalendarEventResponse> events = new ArrayList<>();

        for (SavedJob job : jobs) {
            events.add(new CalendarEventResponse(
                    "JOB_SAVED",
                    job.getSavedAt(),
                    job.getSavedJobId(),
                    job.getTitle(),
                    job.getCompany(),
                    job.getApplicationStatus(),
                    "Job saved"
            ));

            if (job.getStatusUpdatedAt() != null && !job.getStatusUpdatedAt().equals(job.getSavedAt())) {
                events.add(new CalendarEventResponse(
                        "STATUS_CHANGED",
                        job.getStatusUpdatedAt(),
                        job.getSavedJobId(),
                        job.getTitle(),
                        job.getCompany(),
                        job.getApplicationStatus(),
                        "Status changed to " + job.getApplicationStatus()
                ));
            }

            if (job.getInterviewAt() != null) {
                events.add(new CalendarEventResponse(
                        "INTERVIEW",
                        job.getInterviewAt(),
                        job.getSavedJobId(),
                        job.getTitle(),
                        job.getCompany(),
                        job.getApplicationStatus(),
                        "Interview scheduled"
                ));
            }
        }

        events.sort(Comparator.comparing(CalendarEventResponse::eventAt));
        return events;
    }

    private java.util.Optional<SavedJob> findExisting(Long userId, String externalJobId) {
        if (externalJobId == null || externalJobId.isBlank()) {
            return java.util.Optional.empty();
        }
        return savedJobRepository.findByUserUserIdAndExternalJobId(userId, externalJobId.trim());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private SavedJobResponse toResponse(SavedJob savedJob) {
        return new SavedJobResponse(
                savedJob.getSavedJobId(),
                savedJob.getExternalJobId(),
                savedJob.getTitle(),
                savedJob.getCompany(),
                savedJob.getLocation(),
                savedJob.getRedirectUrl(),
                savedJob.getSource(),
                savedJob.getApplicationStatus(),
                savedJob.getSavedAt(),
                savedJob.getStatusUpdatedAt(),
                savedJob.getInterviewAt()
        );
    }
}