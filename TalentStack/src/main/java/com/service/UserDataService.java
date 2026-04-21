package com.talentstack.api.service;

import com.talentstack.api.dto.ProfileResponse;
import com.talentstack.api.dto.ProfileIdentityUpdateRequest;
import com.talentstack.api.dto.JobPreferencesRequest;
import com.talentstack.api.dto.JobPreferencesResponse;
import com.talentstack.api.model.User;
import com.talentstack.api.model.UserProfile;
import com.talentstack.api.repo.UserProfileRepository;
import com.talentstack.api.repo.UserRepository;
import com.talentstack.api.util.InputSanitizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class UserDataService {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public UserDataService(UserRepository userRepo, UserProfileRepository profileRepo) {
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    public ProfileResponse getProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        UserProfile profile = profileRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));

        return toProfileResponse(user, profile);
    }

    public JobPreferencesResponse getJobPreferences(Long userId) {
        UserProfile profile = profileRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));

        return toJobPreferencesResponse(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, ProfileIdentityUpdateRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        UserProfile profile = profileRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));


        user.setEmail(InputSanitizer.sanitizeEmail(request.email()));

        profile.setFirstName(InputSanitizer.sanitizeName(request.firstName()));
        profile.setLastName(InputSanitizer.sanitizeName(request.lastName()));
        profile.setAge(request.age());
        profile.setLocation(normalizeOptionalText(request.location()));
        profile.setProfileStrength(request.profileStrength());
        profile.setAbout(normalizeOptionalText(request.about()));
        profile.setSkills(normalizeOptionalText(request.skills()));
        profile.setTagline(normalizeOptionalText(request.tagline()));
        profile.setApplicationsNum(request.applicationsNum());
        profile.setInterviewsNum(request.interviewsNum());

        userRepo.save(user);
        profileRepo.save(profile);

        return toProfileResponse(user, profile);
    }

    @Transactional
    public JobPreferencesResponse updateJobPreferences(Long userId, JobPreferencesRequest request) {
        UserProfile profile = profileRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));

        validateSalaryRange(request.salaryMin(), request.salaryMax());

        profile.setKeyWords(request.keyWords());
        profile.setLocation(request.location());
        profile.setDistance(request.distance());
        profile.setSalaryMin(request.salaryMin());
        profile.setSalaryMax(request.salaryMax());
        profile.setContractType(request.contractType());
        profile.setMaxDaysOld(request.maxDaysOld());

        profileRepo.save(profile);

        return toJobPreferencesResponse(profile);
    }

    private ProfileResponse toProfileResponse(User user, UserProfile profile) {
        return new ProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getCreatedAt(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getAge(),
                profile.getKeyWords(),
                profile.getLocation(),
                profile.getDistance(),
                profile.getSalaryMin(),
                profile.getSalaryMax(),
                profile.getContractType(),
                profile.getMaxDaysOld(),
                profile.getProfileStrength(),
                profile.getAbout(),
                profile.getSkills(),
                profile.getTagline(),
                profile.getApplicationsNum(),
                profile.getInterviewsNum(),
                profile.getProfilePhoto() == null ? null : Base64.getEncoder().encodeToString(profile.getProfilePhoto())
        );
    }

    private JobPreferencesResponse toJobPreferencesResponse(UserProfile profile) {
        return new JobPreferencesResponse(
                profile.getKeyWords(),
                profile.getLocation(),
                profile.getDistance(),
                profile.getSalaryMin(),
                profile.getSalaryMax(),
                profile.getContractType(),
                profile.getMaxDaysOld()
        );
    }

    private void validateSalaryRange(Integer min, Integer max) {
        if (min == null || max == null) return;

        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Salary must be non-negative");
        }

        if (min > max) {
            throw new IllegalArgumentException("salaryMin cannot be greater than salaryMax");
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim().replaceAll("\\s+", " ");
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Transactional
    public ProfileResponse updateProfilePhoto(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile photo is required.");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        UserProfile profile = profileRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + userId));

        try {
            byte[] converted = convertImage(file.getBytes());
            profile.setProfilePhoto(converted);
            profileRepo.save(profile);
            return toProfileResponse(user, profile);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to process profile image.");
        }
    }

    private byte[] convertImage(byte[] sourceBytes) throws IOException {
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(sourceBytes));
        if (source == null) {
            throw new IllegalArgumentException("Uploaded file must be a valid image.");
        }

        final int maxWidth = 512;
        final int maxHeight = 512;
        int width = source.getWidth();
        int height = source.getHeight();

        double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
        if (ratio > 1.0) {
            ratio = 1.0;
        }

        int targetWidth = Math.max(1, (int) Math.round(width * ratio));
        int targetHeight = Math.max(1, (int) Math.round(height * ratio));

        BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = target.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(target, "jpg", output);
        return output.toByteArray();
    }
}
